package com.twentyfour.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

import com.twentyfour.R;
import com.twentyfour.adapter.FBAdapter;
import com.twentyfour.async.ATFetchAll;
import com.twentyfour.async.ATUserInfo;
import com.twentyfour.menu.NavMenu;
import com.twentyfour.object.FBEvent;
import com.twentyfour.object.FBFriend;
import com.twentyfour.utility.PullToRefreshListView;
import com.twentyfour.utility.Reachability;
import com.twentyfour.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;


public class EventsActivity extends Activity {
    RelativeLayout loadingView;
    String today;
    String tommorrow;
    ListView fbList ;
    String query;
    Button menuButton;
    public String[] friendsList;
    public String[] citiesList;
    public ArrayList<FBEvent> allArray = new ArrayList<FBEvent>();
    NavMenu navMenu;
    ArrayList<FBEvent> newArray;
    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "settings";
    boolean attending = false;
    boolean invite = false;
    HashMap<String, Boolean> tempTitles = new HashMap<String, Boolean>();
    String[] dialogData = null;
    HashMap<String, Boolean> citiesStaticFilter = new HashMap<String, Boolean>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        loadingView = (RelativeLayout)findViewById(R.id.loadingAnimationContent);
        fbList = (ListView)findViewById(R.id.FBList);
        menuButton = (Button)findViewById(R.id.menu_button);
        getTimeInterval();
        navMenu = new NavMenu(this, 0);
        final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fading_in_menu_button);
        final Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fading_out_menu_button);

        ((PullToRefreshListView) fbList).setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAll();
            }
        });

        fbList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                Log.d("############", "Items " + position);

            }
        });
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navMenu.showMenu(null);
            }
        });
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
        loadingView.setVisibility(View.GONE);
    }

    public void getTimeInterval(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        today = (String)dateFormat.format(cal.getTime());
//        today += ":00:00";
        Log.v("24h", "today " + today);

        cal.add(Calendar.DATE,1);  // number of days to add
        tommorrow = (String)(dateFormat.format(cal.getTime()));
//        tommorrow += ":00:00";
        Log.v("24h", "today " + tommorrow);
    }

    public void showAlert(){
        Utility.alertView("No events upcoming now",this, false);
    }

    public void openChoice(final Boolean friends, final Boolean cities){
        String title = "";
        if (friends){
            title = "Select a friend to follow";
            dialogData = friendsList;
            mSettings.edit().putBoolean("citiesClicked", false).commit();
        }else if (cities){
            title = "Select a city for events";
            dialogData = putInListCities(citiesList);
            mSettings.edit().putBoolean("citiesClicked", true).commit();
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                EventsActivity.this);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(title);
        Arrays.sort(dialogData);
        dialogData = sortAllInTop(dialogData);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                EventsActivity.this,
                android.R.layout.select_dialog_multichoice, dialogData);
        newArray = new ArrayList<FBEvent>();
        builderSingle.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        boolean[] checks = makeChecks(dialogData, cities, friends);

        builderSingle.setMultiChoiceItems(dialogData, checks, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialogInterface, int item, boolean isChecked) {
                String strName = arrayAdapter.getItem(item);
                if (item == 0) {
                    Log.v("24h", "allarray count " + allArray.size());
                    mSettings.edit().putString("cities", "").commit();
//                    mSettings.edit().putString("str", "").commit();
                    if(cities)
                        citiesStaticFilter.clear();
                    if(friends)
                        tempTitles.clear();
                    initiateList(allArray, citiesList, friendsList);
                    dialogInterface.dismiss();
                } else if(!isChecked){
                    tempTitles.remove(strName);
                    if(cities && citiesStaticFilter.containsKey(strName))
                        citiesStaticFilter.remove(strName);
                    if(citiesStaticFilter.size() == 0)
                        mSettings.edit().putString("cities", "").commit();
                } else if (isChecked){
                    tempTitles.put(strName,true);
                    if(cities)
                        citiesStaticFilter.put(strName, true);
                }
            }
        });
        builderSingle.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int item = 0; item<dialogData.length; item++){
                    String strName = arrayAdapter.getItem(item);
                    for (FBEvent event : allArray) {
                        if (cities && event.city.equals(strName) && !event.city.equals("no city") && tempTitles.containsKey(strName) && tempTitles.get(strName)) {
                            newArray.add(event);
                            mSettings.edit().putBoolean("citiesClickled", true).commit();
                            Log.v("24h", "citiy " + strName);

                        } else if (friends) {
                            mSettings.edit().putBoolean("citiesClickled", false).commit();
                            for (FBFriend friend : event.friends) {
                                if (friend.name.equals(strName) && tempTitles.containsKey(strName) && tempTitles.get(strName)) {
                                    newArray.add(event);
                                    Log.v("24h", "friend name " + strName);
                                    break;
                                }
                            }
                        }
                    }
                }
//                String str = "";
//                for(FBEvent fbSorted : newArray){
//                    if(!str.contains(fbSorted.city)){
//                        str += fbSorted.city+",";
//                    }
//                }
                String str = "";
                String[] arr = citiesStaticFilter.keySet().toArray(new String[citiesStaticFilter.size()]);
                for(String string : arr)
                    str +=  ", "+string;
                if(str.length() > 5)
                    str = str.substring(2,str.length());

                if(newArray.size() == 0 ){
                    initiateList(allArray, citiesList, friendsList);
                }else{
                    mSettings.edit().putString("cities", str).commit();
                    initiateList(newArray, citiesList, friendsList);
                }

                dialog.dismiss();
            }
        });
        builderSingle.show();

    }

    public String getFilterIfExists(){
        String str = mSettings.getString("cities", "");
//        if (str.length() == 0) return "";
//        String[] strArray = str.split(", ");
//        str = "";
//        for(String s : strArray)
//            str += s + ", ";

        return str;
    }

    public void showWithFilter(){
        String str = getFilterIfExists();
        newArray = new ArrayList<FBEvent>();
        if(str.length()>0){
            Log.v("24h","str " + str);
            for (FBEvent sorted : allArray) {
                Log.v("24h", "bef sort "+sorted.city);
                if(str.contains(sorted.city) && !sorted.city.equals("no city")){
                    newArray.add(sorted);
                    Log.v("24h", "sort "+sorted.city);
                }
                mSettings.edit().putBoolean("citiesClickled", true).commit();
            }
            FBAdapter fbAdapter = new FBAdapter(this, newArray);
            fbList.setAdapter(fbAdapter);
            fbAdapter.notifyDataSetChanged();
        }
    }

    public String[] putInListCities(String[] list){
        String[] strArray = mSettings.getString("cities", "").split(", ");
        ArrayList<String> array = new ArrayList<String>(Arrays.asList(list));
        StringBuilder builder = new StringBuilder();
        for(String s : list) {
            builder.append(s);
        }
        String stringified = builder.toString();
        Log.v("24h","stringified " + stringified);
        for(String element : strArray){
            Log.v("24h","element " + element);
            if(!stringified.contains(element))
                array.add(element);
        }
        String[] s = array.toArray(new String[array.size()]);
        return s;
    }

//    public boolean[] makeChecks(String[] data){
//        boolean[] checks = new boolean[data.length];
//        String str = getFilterIfExists();
//        if(str.length() == 0){
//           str =  mSettings.getString("str","");
//        }
//        Log.v("24h", "strrrrr "+str);
//        String newStr = "";
//        if(str.length()>0){
//            for (int i=0; i<data.length; i++) {
//                String dataString = data[i];
//                Log.v("24h", "item "+dataString);
//                if(str.contains(dataString) && !dataString.equals("no city")){
//                    checks[i] = true;
//                    Log.v("24h","check true " + dataString + "strr "+str);
//                    newStr += dataString + ",";
//                }else{
//                    checks[i] = false;
//                    Log.v("24h","check false " + dataString + "strr "+str);
//                }
//            }
//            if(newStr.length() > 0)
//                str = newStr;
//            mSettings.edit().putString("str", str).commit();
//        }
//
//        return checks;
//    }

    public boolean[] makeChecks(String[] data, boolean cities, boolean friends){
        boolean[] checks = new boolean[data.length];
        if(cities){
            for(int i=0; i<data.length; i++){
                if(citiesStaticFilter.containsKey(data[i])){
                    checks[i] = true;
                }else{
                    checks[i] = false;
                }
            }
        }else if (friends){
            for(int i=0; i<data.length; i++){
                Log.v("24h","friends "+data[i]);
                if(tempTitles.containsKey(data[i])){
                    checks[i] = true;
                }else{
                    checks[i] = false;
                }
            }
        }
        return checks;
    }
    
    public String[] sortAllInTop(String[] data){
        String[] newString = new String[data.length+1];
        newString[0] = "All";
        for(int i=0; i<data.length; i++){
            newString[i+1] = data[i];
        }
        for(String str : newString)
            Log.v("24h","SORTED "+ str);
        return newString;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            findViewById(R.id.mainMenu).setVisibility(View.GONE);
        }else{
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Reachability reachability = new Reachability(this);
        if(reachability.isOnline()){

        }else{
            Utility.alertView("Please check your internet connection",this,true);
        }
    }
}
