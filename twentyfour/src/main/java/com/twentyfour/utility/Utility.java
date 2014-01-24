package com.twentyfour.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by AGalkin on 12/5/13.
 */


public class Utility {
    public static AlertDialog alertDialog;
    public static final String TAG = "24H";

    public static Drawable drawableFromUrl(String url, Activity activity) throws IOException {
        if(url.length()>0){
            Bitmap x;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();
            x = BitmapFactory.decodeStream(input);
            return new BitmapDrawable(activity.getApplicationContext().getResources(), x);
        }else{
            return null;
        }
    }

    public static String dateConvert(long unixSeconds){
        CharSequence dateOut= DateUtils.getRelativeTimeSpanString(unixSeconds * 1000, System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL);
        return dateOut.toString();
    }

    public static void alertView(String message, final Activity activity){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        // set title
        alertDialogBuilder.setTitle("Warning");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        alertDialog.cancel();
                    }
                });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
