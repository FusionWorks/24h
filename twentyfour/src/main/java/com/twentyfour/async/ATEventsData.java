package com.twentyfour.async;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.testflightapp.lib.TestFlight;
import com.twentyfour.main.EventsActivity;
import com.twentyfour.object.FBEvent;
import com.twentyfour.object.FBFriend;
import com.twentyfour.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by AGalkin on 12/16/13.
 */
public class ATEventsData extends AsyncTask<Void, Void, Void> {
    EventsActivity activity;
    RelativeLayout loadingView;
    HashMap<FBEvent, ArrayList<FBFriend>> fbEventsMap;
    String fqlQuery;
    public ATEventsData(String fqlQuery, EventsActivity activity, HashMap<FBEvent, ArrayList<FBFriend>> fbEventsMap, RelativeLayout loadingView){
        super();
        this.fqlQuery = fqlQuery;
        this.activity = activity;
        this.fbEventsMap = fbEventsMap;
        this.loadingView = loadingView;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.v("24h","fqlQuery " + fqlQuery);
        TestFlight.log("fqlQuery " + fqlQuery);
        Bundle bundle = new Bundle();
        bundle.putString("q", fqlQuery);
        Session session = Session.getActiveSession();
        Request request = new Request(session,
                "/fql",
                bundle,
                HttpMethod.GET,
                new Request.Callback(){
                    public void onCompleted(Response response) {
                        GraphObject gr = response.getGraphObject();
                        try {
                            Log.v("24H", "obj "+gr.asMap().get("data").toString());
                            JSONArray arr = new JSONArray(gr.asMap().get("data").toString());
                            for(int y=0; arr.length()>y;y++ ){
                                JSONObject object = new JSONObject(arr.get(y).toString());
                                Log.v("24h", "24h object " + object);
                                String eid = String.valueOf(object.getLong("eid"));
                                String name = "no name";
                                try {
                                    name = object.getString("name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String desc = object.getString("description");

                                String time = "No start time";
                                try {
                                    time = object.getString("start_time");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Drawable picture = null;
                                try{
                                    picture = Utility.drawableFromUrl(object.getString("pic_square"), activity);
                                }catch(NullPointerException e){

                                }

                                String creator = "no host";
                                try{
                                    creator = object.getString("host");
                                }catch(NullPointerException e){

                                }

                                String place = "no location";
                                try{
                                    place = object.getString("location");
                                }catch(NullPointerException e){

                                }

                                String attendingCount = "0";
                                try{
                                    attendingCount = String.valueOf(object.getInt("attending_count"));
                                }catch(NullPointerException e){

                                }

                                String city = "no city";
                                try{
                                    city = object.getJSONObject("venue").getString("city");

                                }catch(NullPointerException e){

                                }

                                FBEvent event = new FBEvent(eid, name, desc, time, picture, creator, place, attendingCount, city);
                                fbEventsMap.put(event, null);
                            }

                            Log.v(Utility.TAG, "length eventsMap " + fbEventsMap.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Request.executeAndWait(request);
//
//        ArrayList<FBItem> newArray = new ArrayList<FBItem>();
//        for(int i=0; array.size()>i;i++){
//            ArrayList<FBEvent> events = new ArrayList<FBEvent>();
//            if(fbEventsMap.get(array.get(i).uid)!=null){
//                events = fbEventsMap.get(array.get(i).uid);
//                for(int z=0;z<events.size();z++){
//                    if (eventsMap.containsKey(events.get(z).eid)){
//                        FBEvent tmpEvent = eventsMap.get((events.get(z).eid));
//
//                        String name = tmpEvent.name;
//                        String desc = tmpEvent.desc;
//                        String time = tmpEvent.time;
//                        Drawable picture = tmpEvent.picture;
//                        String creator = tmpEvent.creator;
//                        String place = tmpEvent.place;
//                        String attendingCount = tmpEvent.attendingCount;
//
//                        FBEvent newFbEvent = new FBEvent(events.get(z).eid, name, desc, time, picture, creator, place, attendingCount);
//                        events.set(z, newFbEvent);
//                    }
//                }
//            }
//            if (events.size()>0){
//                FBItem fbItem = new FBItem(array.get(i).uid, array.get(i).name, events, array.get(i).photo);
//                newArray.add(fbItem);
//            }
//
//        }
//        array = new ArrayList<FBItem>(newArray);
        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        if(fbEventsMap.size()>0){
            Iterator it = fbEventsMap.entrySet().iterator();
            activity.getFriends(fbEventsMap, it);
        }else{
//            loadingView.setVisibility(View.GONE);
            activity.showAlert();
        }
    }

    @Override
    protected void onPreExecute() {
        loadingView.setVisibility(View.VISIBLE);
    }

}