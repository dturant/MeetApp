package com.example.dagna.meetapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.dagna.meetapp.helpers.FilterDialog;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, FilterDialog.FilterDialogListener {


    private GoogleMap mMap;
    private int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public final static String EXTRA_MESSAGE = "com.example.dagna.meetapp.MESSAGE";

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private int REQUEST_CODE = 1;

    public String userID;

    List seletedItems=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
        userID = sharedPref.getString("userID", null);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialog filterDialog = new FilterDialog();
                filterDialog.show(getSupportFragmentManager(), "FilterDialogFragment");
            }
        });


        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users").child(userID);


        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TextView tv = (TextView) findViewById(R.id.user_name);
                tv.setText(dataSnapshot.child("nome").getValue().toString());

                TextView tvMail = (TextView) findViewById(R.id.user_email);
                tvMail.setText(dataSnapshot.child("email").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        seletedItems = FilterDialog.getSeletedItems();
        Log.d("selected items", seletedItems.toString());
        if(seletedItems.size()>0) {
            mMap.clear();

            if (seletedItems.contains(1)) { //your events
                Query query = mFirebaseDatabase.orderByChild("owner").equalTo(userID);
                Log.d("LOL1", "LOL");
                query.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        getResultsFromFirebase(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            if (seletedItems.contains(0)) {
                mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        getResultsFromFirebase(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }



    }

    private void getResultsFromFirebase(DataSnapshot dataSnapshot){
        Log.d("LOL", "LOL");
        if (dataSnapshot.exists()) {
            Log.d("LOL2", "LOL");
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                HashMap<String, Object> markerHashMap = (HashMap<String, Object>) snapshot.getValue();
                try {
                    Date markerDay = new SimpleDateFormat("dd/MM/yyyy").parse(markerHashMap.get("date").toString());

                    if( new Date().before(markerDay)){
                        HashMap<String, Double> markerLoc = (HashMap<String, Double>) markerHashMap.get("location");

                        MarkerOptions markerOptions = new MarkerOptions().title(snapshot.getKey()).position(new LatLng(markerLoc.get("latitude"),markerLoc.get("longitude")));
                        mMap.addMarker(markerOptions);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finishAffinity();
            }
        });

        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }else{
            mMap.setMyLocationEnabled(true);


            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

                mMap.moveCamera(center);
                mMap.animateCamera(zoom);

            }

        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Intent intent = new Intent(MainActivity.this, EventCreate.class);
                String message = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("markers");

        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    HashMap<String, Object> markerHashMap = (HashMap<String, Object>) snapshot.getValue();
                    try {
                        Date markerDay = new SimpleDateFormat("dd/MM/yyyy").parse(markerHashMap.get("date").toString());

                        if( new Date().before(markerDay)){
                            HashMap<String, Double> markerLoc = (HashMap<String, Double>) markerHashMap.get("location");

                            MarkerOptions markerOptions = new MarkerOptions().title(snapshot.getKey()).position(new LatLng(markerLoc.get("latitude"),markerLoc.get("longitude")));
                            mMap.addMarker(markerOptions);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();


                    }





                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Intent intent = new Intent(MainActivity.this, Event.class);
                String message = marker.getTitle();
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);

                return true;

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){


        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            if (data.hasExtra("marker")) {
                MarkerOptions marker = (MarkerOptions) data.getExtras().get("marker");

                mMap.addMarker(marker);

            }
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                        mMap.setMyLocationEnabled(true);
                        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(location != null){
                            CameraUpdate center=
                                    CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

                            mMap.moveCamera(center);
                            mMap.animateCamera(zoom);

                        }
                    }

                }
                return;
            }

        }
    }






    public void onHeaderPress(View view){

        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
        String userID = sharedPref.getString("userID", null);

        Intent intent = new Intent(this, Profile.class);
        intent.putExtra(EXTRA_MESSAGE, userID);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {



        int id = item.getItemId();

        if (id == R.id.nav_notifications) {

            Intent intent = new Intent(this, Notifications.class);
            startActivity(intent);

        } else if (id == R.id.nav_events) {

            Intent intent = new Intent(this, Events.class);
            startActivity(intent);


        } else if (id == R.id.nav_groups) {
            Intent intent = new Intent(this, Groups.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {

            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {

            SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userID", null);
            editor.commit();

            Intent intent = new Intent(this, Login.class);
            startActivity(intent);

        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
