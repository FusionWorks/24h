//package com.twentyfour.async;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.facebook.Response;
//import com.facebook.model.GraphObject;
//import com.twentyfour.main.EventsActivity;
//import com.twentyfour.object.FBEvent;
//import com.twentyfour.utility.Utility;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Created by AGalkin on 12/16/13.
// */
//public class ATEventsId extends AsyncTask<Void, Void, Void> {
//    Response response;
//    EventsActivity activity;
//    String tempIds;
//    HashMap<String, ArrayList<FBEvent>> fbEventsMap;
//    public ATEventsId(Response response, EventsActivity activity, String tempIds, HashMap<String, ArrayList<FBEvent>> fbEventsMap){
//        super();
//        this.response = response;
//        this.activity = activity;
//        this.tempIds = tempIds;
//        this.fbEventsMap = fbEventsMap;
//    }
//
//    @Override
//    protected Void doInBackground(Void... params) {
//        GraphObject gr = response.getGraphObject();
//        try {
//            JSONArray arr = new JSONArray(gr.asMap().get("data").toString());
//            Log.v(Utility.TAG, "length " + arr.length());
//
//            for(int y=0; arr.length()>y;y++ ){
//                JSONObject object = new JSONObject(arr.get(y).toString());
//                ArrayList<FBEvent> fbEventArray;
//
//                if(fbEventsMap.containsKey(String.valueOf(object.getLong("uid")))){
//                    fbEventArray = fbEventsMap.get(String.valueOf(object.getLong("uid")));
//                }else{
//                    fbEventArray = new ArrayList<FBEvent>();
//                }
//                tempIds = tempIds + String.valueOf(object.getLong("eid")) + ", ";
//
//                FBEvent fbEvent = new FBEvent(String.valueOf(object.getLong("eid")), "", "", "", null, "", "", "", "");
//                fbEventArray.add(fbEvent);
//                fbEventsMap.put(String.valueOf(object.getLong("uid")),fbEventArray);
//
//            }
//
//            tempIds = tempIds.substring(0, tempIds.length()-2);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(Void params) {
//        activity.getEvents();
//    }
//
//    @Override
//    protected void onPreExecute() {
//    }
//
//}