package com.twentyfour.main;

import android.app.Application;
import android.util.Log;

import com.testflightapp.lib.TestFlight;

/**
 * Created by AGalkin on 12/29/13.
 */
public class MyTestFlightApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize TestFlight with your app token.
        TestFlight.takeOff(this, "3afe1fdb-e160-41db-8e35-7f1b09349987");
        Log.v("24h","TestFlight ON");
    }

}
