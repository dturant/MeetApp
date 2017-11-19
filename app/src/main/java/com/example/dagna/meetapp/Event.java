package com.example.dagna.meetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Event extends AppCompatActivity {
TextView eventName, eventDate,eventTime,eventLocation,eventCategory,eventDescription;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String markerID = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);


        TabHost host = (TabHost)findViewById(R.id.groups_tab);
        host.setup();

        TabHost.TabSpec spec1 = host.newTabSpec("Info");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Info");
        host.addTab(spec1);

        TabHost.TabSpec spec2 = host.newTabSpec("Participants");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Participants");
        host.addTab(spec2);


        mFirebaseInstance = FirebaseDatabase.getInstance();


        mFirebaseDatabase = mFirebaseInstance.getReference("markers").child(markerID);


        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                eventName = (TextView) findViewById(R.id.event_name);
                eventName.setText(dataSnapshot.child("title").getValue().toString());
//                eventDate = (TextView) findViewById(R.id.event_date);
//                eventDate.setText(date);
//                eventTime = (TextView) findViewById(R.id.event_time);
//                eventTime.setText(time);
//                eventDescription = (TextView) findViewById(R.id.event_description);
//                eventDescription.setText(description);
//                eventCategory = (TextView) findViewById(R.id.event_category);
//                eventCategory.setText(category);

                //Log.d("data", name + " " + date + " " + time + " " + description + " "+category);
                // TextView tv = (TextView) findViewById(R.id.marker_title);
                // tv.setText(title);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

}
