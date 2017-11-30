package com.example.dagna.meetapp.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dagna.meetapp.EventObject;
import com.example.dagna.meetapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dagna on 30/11/2017.
 */
public class EventAdapter extends ArrayAdapter<EventObject> {
    List list = new ArrayList();
    public EventAdapter(Context context, int resource) {
        super(context, resource);
    }


    public void add(EventObject object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public EventObject getItem(int position) {
        return (EventObject)list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row=convertView;
        EventHolder eventHolder;
        if(row==null){
            LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.event_list_item,parent,false);
            eventHolder= new EventHolder();
            eventHolder.event_name= (TextView) row.findViewById(R.id.title);
            eventHolder.event_description= (TextView) row.findViewById(R.id.description);
            eventHolder.image=(ImageView) row.findViewById(R.id.event_image);
            row.setTag(eventHolder);

        }
        else{
            eventHolder=(EventHolder) row.getTag();
        }

        EventObject events = (EventObject)this.getItem(position);
        eventHolder.event_name.setText(events.getName());
        eventHolder.event_description.setText(events.getDescription());
        eventHolder.image.setImageResource(events.getImageId());

        return row;
    }


    static class EventHolder{
        TextView event_name, event_description;
        ImageView image;
    }
}
