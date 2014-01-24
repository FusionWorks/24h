package com.twentyfour.object;

import android.graphics.drawable.Drawable;

/**
 * Created by AGalkin on 12/8/13.
 */
public class FBEvent {
    public String eid;
    public String name;
    public String desc;
    public String time;
    public Drawable picture;
    public String creator;
    public String place;
    public String attendingCount;
    public String city;
    public FBEvent(String eid, String name, String desc, String time, Drawable picture, String creator, String place, String attendingCount, String city){
        super();
        this.eid = eid;
        this.name = name;
        this.desc = desc;
        this.time = time;
        this.picture = picture;
        this.creator = creator;
        this.place = place;
        this.attendingCount = attendingCount;
        this.city = city;
    }
}
