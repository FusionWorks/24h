package com.twentyfour.async;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by AGalkin on 2/21/14.
 */
public class ATFetchAll extends AsyncTask<Void, Void, Void> {
    EventsActivity activity;
    RelativeLayout loadingView;
    JSONArray users;
    JSONArray events;
    JSONArray eventMembers;
    String fqlQuery;
    Set<String> citiesHash;
    Set<String> friendsHash;
    ArrayList<FBEvent> eventsArray;
    public ATFetchAll(String fqlQuery, EventsActivity activity, RelativeLayout loadingView){
        super();
        this.fqlQuery = fqlQuery;
        this.activity = activity;
        this.loadingView = loadingView;
        citiesHash = new HashSet<String>();
        friendsHash = new HashSet<String>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        citiesHash.add("1st All cities");
        friendsHash.add("1st All friends");
        eventsArray = new ArrayList<FBEvent>();
        Log.v("24h", "fqlQuery " + fqlQuery);
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

                            for(int i=0; arr.length()>i; i++ ){
                                JSONObject obj = arr.getJSONObject(i);
                                if(obj.getString("name").equals("event_member")){
                                    eventMembers = obj.getJSONArray("fql_result_set");
                                }
                                if(obj.getString("name").equals("event")){

                                    events = obj.getJSONArray("fql_result_set");
                                }
                                if(obj.getString("name").equals("user")){
                                    users = obj.getJSONArray("fql_result_set");
                                }
                            }
                            for(int y=0; y<events.length(); y++){
                                JSONObject object = new JSONObject(events.get(y).toString());
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

                                String picture = null;
                                try{
                                    picture = object.getString("pic_square");
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
                                    JSONArray empty = object.getJSONArray("venue");
                                }catch (JSONException e){
                                    try{
                                        city = object.getJSONObject("venue").getString("city");
                                        citiesHash.add(city);
                                    }catch(NullPointerException e2){

                                    }
                                }
                                ArrayList<FBFriend> friends = new ArrayList<FBFriend>();
                                for(int z=0; z<eventMembers.length(); z++){
                                    JSONObject obj = eventMembers.getJSONObject(z);
                                    if(obj.getString("eid").equals(eid)){
                                        String uid = obj.getString("uid");
                                        for(int i=0; i<users.length(); i++){
                                            JSONObject obj2 = users.getJSONObject(i);
                                            friendsHash.add(obj2.getString("name"));
                                            if(uid.equals(obj2.getString("uid"))){
                                                String picUrl = obj2.getString("pic_square");
                                                FBFriend friend = new FBFriend(uid, obj2.getString("name"), picUrl);
                                                friends.add(friend);
                                            }
                                        }
                                    }
                                }
                                FBEvent event = new FBEvent(eid, name, desc, time, picture, creator, place, attendingCount, city, friends);
                                eventsArray.add(event);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Request.executeAndWait(request);
        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
            loadingView.setVisibility(View.GONE);
            if(eventsArray.size() < 1){
                activity.showAlert();
            }
            String[] cities = citiesHash.toArray(new String[citiesHash.size()]);
            String[] friends = friendsHash.toArray(new String[friendsHash.size()]);
//            Log.v("24h","citiesHash " + citiesHash + "cities "+cities);
//            Log.v("24h","friendsHash " + friendsHash + "friends "+friends);
            activity.allArray = eventsArray;
            activity.initiateList(eventsArray, cities, friends);
            activity.showWithFilter();

    }

    @Override
    protected void onPreExecute() {
        loadingView.setVisibility(View.VISIBLE);
    }
}
