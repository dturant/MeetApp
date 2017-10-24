package com.example.dagna.meetapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText username = (EditText) findViewById(R.id.editText1);
        username.setText("admin");
    }

    public void login(View view) {
        EditText password = (EditText) findViewById(R.id.editText2);
        EditText username = (EditText) findViewById(R.id.editText1);
        TextView register = (TextView)findViewById(R.id.textView3);

        if (username.getText().toString().equals("admin")
                && password.getText().toString().equals("admin")) {
            Toast.makeText(getApplicationContext(),
                    "Redirecting...",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            if (username.getText().toString().trim().length() <= 0 && password.getText().toString().trim().length() <= 0) {

                Toast.makeText(getApplicationContext(), "Wrong Credentials",
                        Toast.LENGTH_SHORT).show();
                register.setVisibility(View.VISIBLE);
            }
            else if (password.getText().toString().trim().length() <= 0) {
                Toast.makeText(getApplicationContext(), "You didn't enter a Password",
                        Toast.LENGTH_SHORT).show();
            } else if (username.getText().toString().trim().length() <= 0){
                Toast.makeText(getApplicationContext(), "You didn't enter a Username",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Wrong Username or Password",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void register(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}