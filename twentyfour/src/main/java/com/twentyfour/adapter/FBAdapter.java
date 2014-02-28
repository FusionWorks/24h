package com.twentyfour.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twentyfour.R;
import com.twentyfour.main.EventsActivity;
import com.twentyfour.object.FBEvent;
import com.twentyfour.object.FBFriend;
import com.twentyfour.utility.Utility;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FBAdapter extends ArrayAdapter<FBEvent> {
    private ArrayList<FBEvent> data;
    private EventsActivity activity;
    LinearLayout eventGoingPhotos;
//    LinearLayout eventGoingPhotosFirst;
    boolean first;
    public FBAdapter(EventsActivity activity, ArrayList<FBEvent> data) {
        super(activity, R.layout.activity_main,data);
        this.activity=activity;
        this.data=data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.list_item_event, parent, false);

        FBEvent item = data.get(position);
        ArrayList<FBFriend> friends = item.friends;
        TextView eventName = (TextView) rowView.findViewById(R.id.eventName);
        TextView eventCreator = (TextView) rowView.findViewById(R.id.eventCreator);
        TextView eventDesc = (TextView) rowView.findViewById(R.id.eventDesc);
        TextView eventTime = (TextView) rowView.findViewById(R.id.eventTime);
        TextView eventPlace = (TextView) rowView.findViewById(R.id.eventLocation);
        ImageView eventPic = (ImageView) rowView.findViewById(R.id.pic);
        TextView friendsName = (TextView) rowView.findViewById(R.id.friendsName);
        TextView alsoGoing = (TextView) rowView.findViewById(R.id.alsoGoing);

        eventGoingPhotos = (LinearLayout) rowView.findViewById(R.id.going);
//        eventGoingPhotosFirst = (LinearLayout) rowView.findViewById(R.id.goingFirst);
        try{
            RelativeLayout loading = (RelativeLayout)rowView.findViewById(R.id.loadingIcon);
            ATPhotoDownload ATPD = new ATPhotoDownload(null, item.pictureUrl, loading, "", eventPic);
            ATPD.execute();
        }catch(NullPointerException e){

        }
        eventName.setText(item.name);
        eventCreator.setText(item.creator);
        eventDesc.setText(item.desc);
        eventPlace.setText(" " + item.place);
        String time = item.time.substring(0, item.time.length()-4);
        eventTime.setText(" "+ fromDateToUnix(item.time));
        FBFriend fbFriend = friends.get(0);
        friendsName.setText(fbFriend.name);
        first = true;
        RelativeLayout loading = (RelativeLayout)rowView.findViewById(R.id.loading);
        int count = 0;
        for(FBFriend friend : friends){
            ATPhotoDownload ATPD = new ATPhotoDownload(eventGoingPhotos, friend.photoUrl, loading, friend.uid, null);
            ATPD.execute();
            count++;
            if(count>8) break;
        }
        alsoGoing.setText("and other " + item.attendingCount + " are going");

        return rowView;
    }

    public ImageView createImageView(String uid){
        Log.v("24h", "create ID "+uid);
        ImageView view = new ImageView(activity);
        view.setTag(uid);
        return view;
    }
    public void addImageToImageView(LinearLayout linear, String uid, Drawable image){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int height = (int) (50 * scale + 0.5f);
        int width = (int) (50 * scale + 0.5f);
        ImageView view = (ImageView)linear.findViewWithTag(uid);
        Log.v("24h", "add ID "+uid +"add "+view);
        view.setImageDrawable(image);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, height);
        lp.setMargins(0, 0, 5, 0);
        view.setLayoutParams(lp);
    }

    public void addIcon(ImageView view, Drawable pic){
        view.setImageDrawable(pic);
    }

    public String fromDateToUnix(String time){
        Log.v("24h", "time "+time);
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return "no time";
        }
        Log.v("24h", "date "+date);
        date = new Date(date.getTime());
        DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(activity);

        return dateFormat.format(date);
    }

    public class ATPhotoDownload  extends AsyncTask<Void, Void, Void> {
        RelativeLayout loadingView;
        String picUrl;
        Drawable pic;
        String uid;
        LinearLayout linear;
        ImageView image;
        public ATPhotoDownload(LinearLayout linear, String picUrl, RelativeLayout loadingView, String uid, ImageView image){
            super();
            this.picUrl = picUrl;
            this.loadingView = loadingView;
            this.uid = uid;
            this.linear = linear;
            this.image = image;
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                pic = Utility.drawableFromUrl(picUrl, activity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            loadingView.setVisibility(View.GONE);
            if(uid.length() >0 ){
                addImageToImageView(linear, uid, pic);
            }else{
                addIcon(image,pic);
            }
        }

        @Override
        protected void onPreExecute() {
            loadingView.setVisibility(View.VISIBLE);
            if(uid.length() >0 ){
                linear.addView(createImageView(uid));
            }
        }
    }
}
