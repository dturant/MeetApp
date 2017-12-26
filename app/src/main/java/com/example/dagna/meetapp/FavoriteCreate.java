package com.example.dagna.meetapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class FavoriteCreate extends AppCompatActivity {

    public LatLng favoriteLocation;
    public Intent intent;
    public ImageView favoritePhoto;
    public EditText favoriteName, favoriteDescription;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private StorageReference mFirebaseStorage;
    private FirebaseStorage mFirebaseStorageInstance;

    private Uri selectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        favoritePhoto = (ImageView) findViewById(R.id.favorite_photo);
        favoriteName = (EditText) findViewById(R.id.favorite_name);
        favoriteDescription = (EditText) findViewById(R.id.favorite_description);


        intent = getIntent();
        String[] locationString = intent.getStringExtra(MainActivity.EXTRA_MESSAGE).split(",");


        favoriteLocation = new LatLng(Double.parseDouble(locationString[0]), Double.parseDouble(locationString[1]));

    }

    public void createFavorite(View view) {

        String name = favoriteName.getText().toString();
        String description = favoriteDescription.getText().toString();


        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
        String owner = sharedPref.getString("userID", null);



        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users").child(owner).child("favourites");

        mFirebaseStorageInstance = FirebaseStorage.getInstance();
        mFirebaseStorage = mFirebaseStorageInstance.getReference("favorites");


        String favoriteID = mFirebaseDatabase.push().getKey();

        mFirebaseDatabase.child(favoriteID).child("title").setValue(name);
        mFirebaseDatabase.child(favoriteID).child("location").setValue(favoriteLocation);
        mFirebaseDatabase.child(favoriteID).child("description").setValue(description);
//        mFirebaseDatabase.child(favoriteID).child("owner").setValue(owner);

        mFirebaseStorage.child(favoriteID).putFile(selectedImage);


        MarkerOptions marker = new MarkerOptions()
                .position(favoriteLocation)
                .snippet("favorite")
                .title(favoriteID)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star));

        intent.putExtra("marker", marker);

        setResult(Activity.RESULT_OK, intent);
        super.finish();

    }


    protected void addPhoto(View view) {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {

                    try {
                        selectedImage = imageReturnedIntent.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        favoritePhoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    try {
                        selectedImage = imageReturnedIntent.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        favoritePhoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}