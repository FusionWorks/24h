package com.twentyfour.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testflightapp.lib.TestFlight;
import com.twentyfour.R;
import com.twentyfour.adapter.FBAdapter;
import com.twentyfour.async.ATEventsData;
import com.twentyfour.async.ATFriends;
import com.twentyfour.async.ATUserInfo;
import com.twentyfour.menu.NavMenu;
import com.twentyfour.menu.SlideHolder;
import com.twentyfour.object.FBEvent;
import com.twentyfour.object.FBFriend;
import com.twentyfour.object.FBItem;
import com.twentyfour.object.FBSortedEvent;
import com.twentyfour.utility.PullToRefreshListView;
import com.twentyfour.utility.Reachability;
import com.twentyfour.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class EventsActivity extends Activity {
    public ArrayList<FBItem> array;
    public HashMap<FBEvent, ArrayList<FBFriend>> fbEventsMap;
    RelativeLayout loadingView;
    String today;
    String tommorrow;
    ListView fbList ;
    String query;
    int progressStatus = 0;
    Button menuButton;
    SlideHolder mSlideHolder;
    FBEvent fbEvent;
    boolean last;
    String[] friendsList;
    String[] citiesList;
    public ArrayList<FBSortedEvent> fbSorted = new ArrayList<FBSortedEvent>();
    public ArrayList<FBSortedEvent> allArray = new ArrayList<FBSortedEvent>();
    NavMenu navMenu;
    ArrayList<FBSortedEvent> newSorted;
    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "settings";
    public Set<String> cities = new HashSet<String>();
    public Set<String> friends = new HashSet<String>();
    boolean attending = false;
    boolean invite = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        MyTestFlightApp app = new MyTestFlightApp();
        app.onCreate();
        array = new ArrayList<FBItem>();
        fbEventsMap = new HashMap<FBEvent, ArrayList<FBFriend>>();
        loadingView = (RelativeLayout)findViewById(R.id.loadingAnimationContent);
        fbList = (ListView)findViewById(R.id.FBList);
        menuButton = (Button)findViewById(R.id.menu_button);
//        menuButton.getBackground().setAlpha(100);
        getTimeInterval();
        mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);
        mSlideHolder.setEnabled(false);
        mSlideHolder.setAllowInterceptTouch(false);
        navMenu = new NavMenu(this, mSlideHolder, 0);
        final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fading_in_menu_button);
        final Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fading_out_menu_button);

        ((PullToRefreshListView) fbList).setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fbEventsMap.clear();
                array.clear();
                getEvents();
                mSlideHolder.setEnabled(false);
            }
        });

        fbList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
            // TODO Auto-generated method stub
            Log.d("############","Items " + position );

            }
        });
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navMenu.showMenu(null);
            }
        });
//        mSlideHolder.setOnSlideListener(new SlideHolder.OnSlideListener() {
//            @Override
//            public void onSlideCompleted(boolean opened) {
//            if(opened){
//                menuButton.getBackground().setAlpha(255);
//            }else{
//                menuButton.getBackground().setAlpha(140);
//            }
//            }
//        });
        TestFlight.log("App onCreate");
        Reachability reachability = new Reachability(this);
        if(reachability.isOnline()){
            query = "SELECT uid, name, pic FROM user WHERE uid = me()";
            ATUserInfo ATUI = new ATUserInfo(query, EventsActivity.this, loadingView);
            ATUI.execute();
            getEvents();
        }

    }

    public void setUserInfo(FBFriend user){
        TextView userName = (TextView) findViewById(R.id.userName);
        ImageView userImage = (ImageView) findViewById(R.id.userThumb);

        userName.setText(user.name);
        userImage.setImageDrawable(user.photo);
    }

    public void getFriends(HashMap<FBEvent, ArrayList<FBFriend>> fbEventsMap, Iterator it){
        this.fbEventsMap = fbEventsMap;
        HashMap.Entry<FBEvent, ArrayList<FBFriend>> fbEvent = (HashMap.Entry)it.next();
        last = true;
        Log.v("24h", "fbEvent "+fbEvent);
        TestFlight.log("fbEvent "+fbEvent);
        this.fbEvent = fbEvent.getKey();

        if(attending){
            this.query = "SELECT uid, name, pic_square FROM user WHERE uid IN " +
                    "(SELECT uid FROM event_member WHERE eid IN ("+ this.fbEvent.eid +") " +
                    "AND uid IN (SELECT uid FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me())) " +
                    "AND (rsvp_status = \"attending\" OR rsvp_status = \"unsure\"))";
        }else if(invite){
            this.query = "SELECT uid, name, pic_square FROM user WHERE uid IN " +
                    "(SELECT uid FROM event_member WHERE eid IN ("+ this.fbEvent.eid +") " +
                    "AND uid IN (SELECT uid FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me())) " +
                    "AND (rsvp_status = \"attending\" OR rsvp_status = \"unsure\" OR rsvp_status = \"not_replied\" ))";
        }

        ATFriends operation = new ATFriends(query, EventsActivity.this, EventsActivity.this.fbEvent, EventsActivity.this.fbEventsMap, it, loadingView);
        operation.execute();

    }

    public void getEvents(){
        attending = mSettings.getBoolean("attending",false);
        invite = mSettings.getBoolean("invite",true);
        friends.clear();
        cities.clear();
        cities.add("All cities");
        friends.add("1st all friends");
        fbEventsMap.clear();
        String fqlQuery = "";
        if (invite){
            fqlQuery = "SELECT eid, name, description, pic_square, start_time, host, location, attending_count, venue.city FROM event WHERE eid IN " +
                    "(SELECT eid, uid FROM event_member WHERE uid IN " +
                    "(SELECT uid FROM user WHERE uid IN " +
                    "(SELECT uid2 FROM friend WHERE uid1 = me())) " +
                    "AND (rsvp_status = \"attending\" OR rsvp_status = \"unsure\" OR rsvp_status = \"not_replied\" )  " +
                    "AND start_time >'"+today+"' AND start_time < '"+tommorrow+"')  " +
                    "AND start_time >'"+today+"' AND start_time < '"+tommorrow+"'";
        }else if(attending){
            fqlQuery = "SELECT eid, name, description, pic_square, start_time, host, location, attending_count, venue.city FROM event WHERE eid IN " +
                    "(SELECT eid, uid FROM event_member WHERE uid IN " +
                    "(SELECT uid FROM user WHERE uid IN " +
                    "(SELECT uid2 FROM friend WHERE uid1 = me())) " +
                    "AND (rsvp_status = \"attending\" OR rsvp_status = \"unsure\")  " +
                    "AND start_time >'"+today+"' AND start_time < '"+tommorrow+"')  " +
                    "AND start_time >'"+today+"' AND start_time < '"+tommorrow+"'";
        }
//        }else if (selection.equals("my_city")){
//            GPSTracker GPST = new GPSTracker(this);
//            if (GPST.canGetLocation())
//            {
//                String city = GPST.getLocality(this);
//                fqlQuery = "SELECT eid, name, description, pic_square, start_time, host, location, attending_count, venue.city FROM event WHERE eid IN " +
//                        "(SELECT eid, uid FROM event_member WHERE uid IN " +
//                        "(SELECT uid FROM user WHERE uid IN " +
//                        "(SELECT uid2 FROM friend WHERE uid1 = me()) AND hometown_location.city == '" + city + "') " +
//                        "AND (rsvp_status = \"attending\" OR rsvp_status = \"unsure\")  " +
//                        "AND start_time >'"+today+"' AND start_time < '"+tommorrow+"')  " +
//                        "AND start_time >'"+today+"' AND start_time < '"+tommorrow+"'";
//            }else{
//                Utility.alertView("Can't get GPS access", this);
//            }
//
//        }

        ATEventsData ATED = new ATEventsData(fqlQuery, EventsActivity.this, EventsActivity.this.fbEventsMap, loadingView);
        ATED.execute();

    }

    public void initiateList(ArrayList<FBSortedEvent> array, String[] uniqCities, String[] uniqFriends){
        this.citiesList = uniqCities;
        this.friendsList = uniqFriends;

        progressStatus = 0;
        FBAdapter fbAdapter = new FBAdapter(this, array);
        fbList.setAdapter(fbAdapter);
        fbAdapter.notifyDataSetChanged();
        ((PullToRefreshListView) fbList).onRefreshComplete();
        mSlideHolder.setEnabled(true);
        loadingView.setVisibility(View.GONE);
        showWithFilter();
    }

    public void getTimeInterval(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        today = (String)dateFormat.format(cal.getTime());
//        today += ":00:00";
        Log.v("24h", "today "+today);
        TestFlight.log("today "+today);

        cal.add(Calendar.DATE,1);  // number of days to add
        tommorrow = (String)(dateFormat.format(cal.getTime()));
//        tommorrow += ":00:00";
        Log.v("24h", "today "+tommorrow);
        TestFlight.log("today " + tommorrow);
    }

    public ArrayList<FBSortedEvent> sortEvents(ArrayList<FBItem> items){
        ArrayList<FBSortedEvent> array = new ArrayList<FBSortedEvent>();
        HashMap<String, FBEvent> sortedHash = new HashMap<String, FBEvent>();
        for(FBItem item : items){
            ArrayList<FBEvent> events = item.fbEvents;
            for(FBEvent event : events){
                if(!sortedHash.containsKey(event.eid)){
                    sortedHash.put(event.eid, event);
                    FBSortedEvent sortedEvents = new FBSortedEvent(event,null);
                    array.add(sortedEvents);
                }
            }
        }

        ArrayList<FBSortedEvent> tmpArray = new ArrayList<FBSortedEvent>();
        for(int i=0;i<array.size();i++){
            FBSortedEvent sortedEvents =  array.get(i);
            ArrayList<FBFriend> friends = new ArrayList<FBFriend>();
            for(FBItem item : items){
                ArrayList<FBEvent> events = item.fbEvents;
                for(FBEvent event : events){
                    if(event.name.equals(sortedEvents.event.name)){
                        FBFriend fbFriend = new FBFriend(item.uid, item.name, item.photo);
                        friends.add(fbFriend);
                    }
                }
            }
            FBSortedEvent newSortedItem = new FBSortedEvent(sortedEvents.event, friends);
            tmpArray.add(newSortedItem);
        }

        return tmpArray;
    }

//    public void loadingView(int i){
//        mSlideHolder.setEnabled(false);
//        final Handler handler = new Handler();
//        progressStatus = i;
//        new Thread(new Runnable() {
//
//            public void run() {
//                while (progressStatus == 1) {
//                    loadingView.setVisibility(View.VISIBLE);
//
//                }
//            }
//
//        }).start();
//
//    }

    public void showAlert(){
        Utility.alertView("No events upcoming now",this);
    }

    public void openChoice(final Boolean friends, final Boolean cities){
        String title = "";
        String[] data = null;
        if (friends){
            title = "Select a friend to follow";
            data = friendsList;
        }else if (cities){
            title = "Select a city for events";
            data = citiesList;
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                EventsActivity.this);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(title);
        Arrays.sort(data);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                EventsActivity.this,
                android.R.layout.select_dialog_multichoice, data);
        newSorted = new ArrayList<FBSortedEvent>();
        builderSingle.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

//        builderSingle.setAdapter(arrayAdapter,
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ArrayList<FBSortedEvent> newSorted = new ArrayList<FBSortedEvent>();
//                        String strName = arrayAdapter.getItem(which);
//
//                        if(which == 0){
//                            newSorted = fbSorted;
//                        }else{
//                            for(FBSortedEvent sorted : fbSorted){
//                                if (cities && sorted.event.city.equals(strName)){
//                                    newSorted.add(sorted);
//                                }
//                                else if(friends){
//                                    for(FBFriend friend : sorted.friends){
//                                        if(friend.name.equals(strName)){
//                                            newSorted.add(sorted);
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        initiateList(newSorted, citiesList, friendsList);
//                    }
//                });
//        builderSingle.show();
        builderSingle.setMultiChoiceItems(data, null, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialogInterface, int item, boolean isChecked) {
                String strName = arrayAdapter.getItem(item);
                if (item == 0) {
                    Log.v("24h", "allarray count " + allArray.size());
                    mSettings.edit().putString("cities", "").commit();
                    initiateList(allArray, citiesList, friendsList);
                    dialogInterface.dismiss();
                } else {
                    for (FBSortedEvent sorted : fbSorted) {
                        if (cities && sorted.event.city.equals(strName)) {
                            newSorted.add(sorted);
                        } else if (friends) {
                            for (FBFriend friend : sorted.friends) {
                                if (friend.name.equals(strName) && isChecked) {
                                    newSorted.add(sorted);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
//        builderSingle.setAdapter(arrayAdapter,
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                     }
//                });
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("24h", "new list "+newSorted.get(0).friends.get(0).name + "size "+newSorted.size());

                String str = "";
                for(FBSortedEvent fbSorted : newSorted){
                    if(!str.contains(fbSorted.event.city)){
                        str += fbSorted.event.city+",";
                    }
                }
                mSettings.edit().putString("cities", str).commit();

                initiateList(newSorted, citiesList, friendsList);
                dialog.dismiss();
            }
        });
        builderSingle.show();

    }

    public String getFilterIfExists(){
        String str = mSettings.getString("cities", "");
        if (str.length() == 0) return "";
        String[] strArray = str.split(",");
        str = "";
        for(String s : strArray)
            str += s;

        return str;
    }

    public void showWithFilter(){
        String str = getFilterIfExists();
        newSorted = new ArrayList<FBSortedEvent>();
        if(str.length()>0){
            Log.v("24h","str " + str);
            for (FBSortedEvent sorted : allArray) {
                Log.v("24h", "sort "+sorted.event.city);
                if(str.contains(sorted.event.city)){
                    newSorted.add(sorted);
                    Log.v("24h", "sort "+sorted.event.city);
                }
            }
            FBAdapter fbAdapter = new FBAdapter(this, newSorted);
            fbList.setAdapter(fbAdapter);
            fbAdapter.notifyDataSetChanged();
        }
    }

}
