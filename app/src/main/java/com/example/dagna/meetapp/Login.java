package com.example.dagna.meetapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(true){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
