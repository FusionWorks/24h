package com.twentyfour.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.twentyfour.R;
import com.twentyfour.main.EventsActivity;
import com.twentyfour.utility.Utility;

public class NavMenu {
	public EventsActivity screen;
	private int position_id;
	//public static
    String[] menuContent;
    SharedPreferences prefs;
    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "settings";

	public NavMenu(EventsActivity in_screen, int in_position_id){
		Log.v("NavMenu", "NAVMENU CREATED");
        screen = in_screen;
		position_id = in_position_id;
        prefs = PreferenceManager.getDefaultSharedPreferences(screen);
		initiateMenu();
	}
	
	public View getView(int id){
		return screen.findViewById(id);
	}
	public void initiateMenu(){
        mSettings = screen.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String eventsString = "";
        if(mSettings.getString("cities", "").length() > 0 && mSettings.getBoolean("citiesClickled", false)){
            String str = mSettings.getString("cities", "");
            eventsString = "Events in " + str;
        }
        else{
            eventsString = "Events in all cities";
            String str = mSettings.getString("cities", "");
            if(str.length() > 0){
                eventsString = "Events in " + str;
            }
            Log.v("24h", "str " + str );
        }
            menuContent = new String[]{
                    eventsString,
                    "Follow a friend\n(Will not apply city filter)",
                    "All events (Invited only)",
                    "All events joined"
            };

        ListView myList = (ListView) screen.findViewById(R.id.menu);
        MenuAdapter newsEntryAdapter = new MenuAdapter(screen, menuContent, R.layout.list_item_menu, this);
        myList.setAdapter(newsEntryAdapter) ;

		myList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//TextView agencyText = (TextView) screen.findViewById(R.id.agencyText);
				//agencyText.setText(LocationListActivity.currentSelectedLocation);
                    Log.v("CL",""+parent.getTag());
					Log.d("Double map", "itemClick: position = " + position + ", id = " + id);
					selectedItem(position);
			}
		});

    }
	public void selectedItem(int position){
		switch(position){
//			case 0:
//                Log.v("24h","pos "+position);
////                screen.loadingView(1);
//                screen.selection = "all";
//                screen.getEvents();
//                mSlideHolder.close();
//				break;
            case 0:
                Log.v("24h","pos "+position);
                if(screen.citiesList.length > 0){
                    screen.openChoice(false, true);
                }else{
                    Utility.alertView("No cities",screen, false);
                }
                screen.findViewById(R.id.mainMenu).setVisibility(View.GONE);
                break;
			case 1:
                Log.v("24h","pos "+position);
                if(screen.friendsList.length > 0){
                    screen.openChoice(true, false);
                }else{
                    Utility.alertView("No friends",screen, false);
                }
                screen.findViewById(R.id.mainMenu).setVisibility(View.GONE);
                break;
            case 2:
                mSettings.edit().putBoolean("attending",false).commit();
                mSettings.edit().putBoolean("invite",true).commit();
                screen.getAll();
                screen.findViewById(R.id.mainMenu).setVisibility(View.GONE);
                break;
            case 3:
                mSettings.edit().putBoolean("attending",true).commit();
                mSettings.edit().putBoolean("invite",false).commit();
                screen.getAll();
                screen.findViewById(R.id.mainMenu).setVisibility(View.GONE);
                break;
		}
	}

	public void showMenu(View view){
        initiateMenu();
		Log.v("DoubleMap", "Show menu");
        screen.findViewById(R.id.mainMenu).setVisibility(View.VISIBLE);
		/*
		if (mSlideHolder.isOpened()) {
			Log.d("showMenu", "isOpen");
			mSlideHolder.close();
		}else{
			Log.d("showMenu", "isClosed");
			mSlideHolder.open();
		}
		*/
	}

}
