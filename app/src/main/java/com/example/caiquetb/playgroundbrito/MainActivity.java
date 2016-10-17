package com.example.caiquetb.playgroundbrito;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.activitysharedviews.ActivityTransition;

public class MainActivity extends AppCompatActivity {

    boolean backPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityTransition.runEnterAnimation(this, new ActivityTransition.UnbundleViewCallback() {
            @Override
            public void viewUnbundled() {
                //do things after the most duration of animation ends.
            }
        });


    }

    @Override
    public void onBackPressed() {
        if(!backPressed) {
            backPressed = true;
            ActivityTransition.runExitAnimation(this, new ActivityTransition.UnbundleViewCallback() {
                @Override
                public void viewUnbundled() {
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        }
    }
}
