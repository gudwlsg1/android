package com.example.wjsur0329.seeker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by starocean on 2017-12-17.
 */

public class SplashActivity extends AppCompatActivity {

    public static Activity loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loding);

        loading=SplashActivity.this;


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

    }



}
