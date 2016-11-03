package com.example.caiquetb.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.activitysharedviews.ActivityTransition;
import com.example.activitysharedviews.AlphaData;
import com.example.activitysharedviews.AnimationData;
import com.example.activitysharedviews.InterpolatorIdentifier;

import java.util.ArrayList;

public class OneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        getSupportActionBar().setTitle("Activity One");

        final Intent intent = new Intent(this, TwoActivity.class);

        final ArrayList<AnimationData> animationDatas = new ArrayList<>();

        animationDatas.add(
                new AnimationData(
                        R.id.imageTransition,
                        1000,
                        InterpolatorIdentifier.ACCELERATE_DECELERATE,
                        null));
        animationDatas.add(
                new AnimationData(
                        R.id.backgroundTornado,
                        1300,
                        InterpolatorIdentifier.DECELERATE,
                        new AlphaData(1, 0.5f)));
        animationDatas.add(
                new AnimationData(
                        R.id.backgroundOvershoot,
                        1000,
                        InterpolatorIdentifier.OVERSHOOT,
                        null));

        findViewById(R.id.imageTransition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityTransition.startActivity(animationDatas, OneActivity.this, intent);
            }
        });
    }
}
