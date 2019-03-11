package com.example.caiquetb.sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.activitysharedviews.ActivityTransition
import com.example.activitysharedviews.AlphaData
import com.example.activitysharedviews.AnimationData
import com.example.activitysharedviews.InterpolatorIdentifier
import java.util.*

class OneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one)

        supportActionBar?.title = "Activity One"

        val intent = Intent(this, TwoActivity::class.java)

        val animationDatas = ArrayList<AnimationData>()

        animationDatas.add(
                AnimationData(
                        R.id.imageTransition,
                        1000,
                        InterpolatorIdentifier.ACCELERATE_DECELERATE,
                        null))
        animationDatas.add(
                AnimationData(
                        R.id.backgroundTornado,
                        1300,
                        InterpolatorIdentifier.DECELERATE,
                        AlphaData(1f, 0.5f)))
        animationDatas.add(
                AnimationData(
                        R.id.backgroundOvershoot,
                        1000,
                        InterpolatorIdentifier.OVERSHOOT, null))

        findViewById<View>(R.id.imageTransition).setOnClickListener {
            ActivityTransition.startActivity(animationDatas, this@OneActivity, intent) }
    }
}
