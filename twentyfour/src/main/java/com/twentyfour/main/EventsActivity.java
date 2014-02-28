package com.twentyfour.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import com.twentyfour.async.ATFetchAll;
import com.twentyfour.async.ATUserInfo;
import com.twentyfour.menu.NavMenu;
import com.twentyfour.menu.SlideHolder;
import com.twentyfour.object.FBEvent;
import com.twentyfour.object.FBFriend;
import com.twentyfour.utility.PullToRefreshListView;
import com.twentyfour.utility.Reachability;
import com.twentyfour.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class EventsActivity extends Activity {
    RelativeLayout loadingView;
    String today;
    String tommorrow;
    ListView fbList ;
    String query;
    Button menuButton;
    SlideHolder mSlideHolder;
    public String[] friendsList;
    public String[] citiesList;
    public ArrayList<FBEvent> allArray = new ArrayList<FBEvent>();
    NavMenu navMenu;
    ArrayList<FBEvent> newArray;
    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "settings";
    boolean attending = false;
    boolean invite = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        MyTestFlightApp app = new MyTestFlightApp();
        app.onCreate();
        loadingView = (RelativeLayout)findViewById(R.id.loadingAnimationContent);
        fbList = (ListView)findViewById(R.id.FBList);
        menuButton = (Button)findViewById(R.id.menu_button);
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
                getAll();
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
        TestFlight.log("App onCreate");
        Reachability reachability = new Reachability(this);
        if(reachability.isOnline()){
            query = "SELECT uid, name, pic FROM user WHERE uid = me()";
            ATUserInfo ATUI = new ATUserInfo(query, EventsActivity.this, loadingView);
            ATUI.execute();
//            getEvents();
            getAll();
        }

    }

    public void setUserInfo(FBFriend user, Drawable pic){
        TextView userName = (TextView) findViewById(R.id.userName);
        ImageView userImage = (ImageView) findViewById(R.id.userThumb);

        userName.setText(user.name);
        userImage.setImageDrawable(pic);
    }

    public void getAll(){
        String fqlQuery = "";
        attending = mSettings.getBoolean("attending",false);
        invite = mSettings.getBoolean("invite",true);
        Log.v("24h", "attending "+ attending + "invite "+ invite);
        if (invite){
            fqlQuery = "{'event_member':'SELECT eid, uid FROM event_member WHERE uid IN (SELECT uid FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me())) AND (rsvp_status = \"attending\" OR rsvp_status = \"unsure\" OR rsvp_status = \"not_replied\") AND start_time >\""+ today +"\" AND start_time < \""+ tommorrow +"\"', 'user':'SELECT uid, name, pic_square FROM user WHERE uid IN (SELECT uid FROM #event_member)', 'event':'SELECT eid, name, description, pic_square, start_time, host, location, attending_count, venue.city FROM event WHERE eid IN (SELECT eid FROM #event_member)'}";
        }else if(attending){
            fqlQuery = "{'event_member':'SELECT eid, uid FROM event_member WHERE uid IN (SELECT uid FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me())) AND (rsvp_status = \"attending\" OR rsvp_status = \"unsure\") AND start_time >\""+ today +"\" AND start_time < \""+ tommorrow +"\"', 'user':'SELECT uid, name, pic_square FROM user WHERE uid IN (SELECT uid FROM #event_member)', 'event':'SELECT eid, name, description, pic_square, start_time, host, location, attending_count, venue.city FROM event WHERE eid IN (SELECT eid FROM #event_member) ORDER BY start_time ASC'}";
        }
        ATFetchAll ATED = new ATFetchAll(fqlQuery, EventsActivity.this, loadingView);
        ATED.execute();
    }

    public void initiateList(ArrayList<FBEvent> array, String[] uniqCities, String[] uniqFriends){
        this.citiesList = uniqCities;
        this.friendsList = uniqFriends;

        Log.v("24h", "cit "+citiesList.length + "fried "+friendsList.length);

        FBAdapter fbAdapter = new FBAdapter(this, array);
        fbList.setAdapter(fbAdapter);
        fbAdapter.notifyDataSetChanged();
        ((PullToRefreshListView) fbList).onRefreshComplete();
        mSlideHolder.setEnabled(true);
        loadingView.setVisibility(View.GONE);
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
        newArray = new ArrayList<FBEvent>();
        builderSingle.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setMultiChoiceItems(data, null, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialogInterface, int item, boolean isChecked) {
                String strName = arrayAdapter.getItem(item);
                if (item == 0) {
                    Log.v("24h", "allarray count " + allArray.size());
                    mSettings.edit().putString("cities", "").commit();
                    initiateList(allArray, citiesList, friendsList);
                    dialogInterface.dismiss();
                } else {
                    for (FBEvent event : allArray) {
                        if (cities && event.city.equals(strName)) {
                            newArray.add(event);
                        } else if (friends) {
                            for (FBFriend friend : event.friends) {
                                if (friend.name.equals(strName) && isChecked) {
                                    newArray.add(event);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("24h", "new list "+newArray.get(0).friends.get(0).name + "size "+newArray.size());

                String str = "";
                for(FBEvent fbSorted : newArray){
                    if(!str.contains(fbSorted.city)){
                        str += fbSorted.city+",";
                    }
                }
                mSettings.edit().putString("cities", str).commit();
                initiateList(newArray, citiesList, friendsList);
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
        newArray = new ArrayList<FBEvent>();
        if(str.length()>0){
            Log.v("24h","str " + str);
            for (FBEvent sorted : allArray) {
                Log.v("24h", "sort "+sorted.city);
                if(str.contains(sorted.city)){
                    newArray.add(sorted);
                    Log.v("24h", "sort "+sorted.city);
                }
            }
            FBAdapter fbAdapter = new FBAdapter(this, newArray);
            fbList.setAdapter(fbAdapter);
            fbAdapter.notifyDataSetChanged();
        }
    }




}
