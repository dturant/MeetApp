package com.example.dagna.meetapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
        EditText password = (EditText) findViewById(R.id.editText2);
        username.setText("admin");
        password.setText("admin");

    }

    public void login(View view) {
        EditText password = (EditText) findViewById(R.id.editText2);
        EditText username = (EditText) findViewById(R.id.editText1);
        TextView textRegister = (TextView)findViewById(R.id.textView3);

        String strPw = password.getText().toString();
        String strUser = username.getText().toString();

        if (strUser.equals("admin") && strPw.equals("admin")) {
            Toast.makeText(getApplicationContext(),
                    "Redirecting...",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            if (TextUtils.isEmpty(strUser)) {
                username.setError("Your Name can't be empty");
                return;
            } else if (TextUtils.isEmpty(strPw)) {
                password.setError("Your Password can't be empty");
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Username and Password does not match",
                        Toast.LENGTH_SHORT).show();
                textRegister.setVisibility(View.VISIBLE);
            }
        }
    }

    public void register(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}