package com.example.dagna.meetapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View view) {
        EditText nome = (EditText) findViewById(R.id.editText1);
        EditText email = (EditText) findViewById(R.id.editText2);
        EditText password = (EditText) findViewById(R.id.editText3);
        EditText password2 = (EditText) findViewById(R.id.editText4);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

        String strNome = nome.getText().toString();
        String strEmail = email.getText().toString();
        String strPw = password.getText().toString();
        String strPw2 = password2.getText().toString();

        if(TextUtils.isEmpty(strNome)) {
            nome.setError("Your Name can't be empty");
            return;
        } else if (TextUtils.isEmpty(strEmail)) {
            email.setError("Your Email can't be empty");
            return;
        } else if (TextUtils.isEmpty(strPw)) {
            password.setError("Your Password can't be empty");
            return;
        } else if (TextUtils.isEmpty(strPw2)) {
            password2.setError("Your Passwords need to match");
            return;
        } else if (!checkBox.isChecked()){
            Toast.makeText(getApplicationContext(), "You must accept Terms and Conditions",
                    Toast.LENGTH_SHORT).show();
        } else {


            mFirebaseInstance = FirebaseDatabase.getInstance();

            mFirebaseDatabase = mFirebaseInstance.getReference("users");

            String userID = mFirebaseDatabase.push().getKey();

            mFirebaseDatabase.child(userID).child("nome").setValue(strNome);
            mFirebaseDatabase.child(userID).child("email").setValue(strEmail);
            mFirebaseDatabase.child(userID).child("password").setValue(strPw);

            SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userID", userID);
            editor.commit();

            SharedPreferences sharedPref2 = getSharedPreferences("showLocation", MODE_PRIVATE);
            SharedPreferences.Editor editor2 = sharedPref2.edit();
            editor2.putString("showLocation", String.valueOf(true));
            editor2.commit();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}

