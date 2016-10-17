package com.example.caiquetb.playgroundbrito;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.activitysharedviews.ActivityTransition;

public class MainActivity extends AppCompatActivity {

    boolean backPressed = false;
    private TextView textViewAnimated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewAnimated = (TextView) findViewById(R.id.textview_animated);

        textViewAnimated.setAlpha(0);

        ActivityTransition.runEnterAnimation(this, new ActivityTransition.UnbundleViewCallback() {
            @Override
            public void viewUnbundled() {
                textViewAnimated.setTranslationX(textViewAnimated.getWidth());
                textViewAnimated.animate().
                        setDuration(500).
                        alpha(1).
                        translationX(0).
                        setInterpolator(new DecelerateInterpolator());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(!backPressed) {
            backPressed = true;
            textViewAnimated.setAlpha(0);
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
