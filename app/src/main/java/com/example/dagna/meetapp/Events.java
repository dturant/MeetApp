package com.example.dagna.meetapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dagna.meetapp.helpers.EventAdapter;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Events extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.dagna.meetapp.MESSAGE";
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private StorageReference mFirebaseStorage;
    private FirebaseStorage mFirebaseStorageInstance;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayList<String> listItemsKeys=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    Context context;
    ArrayList<EventObject> events;
    EventAdapter adapter;
    ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=this;


        mFirebaseStorageInstance = FirebaseStorage.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("markers");



        events = new ArrayList<>();
        list = (ListView) findViewById(R.id.events_list);
        adapter=new EventAdapter(context,
                events);
        list.setAdapter(adapter);

        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();


                for (final DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    HashMap<String, Object> markerHashMap = (HashMap<String, Object>) snapshot.getValue();


                    try {
                        Date markerDay = new SimpleDateFormat("dd/MM/yyyy").parse((String) markerHashMap.get("date"));

                        if( new Date().before(markerDay) || new Date().equals(markerDay)){

                            String markerTitle = (String) markerHashMap.get("title");
                            listItems.add(markerTitle);
                            listItemsKeys.add(snapshot.getKey());
                            final String id = snapshot.getKey();
                            final String name = (String) markerHashMap.get("title");
                            final String date = (String) markerHashMap.get("date");
                            final String time = (String) markerHashMap.get("time");
                            final String description = (String) markerHashMap.get("description");
                            HashMap<String, Double> markerLoc = (HashMap<String, Double>) markerHashMap.get("location");
                            final LatLng latLng = new LatLng(markerLoc.get("latitude"),markerLoc.get("longitude"));
                            final String owner = (String) markerHashMap.get("owner");
                            final Category category = Category.valueOf((String) markerHashMap.get("category"));

                            mFirebaseStorageInstance.getReference("markers").child(snapshot.getKey()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    mFirebaseStorage = mFirebaseStorageInstance.getReference("markers").child(snapshot.getKey());
                                    EventObject e = new EventObject(id,name,date,time,description,latLng,owner,category,mFirebaseStorage);

                                    events.add(e);
                                    adapter.notifyDataSetChanged();

                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {

                                                            mFirebaseStorage =mFirebaseStorageInstance.getReference("markers").child("default.png");
                                                            EventObject e = new EventObject(id,name,date,time,description,latLng,owner,category,mFirebaseStorage);
                                                            events.add(e);
                                                            adapter.notifyDataSetChanged();
                                                            }
                                                    });

                        }
                    } catch (ParseException e) {
                        e.printStackTrace();


                    }
                }


               // adapter.notifyDataSetChanged();
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {

                        Intent intent = new Intent(Events.this, Event.class);
                        Object o =parent.getAdapter().getItem(position);
                        if(o instanceof  EventObject){
                            String message = ((EventObject) o).getId();
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

}
