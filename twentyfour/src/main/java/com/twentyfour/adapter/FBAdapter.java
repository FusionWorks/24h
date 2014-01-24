package com.twentyfour.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twentyfour.R;
import com.twentyfour.main.EventsActivity;
import com.twentyfour.object.FBEvent;
import com.twentyfour.object.FBFriend;
import com.twentyfour.object.FBSortedEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FBAdapter extends ArrayAdapter<FBSortedEvent> {
    private ArrayList<FBSortedEvent> data;
    private EventsActivity activity;

    public FBAdapter(EventsActivity activity, ArrayList<FBSortedEvent> data) {
        super(activity, R.layout.activity_main,data);
        this.activity=activity;
        this.data=data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_item_event, parent, false);


        FBSortedEvent item = data.get(position);
        FBEvent event = item.event;
        ArrayList<FBFriend> friends = item.friends;

        TextView eventName = (TextView) rowView.findViewById(R.id.eventName);
        TextView eventCreator = (TextView) rowView.findViewById(R.id.eventCreator);
        TextView eventDesc = (TextView) rowView.findViewById(R.id.eventDesc);
        TextView eventTime = (TextView) rowView.findViewById(R.id.eventTime);
        TextView eventPlace = (TextView) rowView.findViewById(R.id.eventLocation);
        ImageView eventPic = (ImageView) rowView.findViewById(R.id.pic);
        TextView friendsName = (TextView) rowView.findViewById(R.id.friendsName);
        TextView alsoGoing = (TextView) rowView.findViewById(R.id.alsoGoing);

        LinearLayout eventGoingPhotos = (LinearLayout) rowView.findViewById(R.id.going);

        try{
            eventPic.setImageDrawable(event.picture);
        }catch(NullPointerException e){

        }
        eventName.setText(event.name);
        eventCreator.setText(event.creator);
        eventDesc.setText(event.desc);
        eventPlace.setText(" " + event.place);
        String time = event.time.substring(0, event.time.length()-4);
        eventTime.setText(" "+ fromDateToUnix(time));
        FBFriend fbFriend = friends.get(0);
        friendsName.setText(fbFriend.name);
        for(FBFriend friend : friends){
            eventGoingPhotos.addView(createImageView(friend.photo));
        }
        alsoGoing.setText("and other " + event.attendingCount + " are going");

        return rowView;
    }

//    public TextView createTextView(String text){
//        TextView view = new TextView(activity);
//        view.setText(text);
//        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 30));
//        view.setTextSize(14.f);
//        view.setBackgroundColor(Color.parseColor("#009933"));
//        view.setPadding(5, 2, 5, 2);
//        view.setTextColor(Color.parseColor("#ffffff"));
//        return view;
//    }

    public ImageView createImageView(Drawable image){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int height = (int) (50 * scale + 0.5f);
        int width = (int) (50 * scale + 0.5f);
        ImageView view = new ImageView(activity);
        view.setImageDrawable(image);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.setMargins(0, 0, 5, 0);
        view.setLayoutParams(lp);
        return view;
    }

    public String fromDateToUnix(String time){
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        date = new Date(date.getTime());
        DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(activity);

        return dateFormat.format(date);
    }
}
