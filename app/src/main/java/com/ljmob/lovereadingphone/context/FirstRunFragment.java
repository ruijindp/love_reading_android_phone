package com.ljmob.lovereadingphone.context;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.lovereadingphone.MainActivity;
import com.ljmob.lovereadingphone.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/11/18.
 * 首次运行引导
 */
public class FirstRunFragment extends Fragment {
    View rootView;
    @Bind(R.id.view_first_run_img)
    ImageView viewFirstRunImg;
    @Bind(R.id.view_first_run_tv)
    TextView viewFirstRunTv;
    @Bind(R.id.view_first_run_btn)
    TextView viewFirstRunBtn;

    @DrawableRes
    private int imgResId;
    @StringRes
    private int textResId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_first_run, container, false);
        }
        ButterKnife.bind(this, rootView);
        if (imgResId != 0) {
            viewFirstRunImg.setImageResource(imgResId);
        }
        if (textResId != 0) {
            viewFirstRunTv.setText(textResId);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.view_first_run_btn)
    protected void gotoMain() {
        startActivity(new Intent(getContext(), MainActivity.class));
        getActivity().finish();
    }

    public void setImageRes(@DrawableRes int resId) {
        this.imgResId = resId;
        if (viewFirstRunImg != null) {
            viewFirstRunImg.setImageResource(resId);
        }
    }

    public void setTextRes(@StringRes int resId) {
        this.textResId = resId;
        if (viewFirstRunTv != null) {
            viewFirstRunTv.setText(resId);
        }
    }

    public void showButton() {
        viewFirstRunBtn.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewFirstRunBtn.setVisibility(View.VISIBLE);
                viewFirstRunBtn.animate().alpha(1.0f).setDuration(2000).start();
            }
        }, 800);
    }
}
