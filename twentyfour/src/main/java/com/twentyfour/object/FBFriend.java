package com.twentyfour.object;

import android.graphics.drawable.Drawable;

/**
 * Created by AGalkin on 12/17/13.
 */
public class FBFriend {
    public String uid;
    public String name;
    public Drawable photo;
    public FBFriend(String uid, String name, Drawable photo){
        super();
        this.uid = uid;
        this.name = name;
        this.photo = photo;
    }
}
