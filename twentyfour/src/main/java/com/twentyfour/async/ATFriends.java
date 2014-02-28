//package com.twentyfour.async;
//
//import android.graphics.drawable.Drawable;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.RelativeLayout;
//
//import com.facebook.HttpMethod;
//import com.facebook.Request;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.model.GraphObject;
//import com.testflightapp.lib.TestFlight;
//import com.twentyfour.main.EventsActivity;
//import com.twentyfour.object.FBEvent;
//import com.twentyfour.object.FBFriend;
//import com.twentyfour.object.FBSortedEvent;
//import com.twentyfour.utility.Utility;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//
///**
// * Created by AGalkin on 12/16/13.
// */
//public class ATFriends extends AsyncTask<Void, Void, Void> {
//    String query;
//    EventsActivity activity;
//    FBEvent fbEvent;
//    HashMap<FBEvent, ArrayList<FBFriend>> fbEventMap;
//    Iterator it;
//    RelativeLayout loadingView;
//    public ATFriends(String query, EventsActivity activity, FBEvent fbEvent, HashMap<FBEvent, ArrayList<FBFriend>> fbEventMap, Iterator it, RelativeLayout loadingView){
//        super();
//        this.query = query;
//        this.activity = activity;
//        this.fbEvent = fbEvent;
//        this.fbEventMap = fbEventMap;
//        this.it = it;
//        this.loadingView = loadingView;
//    }
//
//    @Override
//    protected Void doInBackground(Void... params) {
//
//        Log.v(Utility.TAG, "fqlQuery " + this.query);
//        TestFlight.log("fqlQuery " + this.query);
//
//        if(this.query.length()>0){
//            String fqlQuery = this.query;
//            Bundle bundle = new Bundle();
//            bundle.putString("q", fqlQuery);
//            Session session = Session.getActiveSession();
//            Log.v("24h", "session " + session);
//            TestFlight.log("session "+session);
//            Request request = new Request(session,
//                    "/fql",
//                    bundle,
//                    HttpMethod.GET,
//                    new Request.Callback(){
//                        public void onCompleted(Response response) {
//                            GraphObject gr = response.getGraphObject();
//                            try {
//                                JSONArray arr = new JSONArray(gr.asMap().get("data").toString());
//                                Log.v("CL", "arr "+arr);
//                                ArrayList<FBFriend> array = new ArrayList<FBFriend>();
//                                for(int i=0; arr.length()>i;i++){
//                                    JSONObject object = new JSONObject(arr.get(i).toString());
//                                    Drawable photo = null;
//                                    try {
//                                        photo = Utility.drawableFromUrl(object.getString("pic_square"), activity);
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                    FBFriend fbItem = new FBFriend(String.valueOf(object.getLong("uid")), object.getString("name"), photo);
//                                    array.add(fbItem);
//                                    activity.friends.add(object.getString("name"));
//
//                                }
//
//                                fbEventMap.put(fbEvent, array);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//            Request.executeAndWait(request);
//        }
//
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(Void params) {
//        Log.v("24h", "name " + fbEventMap.get(fbEvent).get(0).name);
//
//        if(it.hasNext()){
////            HashMap.Entry<FBEvent, ArrayList<FBFriend>> fbEvent = (HashMap.Entry)it.next();
////            Log.v("24H", fbEvent.getKey().name);
//            activity.getFriends(fbEventMap,it);
//        }else{
//            ArrayList<FBSortedEvent> fbSortedEventArray = new ArrayList<FBSortedEvent>();
//            Iterator it = fbEventMap.entrySet().iterator();
//            while (it.hasNext()) {
//                HashMap.Entry<FBEvent, ArrayList<FBFriend>> fbEvent = (HashMap.Entry)it.next();
//                Log.v("24h","friends "+fbEvent.getValue());
//                FBSortedEvent fbSortedEvent = new FBSortedEvent(fbEvent.getKey(), fbEvent.getValue());
//                activity.cities.add(fbSortedEvent.event.city);
//                fbSortedEventArray.add(fbSortedEvent);
//            }
//            Log.v("24h","cities -- "+activity.cities);
//
//            Log.v("24h","friends --- "+activity.friends);
//
//            activity.allArray = fbSortedEventArray;
//            activity.initiateList(fbSortedEventArray, activity.cities.toArray(new String[activity.cities.size()]), activity.friends.toArray(new String[activity.friends.size()]));
//            activity.fbSorted = fbSortedEventArray;
//            loadingView.setVisibility(View.GONE);
//        }
//
//    }
//
//    @Override
//    protected void onPreExecute() {
//    }
//
//}