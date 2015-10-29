package com.ljmob.lovereadingphone.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.ReadingActivity;
import com.londonx.lutil.util.ToastUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by london on 15/10/29.
 * 录音机
 */
public class RecorderFragment extends Fragment {
    View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_recorder, container, false);
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.view_recorder_imgRetry)
    protected void retry() {
        ToastUtil.show("retry");
    }

    @OnClick(R.id.view_recorder_imgRecord)
    protected void recordOrPause() {
        ToastUtil.show("recordOrPause");
    }

    @OnClick(R.id.view_recorder_imgDone)
    protected void finishRecording() {
        MaterialDialog feelingDialog = new MaterialDialog.Builder(getActivity())
                .theme(Theme.LIGHT)
                .title(R.string.dialog_feeling)
                .positiveText(R.string.dialog_feeling_confirm)
                .positiveColorRes(R.color.colorPrimary)
                .customView(R.layout.view_dialog_feeling, true)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog,
                                        @NonNull DialogAction dialogAction) {
                        materialDialog.dismiss();
                        EditText etFeeling = (EditText) materialDialog.getCustomView();
                        String feeling = "";
                        if (etFeeling != null) {
                            feeling = etFeeling.getText().toString();
                        }
                        ((ReadingActivity) getActivity())
                                .setCurrentStatus(ReadingActivity.Status.notRatedResult);
                        ToastUtil.show(feeling);
                    }
                })
                .build();
        feelingDialog.show();
    }
}
