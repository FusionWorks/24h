package com.twentyfour.main;

import android.os.Bundle;

import com.twentyfour.utility.MainFragment;

public class MainFragmentActivity extends android.support.v4.app.FragmentActivity {

    private MainFragment mainFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }

}
