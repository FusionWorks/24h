package com.twentyfour.object;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by AGalkin on 12/7/13.
 */
public class FBItem {
    public String uid;
    public String name;
    public Drawable photo;
    public ArrayList<FBEvent> fbEvents;

    public FBItem(String uid, String name, ArrayList<FBEvent> fbEvents, Drawable photo){
        super();
        this.uid = uid;
        this.name = name;
        this.photo = photo;
        this.fbEvents = fbEvents;
    }
}
