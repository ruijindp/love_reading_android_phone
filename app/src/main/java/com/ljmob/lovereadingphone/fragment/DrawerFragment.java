package com.ljmob.lovereadingphone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.gson.Gson;
import com.ljmob.lovereadingphone.ChangePasswordActivity;
import com.ljmob.lovereadingphone.FeedbackActivity;
import com.ljmob.lovereadingphone.ListenedActivity;
import com.ljmob.lovereadingphone.LoginActivity;
import com.ljmob.lovereadingphone.MainActivity;
import com.ljmob.lovereadingphone.MyReadingActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.CheckCount;
import com.ljmob.lovereadingphone.entity.User;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.ljmob.lovereadingphone.util.SimpleImageLoader;
import com.londonx.lutil.Lutil;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.FileUtil;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by london on 15/10/29.
 * 主页Drawer
 */
public class DrawerFragment extends Fragment implements LRequestTool.OnResponseListener {
    private static final int USER_SIGN_OUT = 1;
    private static final int API_RESULTS_COUNT = 2;

    View rootView;

    //drawer
    @Bind(R.id.view_drawer_tvUserName)
    TextView viewDrawerTvUserName;
    @Bind(R.id.view_drawer_imgSex)
    ImageView viewDrawerImgSex;
    @Bind(R.id.view_drawer_tvSchoolClass)
    TextView viewDrawerTvSchoolClass;
    @Bind(R.id.view_drawer_lnMyReading)
    LinearLayout viewDrawerLnMyReading;
    @Bind(R.id.view_drawer_lnStudentReading)
    LinearLayout viewDrawerLnStudentReading;
    @Bind(R.id.view_drawer_lnListened)
    LinearLayout viewDrawerLnListened;
    @Bind(R.id.view_drawer_lnCleanCache)
    LinearLayout viewDrawerLnCleanCache;
    @Bind(R.id.view_drawer_lnFeedBack)
    LinearLayout viewDrawerLnFeedBack;
    @Bind(R.id.view_drawer_lnChangePassword)
    LinearLayout viewDrawerLnChangePassword;
    @Bind(R.id.view_drawer_lnExit)
    LinearLayout viewDrawerLnExit;
    @Bind(R.id.view_drawer_imgDrawerBg)
    ImageView viewDrawerImgDrawerBg;
    @Bind(R.id.view_drawer_frameUser)
    FrameLayout viewDrawerFrameUser;
    @Bind(R.id.view_drawer_tvLogin)
    TextView viewDrawerTvLogin;
    @Bind(R.id.view_drawer_tvCacheTotalSize)
    TextView viewDrawerTvCacheTotalSize;
    @Bind(R.id.view_drawer_imgHead)
    CircleImageView viewDrawerImgHead;
    @Bind(R.id.view_drawer_exLayout)
    ExpandableRelativeLayout viewDrawerExLayout;
    @Bind(R.id.view_drawer_imgMyReadingThumb)
    ImageView viewDrawerImgMyReadingThumb;
    @Bind(R.id.view_drawer_imgStudentReadingThumb)
    ImageView viewDrawerImgStudentReadingThumb;
    @Bind(R.id.view_drawer_tvNotRatedCount)
    TextView viewDrawerTvNotRatedCount;
    @Bind(R.id.view_drawer_tvRatedCount)
    TextView viewDrawerTvRatedCount;

    private MaterialDialog cacheDialog;
    private long cacheSize;
    LRequestTool requestTool;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.view_drawer, container, false);
        }
        ButterKnife.bind(this, rootView);

        viewDrawerExLayout.setDuration(200);
        viewDrawerExLayout.setInterpolator(new DecelerateInterpolator());
        requestTool = new LRequestTool(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initDrawerViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.view_drawer_imgHead)
    protected void changeAvatar() {
        ToastUtil.show("changeAvatar");
    }

    @OnClick(R.id.view_drawer_tvLogin)
    protected void doLogin() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    @OnClick(R.id.view_drawer_lnMyReading)
    protected void myReading() {
        if (viewDrawerExLayout.isExpanded()) {
            viewDrawerImgMyReadingThumb.setImageResource(R.mipmap.icon_top_0);
            viewDrawerImgStudentReadingThumb.setImageResource(R.mipmap.icon_top_0);
        } else {
            viewDrawerImgMyReadingThumb.setImageResource(R.mipmap.icon_bottom);
            viewDrawerImgStudentReadingThumb.setImageResource(R.mipmap.icon_bottom);
        }
        viewDrawerExLayout.toggle();
    }

    @OnClick(R.id.view_drawer_lnStudentReading)
    protected void studentReading() {
        if (viewDrawerExLayout.isExpanded()) {
            viewDrawerImgMyReadingThumb.setImageResource(R.mipmap.icon_top_0);
            viewDrawerImgStudentReadingThumb.setImageResource(R.mipmap.icon_top_0);
        } else {
            viewDrawerImgMyReadingThumb.setImageResource(R.mipmap.icon_bottom);
            viewDrawerImgStudentReadingThumb.setImageResource(R.mipmap.icon_bottom);
        }
        viewDrawerExLayout.toggle();
    }

    @OnClick(R.id.view_drawer_lnNotRated)
    protected void openNotRatedReading() {
        if (MyApplication.currentUser == null) {
            doLogin();
            return;
        }
        Intent intent = new Intent(getActivity(), MyReadingActivity.class);
        intent.putExtra("isRated", false);
        startActivity(intent);
    }

    @OnClick(R.id.view_drawer_lnRated)
    protected void openRatedReading() {
        if (MyApplication.currentUser == null) {
            doLogin();
            return;
        }
        Intent intent = new Intent(getActivity(), MyReadingActivity.class);
        intent.putExtra("isRated", true);
        startActivity(intent);
    }

    @OnClick(R.id.view_drawer_lnListened)
    protected void listened() {
        if (MyApplication.currentUser == null) {
            doLogin();
            return;
        }
        startActivity(new Intent(getActivity(), ListenedActivity.class));
    }

    @OnClick(R.id.view_drawer_lnCleanCache)
    protected void cleanCache() {
        if (cacheDialog == null) {
            cacheDialog = new MaterialDialog.Builder(getActivity())
                    .theme(Theme.LIGHT)
                    .title(R.string.dialog_clean_cache)
                    .content(String.format("%s%s",
                            getString(R.string.total_), formatFileSize(cacheSize)))
                    .negativeText(android.R.string.cancel)
                    .positiveText(android.R.string.ok)
                    .negativeColorRes(R.color.colorPrimary)
                    .positiveColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog
                                , @NonNull DialogAction dialogAction) {
                            materialDialog.dismiss();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean isDeleted = FileUtil.cleanCache();
                                    if (isDeleted) {
                                        viewDrawerLnCleanCache.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.show(R.string.toast_clean_ok);
                                            }
                                        });
                                    } else {
                                        viewDrawerLnCleanCache.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.show(R.string.toast_clean_undone);
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }
                    })
                    .build();
        }
        cacheDialog.show();
    }

    @OnClick(R.id.view_drawer_lnFeedBack)
    protected void feedback() {
        startActivity(new Intent(getActivity(), FeedbackActivity.class));
    }

    @OnClick(R.id.view_drawer_lnChangePassword)
    protected void changePassword() {
        if (MyApplication.currentUser == null) {
            doLogin();
            return;
        }
        startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
    }

    @OnClick(R.id.view_drawer_lnExit)
    protected void exit() {
        requestTool.doPost(NetConstant.ROOT_URL + NetConstant.USER_SIGN_OUT,
                new DefaultParam(), USER_SIGN_OUT);
        MyApplication.currentUser = null;
        Lutil.preferences.edit().clear().apply();
        ((MainActivity) getActivity()).clearAvatar();
        initDrawerViews();
    }

    private void initDrawerViews() {
        viewDrawerTvNotRatedCount.setText(getString(R.string.total__count, 0));
        viewDrawerTvRatedCount.setText(getString(R.string.total__count, 0));
        if (MyApplication.currentUser == null) {//用户未登录
            viewDrawerFrameUser.setVisibility(View.INVISIBLE);
            viewDrawerTvLogin.setVisibility(View.VISIBLE);
        } else {//用户已经登录
            viewDrawerFrameUser.setVisibility(View.VISIBLE);
            viewDrawerTvLogin.setVisibility(View.INVISIBLE);

            if (MyApplication.currentUser.role == User.Role.student) {
                requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_MY_RESULTS_COUNT,
                        new DefaultParam(), API_RESULTS_COUNT);
            } else {
                requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_STUDENT_RESULTS_COUNT,
                        new DefaultParam(), API_RESULTS_COUNT);
            }

            SimpleImageLoader.displayImage(MyApplication.currentUser.avatar.avatar.normal.url,
                    viewDrawerImgHead);
            viewDrawerTvUserName.setText(MyApplication.currentUser.name);
//            viewDrawerTvSchoolClass
            switch (MyApplication.currentUser.sex) {
                case boy:
                    viewDrawerImgSex.setVisibility(View.VISIBLE);
                    viewDrawerImgSex.setImageResource(R.mipmap.icon_boy);
                    break;
                case girl:
                    viewDrawerImgSex.setVisibility(View.VISIBLE);
                    viewDrawerImgSex.setImageResource(R.mipmap.icon_girl);
                    break;
                default:
                    viewDrawerImgSex.setVisibility(View.INVISIBLE);
                    break;
            }
        }
        if (MyApplication.currentUser != null &&
                MyApplication.currentUser.role == User.Role.student) {//学生无“学生朗读”
            viewDrawerLnMyReading.setVisibility(View.VISIBLE);
            viewDrawerLnStudentReading.setVisibility(View.GONE);
        } else {//教师无“我的朗读”
            viewDrawerLnMyReading.setVisibility(View.GONE);
            viewDrawerLnStudentReading.setVisibility(View.VISIBLE);
        }
        cacheSize = FileUtil.getCacheSize();
        viewDrawerTvCacheTotalSize.setText(String.format("%s%s",
                getString(R.string.total_), formatFileSize(cacheSize)));
    }

    private String formatFileSize(long size) {
        DecimalFormat format = new DecimalFormat("######.00");
        if (size < 1024) {
            return size + "b";
        } else if (size < 1024 * 1024) {
            float kb = size / 1024f;
            return format.format(kb) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            float mb = size / 1024f / 1024f;
            return format.format(mb) + "MB";
        } else {
            float gb = size / 1024f / 1024f / 1024f;
            return format.format(gb) + "GB";
        }
    }

    @Override
    public void onResponse(LResponse response) {
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response);
            return;
        }
        switch (response.requestCode) {
            case API_RESULTS_COUNT:
                CheckCount checkCount = new Gson().fromJson(response.body, CheckCount.class);
                viewDrawerTvNotRatedCount.setText(getString(R.string.total__count, checkCount.check_false));
                viewDrawerTvRatedCount.setText(getString(R.string.total__count, checkCount.check_true));
                break;
        }
    }
}
