package com.example.dagna.meetapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Login extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText username = (EditText) findViewById(R.id.editText1);
        EditText password = (EditText) findViewById(R.id.editText2);
        username.setText("admin");
        password.setText("admin");

        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);

        String userID = sharedPref.getString("userID", null);

        if(userID!=null){
            Toast.makeText(getApplicationContext(),
                    "Redirecting...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void login(View view) {
        final EditText password = (EditText) findViewById(R.id.editText2);
        final EditText username = (EditText) findViewById(R.id.editText1);
        TextView textRegister = (TextView)findViewById(R.id.textView3);

        final String strPw = password.getText().toString();
        final String strUser = username.getText().toString();


        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users");


        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Map<String,String> user = (Map<String,String>) snapshot.getValue();

                    String nome =user.get("nome");

                    if(user.get("nome").equals(strUser) && user.get("password").equals(strPw)) {
//        if(strUser.equals("admin") && strPw.equals("admin")){

                        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("userID", snapshot.getKey());
                        editor.commit();


                        Toast.makeText(getApplicationContext(),
                                "Redirecting...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        break;

                    }else{
                        if (TextUtils.isEmpty(strUser)) {
                            username.setError("Your Name can't be empty");
                            return;
                        } else if (TextUtils.isEmpty(strPw)) {
                            password.setError("Your Password can't be empty");
                            return;
                        } else {
                            Toast.makeText(getApplicationContext(), "Username and Password does not match",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }

    public void register(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}