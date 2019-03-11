package com.example.activitysharedviews

import android.animation.TimeInterpolator
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.*
import java.util.*

/*
* Copyright (c) <2018> <Caique Teixeira Brito> https://github.com/CaiqueBrito
* Permission is hereby granted, free of charge, to any person obtaining a copy of this
* software and associated documentation files (the "Software"),
* to deal in the Software without restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense,
* and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
* subject to the following conditions: The above copyright notice and this permission notice
* shall be included in all copies or substantial portions of the Software.
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
* */
class ActivityTransition {

    /**
     * This callback have two different behavior, if you use on EnterAnimation, the callback
     * will notify when the animation starts, then you can apply simultaneous animations by yourself.
     * if you use on ExitAnimation, the callback will return when the higher duration of animation ends.
     */
    interface UnbundleViewCallback {
        fun viewUnbundled()
    }

    companion object {

        /**
         * The method will capture the list of views and write it into a intent to be recovered on the
         * destiny activity.
         * @param animationDatas The list containing all view which will be animated to other activity
         * @param activity The origin activity
         * @param intent The intent which will specify the activity destiny to animated
         */
        fun startActivity(animationDatas: ArrayList<AnimationData>, activity: Activity, intent: Intent) {

            intent.putExtra("animationDatas", animationDatas)

            for (animationData in animationDatas) {
                val viewId = animationData.viewId
                val view = activity.findViewById<View>(viewId)

                val screenLocation = IntArray(2)
                view.getLocationOnScreen(screenLocation)

                intent
                        .putExtra(viewId.toString() + ".left", screenLocation[0])
                        .putExtra(viewId.toString() + ".top", screenLocation[1])
                        .putExtra(viewId.toString() + ".width", view.width)
                        .putExtra(viewId.toString() + ".height", view.height)
            }

            activity.startActivity(intent)
            activity.overridePendingTransition(0, 0)
        }

        /**
         * The method will capture the views and write it into a intent to be recovered on the
         * destiny activity.
         * @param animationData A view which will be animated on other activity
         * @param activity The origin activity
         * @param intent The intent which will specify the activity destiny to animated
         */
        fun startActivity(animationData: AnimationData, activity: Activity, intent: Intent) {

            val animationDatas = ArrayList<AnimationData>()
            animationDatas.add(animationData)

            startActivity(animationDatas, activity, intent)
        }

        /**
         * This method will recover all view writed on the Bundle passed by the origin activity
         * @param activity The activity which will recover the view on Bundle and start the animations of each view.
         * @param unbundleViewCallback The callback which will notify the activity when the most animation end.
         */
        fun runEnterAnimation(activity: Activity, unbundleViewCallback: UnbundleViewCallback?) {

            val bundle = activity.intent.extras
            val animationDatas = bundle!!.getSerializable("animationDatas") as ArrayList<AnimationData>?

            var enterDelay = 0

            animationDatas?.let {
                if (unbundleViewCallback != null)
                    for (animationData in animationDatas) {
                        if (enterDelay < animationData.duration)
                            enterDelay = animationData.duration
                    }

                for (animationData in animationDatas) {
                    val viewId = animationData.viewId
                    val view = activity.findViewById<View>(viewId)
                    val bundledWidth = bundle.getInt(viewId.toString() + ".width")
                    val bundledHeight = bundle.getInt(viewId.toString() + ".height")
                    val bundledTop = bundle.getInt(viewId.toString() + ".top")
                    val bundledLeft = bundle.getInt(viewId.toString() + ".left")

                    view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            view.viewTreeObserver.removeOnPreDrawListener(this)

                            val screenLocation = IntArray(2)
                            view.getLocationOnScreen(screenLocation)
                            val mLeftDelta = bundledLeft - screenLocation[0]
                            val mTopDelta = bundledTop - screenLocation[1]

                            val mWidthScale = bundledWidth.toFloat() / view.width
                            val mHeigthScale = bundledHeight.toFloat() / view.height

                            view.pivotX = 0f
                            view.pivotY = 0f
                            view.scaleX = mWidthScale
                            view.scaleY = mHeigthScale
                            view.translationX = mLeftDelta.toFloat()
                            view.translationY = mTopDelta.toFloat()

                            if (animationData.alphaData != null) {
                                view.animate()
                                        .alphaBy(animationData.alphaData.alphaBy)
                                        .alpha(animationData.alphaData.alphaTo)
                                        .setDuration(animationData.duration.toLong())
                                        .scaleX(1f).scaleY(1f)
                                        .translationX(0f).translationY(0f).interpolator = makeInterpolator(animationData.interpolator)
                            } else
                                view.animate()
                                        .setDuration(animationData.duration.toLong())
                                        .scaleX(1f).scaleY(1f)
                                        .translationX(0f).translationY(0f).interpolator = makeInterpolator(animationData.interpolator)

                            return true
                        }
                    })
                }

                if (unbundleViewCallback != null)
                    Handler().postDelayed({ unbundleViewCallback.viewUnbundled() }, enterDelay.toLong())
            }
        }

        /**
         * This method will revert the animation of each view to these view go back to its original position
         * @param activity The activity which will recover the view on Bundle and start the reverted animations of each view.
         * @param unbundleViewCallback The callback which will notify the activity when the most animation end.
         */
        fun runExitAnimation(activity: Activity, unbundleViewCallback: UnbundleViewCallback?) {

            val bundle = activity.intent.extras
            val animationDatas = bundle!!.getSerializable("animationDatas") as ArrayList<AnimationData>?

            var exitDelay = 0

            animationDatas?.let {
                if (unbundleViewCallback != null)
                    for (animationData in animationDatas) {
                        if (exitDelay < animationData.duration)
                            exitDelay = animationData.duration
                    }

                for (animationData in animationDatas) {
                    val viewId = animationData.viewId
                    val view = activity.findViewById<View>(viewId)
                    val bundledWidth = bundle.getInt(viewId.toString() + ".width")
                    val bundledHeight = bundle.getInt(viewId.toString() + ".height")
                    val bundledTop = bundle.getInt(viewId.toString() + ".top")
                    val bundldedLeft = bundle.getInt(viewId.toString() + ".left")

                    val screenLocation = IntArray(2)
                    view.getLocationOnScreen(screenLocation)
                    val mLeftDelta = bundldedLeft - screenLocation[0]
                    val mTopDelta = bundledTop - screenLocation[1]

                    val mWidthScale = bundledWidth.toFloat() / view.width
                    val mHeigthScale = bundledHeight.toFloat() / view.height

                    if (animationData.alphaData != null) {
                        view.animate()
                                .alphaBy(animationData.alphaData.alphaTo)
                                .alpha(animationData.alphaData.alphaBy)
                                .setDuration(animationData.duration.toLong())
                                .scaleX(mWidthScale).scaleY(mHeigthScale)
                                .translationX(mLeftDelta.toFloat()).translationY(mTopDelta.toFloat()).interpolator = makeInterpolator(animationData.interpolator)
                    } else
                        view.animate()
                                .setDuration(animationData.duration.toLong())
                                .scaleX(mWidthScale).scaleY(mHeigthScale)
                                .translationX(mLeftDelta.toFloat()).translationY(mTopDelta.toFloat()).interpolator = makeInterpolator(animationData.interpolator)
                }
            }

            if (unbundleViewCallback != null)
                Handler().postDelayed({ unbundleViewCallback.viewUnbundled() }, exitDelay.toLong())
        }

        /**
         * This method will create the interpolator which will be used on each view setted to animate.
         * @param interpolatorIdentifier The constant to identify which Interpolator will be Instantiated
         * @return The TimeInterpolator object to animate the view
         */
        private fun makeInterpolator(interpolatorIdentifier: Int): TimeInterpolator? {
            return when (interpolatorIdentifier) {
                InterpolatorIdentifier.ACCELERATE_DECELERATE -> AccelerateDecelerateInterpolator()
                InterpolatorIdentifier.ACCELERATE -> AccelerateInterpolator()
                InterpolatorIdentifier.ANTICIPATE -> AnticipateInterpolator()
                InterpolatorIdentifier.ANTICIPATE_OVERSHOOT -> AnticipateOvershootInterpolator()
                InterpolatorIdentifier.BOUNCE -> BounceInterpolator()
                InterpolatorIdentifier.DECELERATE -> DecelerateInterpolator()
                InterpolatorIdentifier.FAST_OUT_LINEAR_IN -> FastOutLinearInInterpolator()
                InterpolatorIdentifier.FAST_OUT_SLOW_IN -> FastOutSlowInInterpolator()
                InterpolatorIdentifier.LINEAR -> LinearInterpolator()
                InterpolatorIdentifier.LINEAR_OUT_SLOW_IN -> LinearOutSlowInInterpolator()
                InterpolatorIdentifier.OVERSHOOT -> OvershootInterpolator()
                else -> null
            }
        }
    }
}
