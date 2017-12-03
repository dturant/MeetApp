package com.example.dagna.meetapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Calendar;

public class EventCreate extends AppCompatActivity {

    public LatLng eventLocation;
    public Intent intent;
    public ImageView eventPhoto;
    public EditText eventDate, eventTime, eventName,eventDescription;
    Spinner eventCategorySpinner;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private StorageReference mFirebaseStorage;
    private FirebaseStorage mFirebaseStorageInstance;

    private Uri selectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventcreate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventPhoto = (ImageView) findViewById(R.id.event_photo);
        eventName =(EditText) findViewById(R.id.event_name);
        eventDate =(EditText) findViewById(R.id.event_date);
        eventTime =(EditText) findViewById(R.id.event_time);
        eventDescription =(EditText) findViewById(R.id.event_description);

        eventCategorySpinner = (Spinner) findViewById(R.id.event_category);
        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, R.layout.spinner_item, Category.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventCategorySpinner.setAdapter(adapter);

        intent = getIntent();
        String[] locationString = intent.getStringExtra(MainActivity.EXTRA_MESSAGE).split(",");


        eventLocation = new LatLng(Double.parseDouble(locationString[0]), Double.parseDouble(locationString[1]));

    }

    public void createEvent(View view){

        String name = eventName.getText().toString();
        String date = eventDate.getText().toString();
        String time = eventTime.getText().toString();
        String description = eventDescription.getText().toString();


        SharedPreferences sharedPref = getSharedPreferences("userID", MODE_PRIVATE);
        String owner = sharedPref.getString("userID", null);

        String category = Category.valueOf(eventCategorySpinner.getSelectedItem().toString()).toString();




        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("markers");

        mFirebaseStorageInstance = FirebaseStorage.getInstance();
        mFirebaseStorage = mFirebaseStorageInstance.getReference("markers");


        String  markerID = mFirebaseDatabase.push().getKey();

        mFirebaseDatabase.child(markerID).child("title").setValue(name);
        mFirebaseDatabase.child(markerID).child("location").setValue(eventLocation);
        mFirebaseDatabase.child(markerID).child("date").setValue(date);
        mFirebaseDatabase.child(markerID).child("time").setValue(time);
        mFirebaseDatabase.child(markerID).child("description").setValue(description);
        mFirebaseDatabase.child(markerID).child("category").setValue(category);
        mFirebaseDatabase.child(markerID).child("owner").setValue(owner);
        //mFirebaseInstance.getReference("users").child(userID).child("friends").child(String.valueOf(listFriends.size())).setValue(result.getText());
        mFirebaseDatabase.child(markerID).child("users").child("0").setValue(owner);
        mFirebaseStorage.child(markerID).putFile(selectedImage);



        MarkerOptions marker = new MarkerOptions()
                .position(eventLocation)
                .title(markerID);

        intent.putExtra("marker",marker);

        setResult(Activity.RESULT_OK, intent);
        super.finish();

    }


    protected void addPhoto(View view){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){

                    try {
                        selectedImage = imageReturnedIntent.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        eventPhoto.setImageBitmap(bitmap);
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
                        eventPhoto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    protected void pickDate(View view){
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedmonth = selectedmonth + 1;
                eventDate.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }


    protected void pickTime(View view){

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                eventTime.setText( selectedHour + ":" + selectedMinute);

            }
        }, hour, minute, true);//Yes 24 hour eventTime
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }




}
