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
import com.facebook.android.Util;
import com.facebook.model.GraphObject;
import com.testflightapp.lib.TestFlight;
import com.twentyfour.main.EventsActivity;
import com.twentyfour.object.FBFriend;
import com.twentyfour.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by AGalkin on 1/4/14.
 */
public class ATUserInfo extends AsyncTask<Void, Void, Void> {
    public String query;
    public EventsActivity activity;
    public RelativeLayout loadingView;
    FBFriend userInfo;
    public ATUserInfo(String query, EventsActivity activity, RelativeLayout loadingView){
        super();
        this.query = query;
        this.activity = activity;
        this.loadingView = loadingView;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.v(Utility.TAG, "fqlQuery " + this.query);
        TestFlight.log("fqlQuery " + this.query);

        if(this.query.length()>0){
            String fqlQuery = this.query;
            Bundle bundle = new Bundle();
            bundle.putString("q", fqlQuery);
            Session session = Session.getActiveSession();
            Log.v("24h", "session " + session);
            TestFlight.log("session "+session);
            Request request = new Request(session,
                    "/fql",
                    bundle,
                    HttpMethod.GET,
                    new Request.Callback(){
                        public void onCompleted(Response response) {
                            GraphObject gr = response.getGraphObject();
                            try {
                                JSONArray arr = new JSONArray(gr.asMap().get("data").toString());
                                Log.v("CL", "arr "+arr);
                                JSONObject obj = new JSONObject(arr.getJSONObject(0).toString());
                                Drawable pic = Utility.drawableFromUrl(obj.getString("pic"), activity);
                                userInfo = new FBFriend(obj.getString("uid"), obj.getString("name"), pic);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Request.executeAndWait(request);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void params) {
        activity.setUserInfo(userInfo);
    }
}
