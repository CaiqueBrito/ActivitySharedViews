package com.example.caiquetb.sample;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.example.activitysharedviews.ActivityTransition;

public class TwoActivity extends AppCompatActivity {

    boolean backPressed = false;
    private TextView textViewAnimated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        getSupportActionBar().setTitle("Activity Two");

        textViewAnimated = (TextView) findViewById(R.id.textview_animated);
        textViewAnimated.setAlpha(0);

        backPressed = true;

        ActivityTransition.runEnterAnimation(this, new ActivityTransition.UnbundleViewCallback() {
            @Override
            public void viewUnbundled() {

                textViewAnimated.setTranslationX(textViewAnimated.getWidth());
                textViewAnimated.animate().
                        setDuration(500).
                        alpha(1).
                        translationX(0).
                        setInterpolator(new DecelerateInterpolator()).
                        setListener(preventBackPressedListener);
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

    private Animator.AnimatorListener preventBackPressedListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            backPressed = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
}
