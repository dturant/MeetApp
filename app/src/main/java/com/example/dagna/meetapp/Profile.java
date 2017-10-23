package com.example.dagna.meetapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TabHost;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    }

}
