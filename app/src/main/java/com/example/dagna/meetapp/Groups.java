package com.example.dagna.meetapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TabHost;

public class Groups extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabHost host = (TabHost)findViewById(R.id.groups_tab);
        host.setup();

        TabHost.TabSpec spec1 = host.newTabSpec("Your groups");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Your groups");
        host.addTab(spec1);

        TabHost.TabSpec spec2 = host.newTabSpec("Friends' groups");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Friends' groups");
        host.addTab(spec2);

        TabHost.TabSpec spec3 = host.newTabSpec("Other");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Other");
        host.addTab(spec3);
    }

}
