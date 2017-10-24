package com.example.dagna.meetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void register(View view) {
        EditText nome = (EditText) findViewById(R.id.editText1);
        EditText email = (EditText) findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText3);
        EditText password2 = (EditText) findViewById(R.id.editText4);

        String strNome = nome.getText().toString();
        String strEmail = email.getText().toString();
        String strPw = password.getText().toString();
        String strPw2 = password2.getText().toString();


        if(TextUtils.isEmpty(strNome)) {
            nome.setError("Your Name");
            return;
        } else if (TextUtils.isEmpty(strEmail)) {
            email.setError("Your Email");
            return;
        } else if (TextUtils.isEmpty(strPw)) {
            password.setError("Your Password");
            return;
        } else if (TextUtils.isEmpty(strPw2)) {
            password2.setError("Your Passwords need to match");
            return;
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}

