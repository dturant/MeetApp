package com.example.dagna.meetapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity {

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayList<String> listItemsIDs=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;


    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;


    public final static String EXTRA_MESSAGE = "com.example.dagna.meetapp.MESSAGE";

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ListView list = (ListView) findViewById(R.id.notifications_list);

        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
        userID = sharedPref.getString("userID", null);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users").child(userID).child("notifications");
        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {


                    listItems.add((String) snapshot.child("user_name").getValue() + " added an event!");
                    listItemsIDs.add((String) snapshot.child("event").getValue());

                }

                adapter=new ArrayAdapter<String>(Notifications.this,
                        android.R.layout.simple_list_item_1,
                        listItems);



                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {

                        finish();
                        Intent intent = new Intent(Notifications.this, Event.class);
                        String message = listItemsIDs.get(position);
                        intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);

                    }

                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

}
