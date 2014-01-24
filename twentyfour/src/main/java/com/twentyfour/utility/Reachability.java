package com.twentyfour.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.twentyfour.R;

/**
 * Created by AGalkin on 12/29/13.
 */
public class Reachability {
    Activity activity;
    public Reachability(Activity activity){
        this.activity = activity;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            try{
                if(activity.getWindow().getDecorView().findViewWithTag("top_animation").getVisibility()==1){
                    hideMessage();
                }
            }catch(NullPointerException e){

            }
            return true;
        }else{
            showMessage();
            return false;
        }
    }
    public void showMessage(){
        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.sliding_top);
        anim.setInterpolator((new AccelerateDecelerateInterpolator()));
        anim.setFillAfter(true);
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.sliding_top_text, null);

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        int height = dm.heightPixels;

        activity.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height/10));
        Log.v("RPS", "height " + height);
        LinearLayout layout = (LinearLayout)activity.getWindow().getDecorView().findViewWithTag("top_animation");
        layout.setVisibility(View.VISIBLE);
        layout.setAnimation(anim);
    }
    public void hideMessage(){
        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.sliding_top_out);
        anim.setInterpolator((new AccelerateDecelerateInterpolator()));
        anim.setFillAfter(true);

        LinearLayout layout = (LinearLayout)activity.getWindow().getDecorView().findViewWithTag("top_animation");
        layout.setAnimation(anim);
    }
}
