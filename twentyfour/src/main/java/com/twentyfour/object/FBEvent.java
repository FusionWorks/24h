package com.twentyfour.object;

import java.util.ArrayList;

/**
 * Created by AGalkin on 12/8/13.
 */
public class FBEvent {
    public String eid;
    public String name;
    public String desc;
    public String time;
    public String pictureUrl;
    public String creator;
    public String place;
    public String attendingCount;
    public String city;
    public ArrayList<FBFriend> friends;
    public FBEvent(String eid, String name, String desc, String time, String pictureUrl, String creator, String place, String attendingCount, String city, ArrayList<FBFriend> friends){
        super();
        this.eid = eid;
        this.name = name;
        this.desc = desc;
        this.time = time;
        this.pictureUrl = pictureUrl;
        this.creator = creator;
        this.place = place;
        this.attendingCount = attendingCount;
        this.city = city;
        this.friends = friends;
    }
}
