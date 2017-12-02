package com.example.dagna.meetapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

public class LastUsersLocation extends AsyncTask<Context, Void, Void> {

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    public String userID;

    public MainActivity ma;



    public void saveLocation() throws IOException {



        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseInstance.getReference("users").child(userID).child("location").setValue(ma.getLocation());




    }


    public void getFriendsLocation(){

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseInstance.getReference("users").child(userID).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    mFirebaseInstance.getReference("users").child(snapshot.getValue().toString()).child("location").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()){
                             Log.i("AQUI",dataSnapshot.getValue().toString());

                                HashMap<String, Double> friendLoc = (HashMap<String, Double>) dataSnapshot.getValue();
                                LatLng ltlg =  new LatLng(friendLoc.get("latitude"),friendLoc.get("longitude"));
                                ma.setFriendMarker(ltlg);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void callLocationsFunctions() throws IOException {

        saveLocation();
        getFriendsLocation();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    callLocationsFunctions();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 120000);//from 2 to 2 minutes


    }

    @Override
    protected Void doInBackground(Context... contexts) {

        ma = (MainActivity) contexts[0];

        SharedPreferences sharedPref = contexts[0].getSharedPreferences("userID", MODE_PRIVATE);
        userID = sharedPref.getString("userID", null);

        try {
            callLocationsFunctions();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute(){

    }
}
