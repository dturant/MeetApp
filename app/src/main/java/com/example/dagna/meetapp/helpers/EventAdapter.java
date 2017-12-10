package com.example.dagna.meetapp.helpers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dagna.meetapp.Event;
import com.example.dagna.meetapp.EventObject;
import com.example.dagna.meetapp.Events;
import com.example.dagna.meetapp.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dagna on 30/11/2017.
 */
public class EventAdapter extends ArrayAdapter<EventObject> {
    public final static String EXTRA_MESSAGE = "com.example.dagna.meetapp.MESSAGE";


    Context context;
    public EventAdapter(Context context, ArrayList<EventObject> list) {
        super(context, 0, list);
        this.context =context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final EventObject e = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.event_image);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView description = (TextView) convertView.findViewById(R.id.description);


        if(e.getImageRef()!=null) {
            Log.d("EventAdapter","ref not null");
            Glide.with(context).using(new FirebaseImageLoader()).load(e.getImageRef()).into(imageView);
        }

        //byte[] imageAsBytes = Base64.decode(e.getaPictureUri().getBytes(), Base64.DEFAULT);
        //imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

        title.setText(e.getName());
        description.setText(e.getDescription());



        return convertView;
    }
}
