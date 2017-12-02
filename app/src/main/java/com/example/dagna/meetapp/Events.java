package com.example.dagna.meetapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dagna.meetapp.helpers.EventAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayList<String> listItemsKeys=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    EventAdapter adapter;
    Context context;
    List<EventObject> events;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context=this;
        events = new ArrayList<>();




        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("markers");

        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListView list = (ListView) findViewById(R.id.events_list);



                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    HashMap<String, Object> markerHashMap = (HashMap<String, Object>) snapshot.getValue();


                    try {
                        Date markerDay = new SimpleDateFormat("dd/MM/yyyy").parse((String) markerHashMap.get("date"));

                        if( new Date().before(markerDay) || new Date().equals(markerDay)){

                            String markerTitle = (String) markerHashMap.get("title");
                            listItems.add(markerTitle);
                            listItemsKeys.add(snapshot.getKey());
                            String name = (String) markerHashMap.get("title");
                            String date = (String) markerHashMap.get("date");
                            String time = (String) markerHashMap.get("time");
                            String description = (String) markerHashMap.get("description");
                            HashMap<String, Double> markerLoc = (HashMap<String, Double>) markerHashMap.get("location");
                            LatLng latLng = new LatLng(markerLoc.get("latitude"),markerLoc.get("longitude"));
                            String owner = (String) markerHashMap.get("owner");
                            Category category = Category.valueOf((String) markerHashMap.get("category"));
                            //TODO change for the image from the DB
                            Integer imageId = R.drawable.abc;

                            EventObject e = new EventObject(name,date,time,description,latLng,owner,category,imageId);
                            events.add(e);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();


                    }




                }


                adapter=new EventAdapter(context,
                        R.layout.event_list_item);

                for(int i=0;i<events.size();i++) {
                    adapter.add(events.get(i));
                }



                list.setAdapter(adapter);



                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {

                        Intent intent = new Intent(Events.this, Event.class);
                        String message = listItemsKeys.get(position);
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
