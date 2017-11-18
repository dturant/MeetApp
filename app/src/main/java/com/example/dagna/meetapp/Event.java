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

public class Event extends AppCompatActivity {
TextView eventName, eventDate,eventTime,eventLocation,eventCategory,eventDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String description = intent.getStringExtra("description");
        String category = intent.getStringExtra("category");

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

        eventName = (TextView) findViewById(R.id.event_name);
        eventName.setText(name);
        eventDate = (TextView) findViewById(R.id.event_date);
        eventDate.setText(date);
        eventTime = (TextView) findViewById(R.id.event_time);
        eventTime.setText(time);
        eventDescription = (TextView) findViewById(R.id.event_description);
        eventDescription.setText(description);
        eventCategory = (TextView) findViewById(R.id.event_category);
        eventCategory.setText(category);

        Log.d("data", name + " " + date + " " + time + " " + description + " "+category);
       // TextView tv = (TextView) findViewById(R.id.marker_title);
       // tv.setText(title);
    }

}
