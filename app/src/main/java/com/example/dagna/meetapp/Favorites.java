package com.example.dagna.meetapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dagna.meetapp.helpers.FavoriteAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class Favorites extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.dagna.meetapp.MESSAGE";
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private StorageReference mFirebaseStorage;
    private FirebaseStorage mFirebaseStorageInstance;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayList<String> listItemsKeys=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW

    ArrayList<FavoriteObject> favorites;
    FavoriteAdapter adapter;
    ListView list;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
        userID = sharedPref.getString("userID", null);

        mFirebaseStorageInstance = FirebaseStorage.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("users").child(userID).child("favourites");

        favorites = new ArrayList<>();
        list = (ListView) findViewById(R.id.favorites_list);
        adapter=new FavoriteAdapter(this,
                favorites);
        list.setAdapter(adapter);

        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favorites.clear();


                for (final DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    HashMap<String, Object> markerHashMap = (HashMap<String, Object>) snapshot.getValue();

                        String markerTitle = (String) markerHashMap.get("title");
                        listItems.add(markerTitle);
                        listItemsKeys.add(snapshot.getKey());
                        final String id = snapshot.getKey();
                        final String name = (String) markerHashMap.get("title");
                        final String description = (String) markerHashMap.get("description");
                        HashMap<String, Double> markerLoc = (HashMap<String, Double>) markerHashMap.get("location");
                        final LatLng latLng = new LatLng(markerLoc.get("latitude"),markerLoc.get("longitude"));

                        mFirebaseStorageInstance.getReference("favorites").child(snapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                mFirebaseStorage = mFirebaseStorageInstance.getReference("favorites").child(snapshot.getKey());
                                FavoriteObject e = new FavoriteObject(id,name,description,latLng,mFirebaseStorage);

                                favorites.add(e);
                                adapter.notifyDataSetChanged();

                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                                mFirebaseStorage =mFirebaseStorageInstance.getReference("favorites").child("default.png");
                                FavoriteObject e = new FavoriteObject(id,name,description,latLng,mFirebaseStorage);
                                favorites.add(e);
                                adapter.notifyDataSetChanged();
                            }
                        });



                }


                // adapter.notifyDataSetChanged();
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {

                        Intent intent = new Intent(Favorites.this, Favorite.class);
                        Object o = parent.getAdapter().getItem(position);
                        if(o instanceof  FavoriteObject){
                            String message = ((FavoriteObject) o).getId();
                            intent.putExtra(EXTRA_MESSAGE, message);
                            startActivity(intent);
                        }
                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {

        Intent it = new Intent(Favorites.this, MainActivity.class);
        startActivity(it);

        finish();
    }

}
