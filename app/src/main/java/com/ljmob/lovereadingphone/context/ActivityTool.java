package com.ljmob.lovereadingphone.context;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by london on 15/10/21.
 * finish activity with compat
 */
public class ActivityTool {
    public static void finish(Activity activity) {
        if (Build.VERSION.SDK_INT > 21) {
            activity.finishAfterTransition();
        } else {
            activity.finish();
        }
    }

    public static void start(Activity from, Intent intent, View view) {
        if (Build.VERSION.SDK_INT > 21) {
            ((ViewGroup) view.getParent()).setTransitionGroup(false);
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(from, view, "view");
            if (options != null) {
                from.startActivity(intent, options.toBundle());
            }
        } else {
            from.startActivity(intent);
        }
    }
}
