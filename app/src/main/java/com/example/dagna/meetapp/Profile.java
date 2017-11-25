package com.example.dagna.meetapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import net.glxn.qrgen.android.QRCode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.R.id.content;

public class Profile extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    public String userID;
    private DatabaseReference mFirebaseDatabaseUsers;
    private DatabaseReference mFirebaseDatabaseMarkers;
    private FirebaseDatabase mFirebaseInstance;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayList<String> listItemsKeys=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    public final static String EXTRA_MESSAGE = "com.example.dagna.meetapp.MESSAGE";

    public TextView name, email;
    private ZXingScannerView mScannerView;
    private int PERMISSION_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                if (!haveCameraPermission()){
                    ActivityCompat.requestPermissions(Profile.this,new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                }
                mScannerView = new ZXingScannerView(Profile.this);   // Programmatically initialize the scanner view<br />
                setContentView(mScannerView);
                startCamera();// Start camera<br />
            }
        });

        TabHost host = (TabHost)findViewById(R.id.profile_tab);
        host.setup();

        TabHost.TabSpec spec1 = host.newTabSpec("Info");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Info");
        host.addTab(spec1);

        TabHost.TabSpec spec2 = host.newTabSpec("Friends");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Friends");
        host.addTab(spec2);

        TabHost.TabSpec spec3 = host.newTabSpec("Events");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Events");
        host.addTab(spec3);


        Intent intent = getIntent();
        userID = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        mFirebaseInstance = FirebaseDatabase.getInstance();


        mFirebaseDatabaseUsers = mFirebaseInstance.getReference("users").child(userID);


        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);

        mFirebaseDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name.setText(dataSnapshot.child("nome").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());

                Bitmap myBitmap = QRCode.from(userID).bitmap();
                ImageView myImage = (ImageView) findViewById(R.id.QRCode);
                myImage.setImageBitmap(myBitmap);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mFirebaseDatabaseMarkers = mFirebaseInstance.getReference("markers");
        mFirebaseDatabaseMarkers.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListView list = (ListView) findViewById(R.id.events_list);



                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    HashMap<String, Object> markerHashMap = (HashMap<String, Object>) snapshot.getValue();


                    try {
                        Date markerDay = new SimpleDateFormat("dd/MM/yyyy").parse((String) markerHashMap.get("date"));

                        if( new Date().before(markerDay) && userID.equals((String) markerHashMap.get("owner"))){

                            String markerTitle = (String) markerHashMap.get("title");
                            listItems.add(markerTitle);
                            listItemsKeys.add(snapshot.getKey());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();


                    }

                }

                adapter=new ArrayAdapter<String>(Profile.this,
                        android.R.layout.simple_list_item_1,
                        listItems);



                list.setAdapter(adapter);



                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {

                        Intent intent = new Intent(Profile.this, Event.class);
                        String message = listItemsKeys.get(position);
                        intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);

                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    private boolean haveCameraPermission()
    {
        if (Build.VERSION.SDK_INT < 23)
            return true;
        return checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        // This is because the dialog was cancelled when we recreated the activity.
        if (permissions.length == 0 || grantResults.length == 0)
            return;

        switch (requestCode)
        {
            case 1:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startCamera();
                }
                else
                {
                    finish();
                }
            }
            break;
        }
    }


    public void startCamera()
    {
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void handleResult(Result result) {
        Log.i("QRCODE",result.getText());
        mScannerView.stopCamera();
        setContentView(R.layout.activity_profile);
    }



    @Override
    public void onPause() {
            super.onPause();
            mScannerView.stopCamera();   // Stop camera on pause<br />
    }
}
