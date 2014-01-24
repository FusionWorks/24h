package com.twentyfour.object;

import java.util.ArrayList;

/**
 * Created by AGalkin on 12/8/13.
 */
public class FBSortedEvent {
    public FBEvent event;
    public ArrayList<FBFriend> friends;

    public FBSortedEvent(FBEvent event, ArrayList<FBFriend> friends){
        super();
        this.event = event;
        this.friends = friends;
    }
}
