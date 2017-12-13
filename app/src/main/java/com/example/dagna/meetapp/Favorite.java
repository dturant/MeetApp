package com.example.dagna.meetapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Favorite extends AppCompatActivity {
    TextView favoriteName, favoriteLocation, favoriteDescription;
    ImageView favoritePhoto;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private StorageReference mFirebaseStorage;
    private FirebaseStorage mFirebaseStorageInstance;

    public String markerID;
    private String userID;
    public int pos = 0;

    Context context;


    public final static String EXTRA_MESSAGE = "com.example.dagna.meetapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=this;
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        markerID = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
        userID = sharedPref.getString("userID", null);

        setStatusBarTranslucent(true);


        favoritePhoto = (ImageView) findViewById(R.id.favorite_photo);
        mFirebaseStorageInstance = FirebaseStorage.getInstance();
        mFirebaseStorage = mFirebaseStorageInstance.getReference("favorites").child(markerID);

        mFirebaseStorageInstance.getReference("favorites").child(markerID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).using(new FirebaseImageLoader()).load(mFirebaseStorage).into(favoritePhoto);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Glide.with(context).using(new FirebaseImageLoader()).load(mFirebaseStorageInstance.getReference("markers").child("default.png")).into(favoritePhoto);
            }
        });




        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("favorites").child(markerID);


        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                favoriteName = (TextView) findViewById(R.id.favorite_name);
                favoriteName.setText(dataSnapshot.child("title").getValue().toString());
                favoriteDescription = (TextView) findViewById(R.id.favorite_description);
                favoriteDescription.setText(dataSnapshot.child("description").getValue().toString());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void cancelEvent(View view){

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("markers").child(markerID);
        mFirebaseDatabase.removeValue();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }



    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
