package com.example.dagna.meetapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener,
        GoogleApiClient.OnConnectionFailedListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    public Location location = null;
    private int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public final static String EXTRA_MESSAGE = "com.example.dagna.meetapp.MESSAGE";

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mFirebaseStorage;
    private FirebaseStorage mFirebaseStorageInstance;
    private int REQUEST_CODE = 1;

    public String userID;
    private Context context;
    List seletedItems = new ArrayList();
    private FusedLocationProviderClient mFusedLocationClient;
    List<Integer> checkedItems;
    EditText filterDate;
    String pickedDate;

    public RadioButton all;
    public RadioButton favourites;
    public RadioButton allEvents ;
    public RadioButton yourEvents;
    public RadioButton friendsEvents;
    public RadioButton byDate;


    ArrayList<String> listFriendsIDs=new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
        userID = sharedPref.getString("userID", null);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(firebaseUser.getUid())
                    .child("instanceId")
                    .setValue(FirebaseInstanceId.getInstance().getToken());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkedItems = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(context);
                View dialogView = li.inflate(R.layout.dialog_filter, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle("Filter");
                alertDialogBuilder.setView(dialogView);

                all = (RadioButton) dialogView.findViewById(R.id.all);
                favourites = (RadioButton) dialogView.findViewById(R.id.favourites);
                allEvents = (RadioButton) dialogView.findViewById(R.id.all_events);
                yourEvents = (RadioButton) dialogView.findViewById(R.id.your_events);
                friendsEvents = (RadioButton)dialogView.findViewById(R.id.friends_events);
                byDate = (RadioButton)dialogView.findViewById(R.id.by_date);



                filterDate = (EditText) dialogView.findViewById(R.id.filter_date);
                filterDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickDate();
                    }
                });

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        checkedItems.clear();
                                        if(all.isChecked()) {
                                            checkedItems.add(0);
                                        }
                                        if(favourites.isChecked()) {
                                            checkedItems.add(1);
                                        }
                                        if(allEvents.isChecked()) {
                                            checkedItems.add(2);
                                        }
                                        if(yourEvents.isChecked()){
                                            checkedItems.add(3);
                                        }
                                        if(friendsEvents.isChecked()){
                                            checkedItems.add(4);
                                        }
                                        if(byDate.isChecked() && !filterDate.equals(null) ){
                                            checkedItems.add(5);
                                            pickedDate = filterDate.getText().toString();
                                        }


                                        filter();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }



        });

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseInstance.getReference("users").child(userID).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    listFriendsIDs.add(snapshot.getValue().toString());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        mFirebaseStorageInstance = FirebaseStorage.getInstance();
        mFirebaseStorage = mFirebaseStorageInstance.getReference("users").child(userID);

        mFirebaseStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSuccess(Uri uri) {

                ImageView profilePhoto = (ImageView) findViewById(R.id.profilePhoto);
                Glide.with(MainActivity.this).using(new FirebaseImageLoader()).load(mFirebaseStorage).into(profilePhoto);



            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ImageView profilePhoto = (ImageView) findViewById(R.id.profilePhoto);
                Glide.with(MainActivity.this)
                        .load(mFirebaseUser.getPhotoUrl())
                        .into(profilePhoto);

            }
        });




        mFirebaseDatabase = mFirebaseInstance.getReference("users").child(userID);


        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TextView tv = (TextView) findViewById(R.id.name);
                tv.setText(dataSnapshot.child("nome").getValue().toString());

                TextView tvMail = (TextView) findViewById(R.id.email);
                tvMail.setText(dataSnapshot.child("email").getValue().toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);




        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        new LastUsersLocation().execute(MainActivity.this);

    }

    public final void setFriendMarker(String friendID, LatLng location) {



        MarkerOptions markerOptions = new MarkerOptions().title(friendID).snippet("friend").position(location)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_friend_marker));
        mMap.addMarker(markerOptions);


    }

    public final LatLng getLocation() {

        if (!(location == null)) {
            return new LatLng(location.getLatitude(), location.getLongitude());
        }
        return null;
    }

    private void getAllEvents(){
        DatabaseReference ref = mFirebaseInstance.getReference("markers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    getEventsFromFirebase(postSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getYourEvents(){
        DatabaseReference ref = mFirebaseInstance.getReference("markers");
        ref.orderByChild("owner").equalTo(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                getEventsFromFirebase(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void getFriendsEvents(){

        DatabaseReference mdb = mFirebaseInstance.getReference("users").child(userID).child("friends");
        mdb.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    String friend = postSnapshot.getValue().toString();
                    DatabaseReference ref = mFirebaseInstance.getReference("markers");
                    ref.orderByChild("owner").equalTo(friend).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                            getEventsFromFirebase(dataSnapshot);
                        }
                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {}

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getEventsByDate(){
        DatabaseReference ref = mFirebaseInstance.getReference("markers");

        ref.orderByChild("date").equalTo(pickedDate).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                getEventsFromFirebase(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public void getFavourites(){
        DatabaseReference ref = mFirebaseInstance.getReference("users").child(userID).child("favourites");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    getFavouritesFromFirebase(postSnapshot);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void filter(){

        if (checkedItems.size() > 0) {
            mMap.clear();
            if (checkedItems.contains(0)) { //all
                getAllEvents();
                getFavourites();
            }
            if (checkedItems.contains(1)) { //favourites
                getFavourites();
            }
            if (checkedItems.contains(2)) { //all events
                getAllEvents();
            }

            if (checkedItems.contains(3)) { //your events
                getYourEvents();
            }

            if (checkedItems.contains(4)) { //friends' events
                getFriendsEvents();
            }

            if (checkedItems.contains(5)) { //by date
                getEventsByDate();
            }


        }
    }


    private void getEventsFromFirebase(DataSnapshot snapshot) {

        if (snapshot.exists()) {

            HashMap<String, Object> markerHashMap = (HashMap<String, Object>) snapshot.getValue();
            try {
                Date markerDay = new SimpleDateFormat("dd/MM/yyyy").parse(markerHashMap.get("date").toString());
                ArrayList<String> invited = (ArrayList<String>) markerHashMap.get("users");


                String privacy;
                try{
                    privacy = markerHashMap.get("privacy").toString();
                }catch (Exception e){
                    privacy = "";
                }

                if ((new Date().before(markerDay) || new Date().equals(markerDay)) && (privacy.equals("Public") || (privacy.equals("Private") && (markerHashMap.get("owner").toString().equals(userID) || invited.contains(userID)) ) ||
                        (privacy.equals("Friends") && (markerHashMap.get("owner").toString().equals(userID) || invited.contains(userID) || listFriendsIDs.contains(markerHashMap.get("owner").toString())) ) )  ) {
                    HashMap<String, Double> markerLoc = (HashMap<String, Double>) markerHashMap.get("location");

                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_event)).snippet("event").title(snapshot.getKey()).position(new LatLng(markerLoc.get("latitude"), markerLoc.get("longitude")));
                    mMap.addMarker(markerOptions);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //  }
        }
    }

    private void getFavouritesFromFirebase(DataSnapshot snapshot) {

        if (snapshot.exists()) {
            HashMap<String, Object> markerHashMap = (HashMap<String, Object>) snapshot.getValue();

            HashMap<String, Double> markerLoc = (HashMap<String, Double>) markerHashMap.get("location");

            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star)).snippet("favorite").title(snapshot.getKey()).position(new LatLng(markerLoc.get("latitude"), markerLoc.get("longitude")));
            mMap.addMarker(markerOptions);

        }
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

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
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
        } else {

            mMap.setMyLocationEnabled(true);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location loc) {
                            // Got last known location. In some rare situations this can be null.
                            if (loc != null) {
                                location = loc;
                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                                mMap.moveCamera(center);
                                mMap.animateCamera(zoom);
                            }
                        }
                    });
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                CharSequence longPress[] = new CharSequence[] {"Event", "Favorite"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.long_press_map)
                        .setItems(longPress, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){ //Event
                                    Intent intent = new Intent(MainActivity.this, EventCreate.class);
                                    String message = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
                                    intent.putExtra(EXTRA_MESSAGE, message);
                                    startActivityForResult(intent, REQUEST_CODE);
                                } else if (which == 1){ //Favorite
                                    Intent intent = new Intent(MainActivity.this, FavoriteCreate.class); //create favoritCreate
                                    String message = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
                                    intent.putExtra(EXTRA_MESSAGE, message);
                                    startActivityForResult(intent, REQUEST_CODE);
                                } else {
                                    dialog.cancel();
                                }
                            }
                        });
                builder.show();
            }
        });


        SharedPreferences sharedPrefSatellite = getSharedPreferences("showSatelliteView", MODE_PRIVATE);
        String showSatelliteView = sharedPrefSatellite.getString("showSatelliteView", "null");

        if (!showSatelliteView.equals("null")) {
            mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(mMap.MAP_TYPE_NORMAL);
        }


        mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("markers");

        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    HashMap<String, Object> markerHashMap = (HashMap<String, Object>) snapshot.getValue();
                    try {
                        Date markerDay = new SimpleDateFormat("dd/MM/yyyy").parse(markerHashMap.get("date").toString());

                        ArrayList<String> invited = (ArrayList<String>) markerHashMap.get("users");


                        String privacy;
                        try{
                            privacy = markerHashMap.get("privacy").toString();
                        }catch (Exception e){
                            privacy = "";
                        }

                        if ((new Date().before(markerDay) || new Date().equals(markerDay)) && (privacy.equals("Public") || (privacy.equals("Private") && (markerHashMap.get("owner").toString().equals(userID) || invited.contains(userID)) ) ||
                                (privacy.equals("Friends") && (markerHashMap.get("owner").toString().equals(userID) || invited.contains(userID) || listFriendsIDs.contains(markerHashMap.get("owner").toString())) ) )  ) {
                            HashMap<String, Double> markerLoc = (HashMap<String, Double>) markerHashMap.get("location");

                            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_event)).snippet("event").title(snapshot.getKey()).position(new LatLng(markerLoc.get("latitude"), markerLoc.get("longitude")));
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

        mFirebaseDatabase = mFirebaseInstance.getReference("users").child(userID).child("favourites");

        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    HashMap<String, Object> markerHashMap = (HashMap<String, Object>) snapshot.getValue();
                    HashMap<String, Double> markerLoc = (HashMap<String, Double>) markerHashMap.get("location");

                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star)).snippet("favorite").title(snapshot.getKey()).position(new LatLng(markerLoc.get("latitude"), markerLoc.get("longitude")));
                    mMap.addMarker(markerOptions);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Intent intent;

                if (marker.getSnippet().equals("event")) {
                    intent = new Intent(MainActivity.this, Event.class);

                }else if(marker.getSnippet().equals("favorite")){
                    intent = new Intent(MainActivity.this, Favorite.class);

                } else {
                    intent = new Intent(MainActivity.this, Profile.class);
                }

                String message = marker.getTitle();
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                return true;

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


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
                    }

                }
                return;
            }

        }
    }


    public void onHeaderPress(View view) {

        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
        String userID = sharedPref.getString("userID", null);

        Intent intent = new Intent(this, Profile.class);
        intent.putExtra(EXTRA_MESSAGE, userID);
        startActivity(intent);

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

        } else if (id == R.id.nav_favorites) {

            Intent intent = new Intent(this, Favorites.class);
            startActivity(intent);

            //CLOSED FOR NOW
//        } else if (id == R.id.nav_groups) {
//            Intent intent = new Intent(this, Groups.class);
//            startActivity(intent);

        } else if (id == R.id.nav_settings) {

            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {

            SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userID", null);
            editor.commit();

            mFirebaseAuth.signOut();
            startActivity(new Intent(this, Login.class));
            return true;

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onLocationChanged(Location loc) {
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            mMap.setMyLocationEnabled(true);
            location = loc;
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void pickDate(){
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedmonth = selectedmonth + 1;
                filterDate.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();

        byDate.setChecked(true);
    }
}
