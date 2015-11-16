package com.ljmob.lovereadingphone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.ljmob.lovereadingphone.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by london on 15/11/13.
 * 倒计时
 */
public class CountDownFragment extends Fragment {
    View rootView;
    @Bind(R.id.view_count_tvTime1)
    TextView viewCountTvTime1;
    @Bind(R.id.view_count_tvTime2)
    TextView viewCountTvTime2;
    @Bind(R.id.view_count_tvTime3)
    TextView viewCountTvTime3;
    @Bind(R.id.activity_count_down_viewCleaner)
    View activityCountDownViewCleaner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_count_down, container, false);
        }
        ButterKnife.bind(this, rootView);
        startAnim();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void startAnim() {
        //文字动画
        viewCountTvTime3.post(new Runnable() {
            @Override
            public void run() {
                viewCountTvTime3.animate().scaleY(1.4f).scaleX(1.4f)
                        .setDuration(500)
                        .setInterpolator(new AccelerateInterpolator())
                        .start();
            }
        });
        viewCountTvTime3.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewCountTvTime3.animate().scaleY(1.0f).scaleX(1.0f)
                        .setDuration(500)
                        .alpha(0)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
        }, 500);

        viewCountTvTime2.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewCountTvTime2.animate().scaleY(1.6f).scaleX(1.6f)
                        .setDuration(500)
                        .alpha(1.0f)
                        .setInterpolator(new AccelerateInterpolator())
                        .start();
            }
        }, 1000);
        viewCountTvTime2.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewCountTvTime2.animate().scaleY(1.0f).scaleX(1.0f)
                        .setDuration(500)
                        .alpha(0)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
        }, 1500);

        viewCountTvTime1.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewCountTvTime1.animate().scaleY(1.8f).scaleX(1.8f)
                        .setDuration(300)
                        .setInterpolator(new AccelerateInterpolator())
                        .start();
                viewCountTvTime1.animate()
                        .setDuration(50)
                        .alpha(1.0f)
                        .setInterpolator(new AccelerateInterpolator())
                        .start();
            }
        }, 2000);

        //圈动画
        activityCountDownViewCleaner.post(new Runnable() {
            @Override
            public void run() {
                activityCountDownViewCleaner.animate().scaleY(1.5f).scaleX(1.5f)
                        .setDuration(500)
                        .setInterpolator(new AccelerateInterpolator())
                        .start();
            }
        });
        activityCountDownViewCleaner.postDelayed(new Runnable() {
            @Override
            public void run() {
                activityCountDownViewCleaner.animate().scaleY(1.0f).scaleX(1.0f)
                        .setDuration(500)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
        }, 500);

        activityCountDownViewCleaner.postDelayed(new Runnable() {
            @Override
            public void run() {
                activityCountDownViewCleaner.animate().scaleY(1.8f).scaleX(1.8f)
                        .setDuration(500)
                        .setInterpolator(new AccelerateInterpolator())
                        .start();
            }
        }, 1000);
        activityCountDownViewCleaner.postDelayed(new Runnable() {
            @Override
            public void run() {
                activityCountDownViewCleaner.animate().scaleY(1.0f).scaleX(1.0f)
                        .setDuration(500)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
        }, 1500);
        activityCountDownViewCleaner.postDelayed(new Runnable() {
            @Override
            public void run() {
                activityCountDownViewCleaner.animate().scaleX(10).scaleY(10)
                        .setDuration(300)
                        .setInterpolator(new AccelerateInterpolator())
                        .start();
            }
        }, 2000);

        rootView.postDelayed(new Runnable() {
            @Override
            public void run() {
                rootView.animate().alpha(0)
                        .setDuration(500)
                        .start();
            }
        }, 2500);
    }
}
