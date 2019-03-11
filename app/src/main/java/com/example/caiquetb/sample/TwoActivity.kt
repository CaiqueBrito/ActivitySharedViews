package com.example.caiquetb.sample

import android.animation.Animator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.animation.DecelerateInterpolator
import com.example.activitysharedviews.ActivityTransition
import kotlinx.android.synthetic.main.activity_two.*

class TwoActivity : AppCompatActivity() {

    internal var backPressed = false

    private val preventBackPressedListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {

        }

        override fun onAnimationEnd(animation: Animator) {
            backPressed = false
        }

        override fun onAnimationCancel(animation: Animator) {

        }

        override fun onAnimationRepeat(animation: Animator) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)

        supportActionBar?.title = "Activity Two"

        textview_animated.alpha = 0f

        backPressed = true

        ActivityTransition.runEnterAnimation(this, object : ActivityTransition.UnbundleViewCallback {
            override fun viewUnbundled() {

                textview_animated.translationX = textview_animated.width.toFloat()
                textview_animated.animate().setDuration(500).alpha(1f).translationX(0f).setInterpolator(DecelerateInterpolator()).setListener(preventBackPressedListener)
            }
        })
    }

    override fun onBackPressed() {
        if (!backPressed) {
            backPressed = true
            textview_animated.alpha = 0f
            ActivityTransition.runExitAnimation(this, object : ActivityTransition.UnbundleViewCallback {
                override fun viewUnbundled() {
                    finish()
                    overridePendingTransition(0, 0)
                }
            })
        }
    }
}
