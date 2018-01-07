package com.example.dagna.meetapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SharedPreferences sharedPref = getSharedPreferences("showLocation", MODE_PRIVATE);
        String showLocation = sharedPref.getString("showLocation", "null");


        Switch switchButtonLocation = (Switch) findViewById(R.id.location);

        if(!showLocation.equals("null")){
            switchButtonLocation.setChecked(true);
        }else{
            switchButtonLocation.setChecked(false);
        }


        switchButtonLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences sharedPref = getSharedPreferences("showLocation", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("showLocation", String.valueOf(true));
                    editor.commit();
                } else {
                    SharedPreferences sharedPref = getSharedPreferences("showLocation", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("showLocation", null);
                    editor.commit();
                }
            }
        });


        SharedPreferences sharedPrefSatellite = getSharedPreferences("showSatelliteView", MODE_PRIVATE);
        String showSatelliteView = sharedPrefSatellite.getString("showSatelliteView", "null");


        Switch switchButtonSatellite = (Switch) findViewById(R.id.view);

        if(!showSatelliteView.equals("null")){
            switchButtonSatellite.setChecked(true);
        }else{
            switchButtonSatellite.setChecked(false);
        }


        switchButtonSatellite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences sharedPref = getSharedPreferences("showSatelliteView", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("showSatelliteView", String.valueOf(true));
                    editor.commit();
                } else {
                    SharedPreferences sharedPref = getSharedPreferences("showSatelliteView", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("showSatelliteView", null);
                    editor.commit();
                }
            }
        });


    }
}