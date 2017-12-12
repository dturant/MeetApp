package com.example.dagna.meetapp;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.View;
        import android.view.WindowManager;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.TabHost;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.bumptech.glide.Glide;
        import com.firebase.ui.storage.images.FirebaseImageLoader;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;

        import java.util.ArrayList;
        import java.util.HashMap;

public class Favorite extends AppCompatActivity {
    TextView eventName, eventDate,eventTime,eventLocation,eventCategory,eventDescription;
    Button cancelButton, goButton;
    ImageView eventPhoto;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private StorageReference mFirebaseStorage;
    private FirebaseStorage mFirebaseStorageInstance;

    public String markerID;
    private String userID;
    public int pos = 0;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listParticipants=new ArrayList<String>();
    ArrayList<String> listParticipantsIDs;

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    Context context;


    public final static String EXTRA_MESSAGE = "com.example.dagna.meetapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=this;
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        markerID = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
        userID = sharedPref.getString("userID", null);

        setStatusBarTranslucent(true);

        TabHost host = (TabHost)findViewById(R.id.groups_tab);
        host.setup();

        TabHost.TabSpec spec1 = host.newTabSpec("Info");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Info");
        host.addTab(spec1);

        TabHost.TabSpec spec2 = host.newTabSpec("Participants");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Participants");
        host.addTab(spec2);


        eventPhoto = (ImageView) findViewById(R.id.event_photo);
        mFirebaseStorageInstance = FirebaseStorage.getInstance();
        mFirebaseStorage = mFirebaseStorageInstance.getReference("markers").child(markerID);

        mFirebaseStorageInstance.getReference("markers").child(markerID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).using(new FirebaseImageLoader()).load(mFirebaseStorage).into(eventPhoto);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Glide.with(context).using(new FirebaseImageLoader()).load(mFirebaseStorageInstance.getReference("markers").child("default.png")).into(eventPhoto);
            }
        });




        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("markers").child(markerID);


        mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                eventName = (TextView) findViewById(R.id.event_name);
                eventName.setText(dataSnapshot.child("title").getValue().toString());
                eventDate = (TextView) findViewById(R.id.event_date);
                eventDate.setText(dataSnapshot.child("date").getValue().toString());
                eventTime = (TextView) findViewById(R.id.event_time);
                eventTime.setText(dataSnapshot.child("time").getValue().toString());
                eventDescription = (TextView) findViewById(R.id.event_description);
                eventDescription.setText(dataSnapshot.child("description").getValue().toString());
                eventCategory = (TextView) findViewById(R.id.event_category);
                eventCategory.setText(dataSnapshot.child("category").getValue().toString());

                listParticipantsIDs = (ArrayList<String>)  dataSnapshot.child("users").getValue();
                Log.d("numberOfParticipants", Integer.toString(listParticipantsIDs.size()));
                SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
                final String userID = sharedPref.getString("userID", null);


                if(dataSnapshot.child("owner").getValue().toString().equals(userID)){
                    cancelButton = (Button) findViewById(R.id.buttonCancel);
                    cancelButton.setVisibility(View.VISIBLE);
                }else if(!listParticipantsIDs.contains(userID)){
                    goButton = (Button) findViewById(R.id.buttonGo);
                    goButton.setVisibility(View.VISIBLE);
                }

                for(String user: listParticipantsIDs){

                    mFirebaseInstance = FirebaseDatabase.getInstance();
                    mFirebaseInstance.getReference("users").child(user).child("nome").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            listParticipants.add((String) dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }



                    });


                }


                adapter=new ArrayAdapter<String>(Favorite.this,
                        android.R.layout.simple_list_item_1,
                        listParticipants);

                ListView list = (ListView) findViewById(R.id.participants_list);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {

                        Intent intent = new Intent(Favorite.this, Profile.class);
                        String message = listParticipantsIDs.get(position);
                        intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);

                    }

                });

                //Log.d("data", name + " " + date + " " + time + " " + description + " "+category);
                // TextView tv = (TextView) findViewById(R.id.marker_title);
                // tv.setText(title);

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

    public void goEvent(View view){



        FirebaseDatabase.getInstance().getReference("markers").child(markerID).child("users").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    pos = Integer.valueOf(snapshot.getKey())+1;


                }

                mFirebaseInstance.getReference("markers").child(markerID).child("users").child(String.valueOf(pos)).setValue(userID);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        Toast.makeText(getApplicationContext(),
                "You confirmed your presence", Toast.LENGTH_SHORT).show();

        goButton.setVisibility(View.INVISIBLE);

    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
