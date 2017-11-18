package com.example.dagna.meetapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dagna on 18.11.2017.
 */

public class MockupEventCreator {
    //TODO delete this class once database is running; this is just for creating hard-coded events

    public static List<EventObject> eventList(){
        List eventList = new ArrayList<EventObject>();

        String name = "Hiking";
        String date = "18/11/2017";
        String time = "16:30";
        String description = "Let's go hiking guys!";
        LatLng location = new LatLng(38.717794, -9.137274);
        String owner ="owner";
        //TODO add a working login of an event's creator
        Category category = Category.Sport;
        EventObject eventObject = new EventObject(name, date,time,description,location,owner,category);
        eventList.add(eventObject);

        name = "Karaoke";
        date = "30/11/2017";
        time = "16:30";
        description = "Super fun!";
        location = new LatLng(38.720111, -9.154117);
        owner ="owner";
        //TODO add a working login of an event's creator
        category = Category.Party;
        eventObject = new EventObject(name, date,time,description,location,owner,category);
        eventList.add(eventObject);

        return  eventList;

    }
}
