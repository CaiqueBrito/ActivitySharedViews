package com.example.activitysharedviews;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;

/*
* Copyright (c) <2016> <Caique Teixeira Brito> https://github.com/CaiqueBrito
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
public class ActivityTransition {

    /**
     * The method will capture the list of views and write it into a intent to be recovered on the
     * destiny activity.
     * @param animationDatas The list containing all view which will be animated to other activity
     * @param activity The origin activity
     * @param intent The intent which will specify the activity destiny to animated
     */
    public static void startActivity(ArrayList<AnimationData> animationDatas, Activity activity, Intent intent) {

        intent.putExtra("animationDatas", animationDatas);

        for(AnimationData animationData : animationDatas) {
            int viewId = animationData.getViewId();
            View view = activity.findViewById(viewId);

            int[] screenLocation = new int[2];
            view.getLocationOnScreen(screenLocation);

            intent
                    .putExtra(viewId + ".left", screenLocation[0])
                    .putExtra(viewId + ".top", screenLocation[1])
                    .putExtra(viewId + ".width", view.getWidth())
                    .putExtra(viewId + ".height", view.getHeight());
        }

        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    /**
     * The method will capture the views and write it into a intent to be recovered on the
     * destiny activity.
     * @param animationData A view which will be animated on other activity
     * @param activity The origin activity
     * @param intent The intent which will specify the activity destiny to animated
     */
    public static void startActivity(AnimationData animationData, Activity activity, Intent intent) {

        ArrayList<AnimationData> animationDatas = new ArrayList<>();
        animationDatas.add(animationData);

        startActivity(animationDatas, activity, intent);
    }

    /**
     * This method will recover all view writed on the Bundle passed by the origin activity
     * @param activity The activity which will recover the view on Bundle and start the animations of each view.
     * @param unbundleViewCallback The callback which will notify the activity when the most animation end.
     */
    public static void runEnterAnimation(final Activity activity, @Nullable final UnbundleViewCallback unbundleViewCallback) {

        Bundle bundle = activity.getIntent().getExtras();
        ArrayList<AnimationData> animationDatas = (ArrayList<AnimationData>) bundle.getSerializable("animationDatas");

        int enterDelay = 0;

        if (animationDatas != null)
            if (unbundleViewCallback != null)
                for (AnimationData animationData : animationDatas) {
                    if (enterDelay < animationData.getDuration())
                        enterDelay = animationData.getDuration();
                }

            for(final AnimationData animationData : animationDatas) {
                int viewId = animationData.getViewId();
                final View view = activity.findViewById(viewId);
                final int bundledWidth = bundle.getInt(viewId + ".width");
                final int bundledHeight = bundle.getInt(viewId + ".height");
                final int bundledTop = bundle.getInt(viewId + ".top");
                final int bundledLeft = bundle.getInt(viewId + ".left");

                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);

                        int[] screenLocation = new int[2];
                        view.getLocationOnScreen(screenLocation);
                        int mLeftDelta = bundledLeft - screenLocation[0];
                        int mTopDelta = bundledTop - screenLocation[1];

                        float mWidthScale = (float) bundledWidth / view.getWidth();
                        float mHeigthScale = (float) bundledHeight / view.getHeight();

                        view.setPivotX(0);
                        view.setPivotY(0);
                        view.setScaleX(mWidthScale);
                        view.setScaleY(mHeigthScale);
                        view.setTranslationX(mLeftDelta);
                        view.setTranslationY(mTopDelta);

                        view.animate()
                                .setDuration(animationData.getDuration())
                                .scaleX(1).scaleY(1)
                                .translationX(0).translationY(0)
                                .setInterpolator(makeInterpolator(animationData.getInterpolator()));

                        return true;
                    }
                });
            }

        if (unbundleViewCallback != null)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    unbundleViewCallback.viewUnbundled();
                }
            }, enterDelay);
    }

    /**
     * This method will revert the animation of each view to these view go back to its original position
     * @param activity The activity which will recover the view on Bundle and start the reverted animations of each view.
     * @param unbundleViewCallback The callback which will notify the activity when the most animation end.
     */
    public static void runExitAnimation(Activity activity, @Nullable final UnbundleViewCallback unbundleViewCallback) {

        Bundle bundle = activity.getIntent().getExtras();
        ArrayList<AnimationData> animationDatas = (ArrayList<AnimationData>) bundle.getSerializable("animationDatas");

        int exitDelay = 0;

        if (animationDatas != null) {
            if (unbundleViewCallback != null)
                for (AnimationData animationData : animationDatas) {
                    if (exitDelay < animationData.getDuration())
                        exitDelay = animationData.getDuration();
                }

            for (AnimationData animationData : animationDatas) {
                int viewId = animationData.getViewId();
                View view = activity.findViewById(viewId);
                int bundledWidth = bundle.getInt(viewId + ".width");
                int bundledHeight = bundle.getInt(viewId + ".height");
                int bundledTop = bundle.getInt(viewId + ".top");
                int bundldedLeft = bundle.getInt(viewId + ".left");

                int[] screenLocation = new int[2];
                view.getLocationOnScreen(screenLocation);
                int mLeftDelta = bundldedLeft - screenLocation[0];
                int mTopDelta = bundledTop - screenLocation[1];

                float mWidthScale = (float) bundledWidth / view.getWidth();
                float mHeigthScale = (float) bundledHeight / view.getHeight();

                view.animate()
                        .setDuration(animationData.getDuration())
                        .scaleX(mWidthScale).scaleY(mHeigthScale)
                        .translationX(mLeftDelta).translationY(mTopDelta)
                        .setInterpolator(makeInterpolator(animationData.getInterpolator()));
            }
        }

        if (unbundleViewCallback != null)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    unbundleViewCallback.viewUnbundled();
                }
            }, exitDelay);
    }

    /**
     * This method will create the interpolator which will be used on each view setted to animate.
     * @param interpolatorIdentifier The constant to identify which Interpolator will be Instantiated
     * @return The TimeInterpolator object to animate the view
     */
    private static TimeInterpolator makeInterpolator(int interpolatorIdentifier) {
        switch (interpolatorIdentifier) {
            case InterpolatorIdentifier.ACCELERATE_DECELERATE:
                return new AccelerateDecelerateInterpolator();
            case InterpolatorIdentifier.ACCELERATE:
                return new AccelerateInterpolator();
            case InterpolatorIdentifier.ANTICIPATE:
                return new AnticipateInterpolator();
            case InterpolatorIdentifier.ANTICIPATE_OVERSHOOT:
                return new AnticipateOvershootInterpolator();
            case InterpolatorIdentifier.BOUNCE:
                return new BounceInterpolator();
            case InterpolatorIdentifier.DECELERATE:
                return new DecelerateInterpolator();
            case InterpolatorIdentifier.FAST_OUT_LINEAR_IN:
                return new FastOutLinearInInterpolator();
            case InterpolatorIdentifier.FAST_OUT_SLOW_IN:
                return new FastOutSlowInInterpolator();
            case InterpolatorIdentifier.LINEAR:
                return new LinearInterpolator();
            case InterpolatorIdentifier.LINEAR_OUT_SLOW_IN:
                return new LinearOutSlowInInterpolator();
            case InterpolatorIdentifier.OVERSHOOT:
                return new OvershootInterpolator();
            default:
                return null;
        }
    }

    /**
     * This callback have two different behavior, if you use on EnterAnimation, the callback
     * will notify when the animation starts, then you can apply simultaneous animations by yourself.
     * if you use on ExitAnimation, the callback will return when the higher duration of animation ends.
     */
    public interface UnbundleViewCallback {
        void viewUnbundled();
    }
}
