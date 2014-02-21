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
	public SlideHolder mSlideHolder;
	public EventsActivity screen;
	private int position_id;
	//public static
    String[] menuContent;
    String[] buttonTitles;
    SharedPreferences prefs;
    SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "settings";

	public NavMenu(EventsActivity in_screen, SlideHolder in_mSlideHolder, int in_position_id){
		Log.v("NavMenu", "NAVMENU CREATED");
		mSlideHolder = in_mSlideHolder;
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
        if(mSettings.getString("cities", "").length() > 0){
            eventsString = "Events in " + mSettings.getString("cities", "");
        }
        else{
            eventsString = "Events in all cities";
        }
            menuContent = new String[]{
                    eventsString,
                    "Follow a friend",
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
                if(screen.fbSorted.size()>0){
                    screen.openChoice(false, true);
                }else{
                    Utility.alertView("No events available",screen);
                }
                mSlideHolder.close();
                break;
			case 1:
                Log.v("24h","pos "+position);
                if(screen.fbSorted.size()>0){
                    screen.openChoice(true, false);
                }else{
                    Utility.alertView("No events available",screen);
                }
                mSlideHolder.close();
                break;
            case 2:
                mSettings.getBoolean("attending",false);
                mSettings.getBoolean("invite",true);
                screen.getEvents();
                mSlideHolder.close();
                break;
            case 3:
                mSettings.getBoolean("attending",true);
                mSettings.getBoolean("invite",false);
                screen.getEvents();
                mSlideHolder.close();
                break;
		}
	}

	public void showMenu(View view){
        initiateMenu();
		Log.v("DoubleMap", "Show menu");
		mSlideHolder.toggle();
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
