package com.example.dagna.meetapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.Result;

import net.glxn.qrgen.android.QRCode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Profile extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    public String userID;
    public String userIDLogged;
    public String userFriendID;
    private DatabaseReference mFirebaseDatabaseUsers;
    private DatabaseReference mFirebaseDatabaseMarkers;
    private DatabaseReference mFirebaseDatabaseFriends;
    private FirebaseDatabase mFirebaseInstance;
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayList<String> listItemsKeys=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;


    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listFriends=new ArrayList<String>();
    ArrayList<String> listFriendsIDs=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapterFriends;

    public final static String EXTRA_MESSAGE = "com.example.dagna.meetapp.MESSAGE";

    public TextView name, email;
    private ZXingScannerView mScannerView;
    private int PERMISSION_REQUEST_CAMERA = 1;


    private StorageReference mFirebaseStorage;
    private FirebaseStorage mFirebaseStorageInstance;
    private Uri selectedImage = null;
    public ImageView profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        userID = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                if (!haveCameraPermission()){
                    ActivityCompat.requestPermissions(Profile.this,new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                }
                mScannerView = new ZXingScannerView(Profile.this);   // Programmatically initialize the scanner view<br />
                setContentView(mScannerView);
                startCamera();// Start camera<br />
            }
        });

        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
        userIDLogged = sharedPref.getString("userID", null);

        if(!userID.equals(userIDLogged)){

            fab.setVisibility(View.INVISIBLE);

        }




        TabHost host = (TabHost)findViewById(R.id.profile_tab);
        host.setup();

        TabHost.TabSpec spec1 = host.newTabSpec("Info");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Info");
        host.addTab(spec1);

        TabHost.TabSpec spec2 = host.newTabSpec("Friends");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Friends");
        host.addTab(spec2);

        TabHost.TabSpec spec3 = host.newTabSpec("Events");
        spec3.setContent(R.id.tab3);
        spec3.setIndicator("Events");
        host.addTab(spec3);


        profilePhoto = (ImageView) findViewById(R.id.profilePhoto);

        mFirebaseStorageInstance = FirebaseStorage.getInstance();
        mFirebaseStorage = mFirebaseStorageInstance.getReference("users").child(userID);



        mFirebaseStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSuccess(Uri uri) {


                Glide.with(Profile.this).using(new FirebaseImageLoader()).load(mFirebaseStorage).into(profilePhoto);



            }

        });




        mFirebaseInstance = FirebaseDatabase.getInstance();


        mFirebaseDatabaseUsers = mFirebaseInstance.getReference("users").child(userID);


        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);

        mFirebaseDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                name.setText(dataSnapshot.child("nome").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());

                if(userID.equals(userIDLogged)) {
                    Bitmap myBitmap = QRCode.from(userID).bitmap();
                    ImageView myImage = (ImageView) findViewById(R.id.QRCode);
                    myImage.setImageBitmap(myBitmap);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mFirebaseDatabaseMarkers = mFirebaseInstance.getReference("markers");
        mFirebaseDatabaseMarkers.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListView list = (ListView) findViewById(R.id.events_list);



                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    HashMap<String, Object> markerHashMap = (HashMap<String, Object>) snapshot.getValue();


                    try {
                        Date markerDay = new SimpleDateFormat("dd/MM/yyyy").parse((String) markerHashMap.get("date"));

                        if( new Date().before(markerDay) && userID.equals((String) markerHashMap.get("owner"))){

                            String markerTitle = (String) markerHashMap.get("title");
                            listItems.add(markerTitle);
                            listItemsKeys.add(snapshot.getKey());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();


                    }

                }

                adapter=new ArrayAdapter<String>(Profile.this,
                        android.R.layout.simple_list_item_1,
                        listItems);



                list.setAdapter(adapter);



                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {

                        Intent intent = new Intent(Profile.this, Event.class);
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


        mFirebaseDatabaseFriends = mFirebaseInstance.getReference("users").child(userID).child("friends");
        mFirebaseDatabaseFriends.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListView list = (ListView) findViewById(R.id.friends_list);

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {



                    mFirebaseInstance.getReference("users").child((String) snapshot.getValue()).child("nome").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            listFriends.add((String) dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    listFriendsIDs.add((String) snapshot.getValue());


                }

                adapterFriends=new ArrayAdapter<String>(Profile.this,
                        android.R.layout.simple_list_item_1,
                        listFriends);



                list.setAdapter(adapterFriends);



                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {

                        finish();
                        Intent intent = new Intent(Profile.this, Profile.class);
                        String message = listFriendsIDs.get(position);
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



    private boolean haveCameraPermission(){
        if (Build.VERSION.SDK_INT < 23)
            return true;
        return checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        // This is because the dialog was cancelled when we recreated the activity.
        if (permissions.length == 0 || grantResults.length == 0)
            return;

        switch (requestCode)
        {
            case 1:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startCamera();
                }
                else
                {
                    finish();
                }
            }
            break;
        }
    }


    public void startCamera()
    {
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void handleResult(Result result) {

        userFriendID = result.getText();

        mFirebaseInstance.getReference("users").child(userFriendID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("nome")) {
                    mScannerView.stopCamera();

                    mFirebaseInstance.getReference("users").child(userID).child("friends").child(String.valueOf(listFriends.size())).setValue(userFriendID);

                    mFirebaseInstance.getReference("users").child(userFriendID).child("friends").child(String.valueOf(dataSnapshot.child("friends").getChildrenCount())).setValue(userID);

                    Toast.makeText(getApplicationContext(),
                            "Friend added!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Profile.this, Profile.class);
                    String message = userID;
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(),
                            "Wrong QRCode!", Toast.LENGTH_SHORT).show();
                    startCamera();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    protected void addPhoto(View view){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){

                    try {
                        selectedImage = imageReturnedIntent.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        profilePhoto.setImageBitmap(bitmap);


                        mFirebaseStorageInstance = FirebaseStorage.getInstance();
                        mFirebaseStorage = mFirebaseStorageInstance.getReference("users");

                        mFirebaseStorage.child(userID).delete();

                        mFirebaseStorage.child(userID).putFile(selectedImage);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    try {
                        selectedImage = imageReturnedIntent.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        profilePhoto.setImageBitmap(bitmap);


                        mFirebaseStorageInstance = FirebaseStorage.getInstance();
                        mFirebaseStorage = mFirebaseStorageInstance.getReference("users");

                        mFirebaseStorage.child(userID).delete();
                        mFirebaseStorage.child(userID).putFile(selectedImage);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


}
