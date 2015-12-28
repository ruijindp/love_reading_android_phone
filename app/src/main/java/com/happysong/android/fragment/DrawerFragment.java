package com.happysong.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
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
import com.happysong.android.ChangePasswordActivity;
import com.happysong.android.FeedbackActivity;
import com.happysong.android.ListenedActivity;
import com.happysong.android.LoginActivity;
import com.happysong.android.MainActivity;
import com.happysong.android.MyReadingActivity;
import com.happysong.android.R;
import com.happysong.android.context.MyApplication;
import com.happysong.android.entity.CheckCount;
import com.happysong.android.entity.TeamClass;
import com.happysong.android.entity.User;
import com.happysong.android.net.NetConstant;
import com.happysong.android.util.DefaultParam;
import com.happysong.android.util.SimpleImageLoader;
import com.londonx.lutil.Lutil;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.FileUtil;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.soundcloud.android.crop.Crop;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

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
    private static final int API_AVATAR = 3;

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
    @Bind(R.id.view_drawer_lnCleanCache)
    LinearLayout viewDrawerLnCleanCache;
    @Bind(R.id.view_drawer_lnChangePassword)
    LinearLayout viewDrawerLnChangePassword;
    @Bind(R.id.view_drawer_lnExit)
    LinearLayout viewDrawerLnExit;
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
    @Bind(R.id.view_drawer_div)
    View viewDrawerDiv;

    private MaterialDialog cacheDialog;
    private long cacheSize;
    LRequestTool requestTool;
    private File tempAvatar;

    long lastGetCountAt;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == Crop.REQUEST_PICK) {
            try {
                tempAvatar = new File(FileUtil.getCacheFolder(), "temp.png");
                Crop.of(result.getData(), Uri.fromFile(tempAvatar)).asSquare().start(getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        if (requestCode == Crop.REQUEST_CROP) {
            if (tempAvatar == null || !tempAvatar.exists() || tempAvatar.length() == 0) {
                ToastUtil.show(R.string.toast_avatar_err);
                return;
            }
            viewDrawerImgHead.setImageResource(R.color.test);
            DefaultParam param = new DefaultParam();
            param.put("avatar", tempAvatar);
            requestTool.doPost(NetConstant.ROOT_URL + NetConstant.API_AVATAR, param, API_AVATAR);
        }
    }

    @OnClick(R.id.view_drawer_imgHead)
    protected void changeAvatar() {
        Crop.pickImage(getActivity());
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
                                                cacheSize = 0;
                                                viewDrawerTvCacheTotalSize.setText(String.format("%s%s",
                                                        getString(R.string.total_), formatFileSize(cacheSize)));
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
        cacheDialog.setContent(String.format("%s%s",
                getString(R.string.total_), formatFileSize(cacheSize)));
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
        if (MyApplication.currentUser == null) {
            return;
        }
        requestTool.doPost(NetConstant.ROOT_URL + NetConstant.USER_SIGN_OUT,
                new DefaultParam(), USER_SIGN_OUT);
        MyApplication.currentUser = null;
//        Lutil.preferences.edit().clear().apply();
        Lutil.preferences.edit().remove("UB64").apply();
        MobclickAgent.onProfileSignOff();
        ((MainActivity) getActivity()).clearAvatar();
        initDrawerViews();
    }

    private void initDrawerViews() {
        if (MyApplication.currentUser == null) {//默认显示“我的朗读”
            viewDrawerLnMyReading.setVisibility(View.VISIBLE);
            viewDrawerLnStudentReading.setVisibility(View.GONE);

            viewDrawerDiv.setVisibility(View.INVISIBLE);
            viewDrawerLnChangePassword.setVisibility(View.INVISIBLE);
            viewDrawerLnExit.setVisibility(View.INVISIBLE);
        } else {
            if (MyApplication.currentUser.role == User.Role.student) {
                viewDrawerLnMyReading.setVisibility(View.VISIBLE);
                viewDrawerLnStudentReading.setVisibility(View.GONE);
            } else {
                viewDrawerLnMyReading.setVisibility(View.GONE);
                viewDrawerLnStudentReading.setVisibility(View.VISIBLE);
            }
            viewDrawerDiv.setVisibility(View.VISIBLE);
            viewDrawerLnChangePassword.setVisibility(View.VISIBLE);
            viewDrawerLnExit.setVisibility(View.VISIBLE);
        }
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

            SimpleImageLoader.displayImage(MyApplication.currentUser.avatar.avatar.big.url,
                    viewDrawerImgHead);
            viewDrawerTvUserName.setText(MyApplication.currentUser.name);

            List<TeamClass> teamClasses = MyApplication.currentUser.team_classes;
            String schoolClass;
            if (teamClasses.size() != 0 && teamClasses.size() < 2) {//学生
                TeamClass teamClass = teamClasses.get(0);
                schoolClass = String.format("%s - %s%s", teamClass.school.name, teamClass.grade.name, teamClass.name);
            } else {//教师有多个班级，某些教师有多个学校
                int schoolCount = 0;
                int lastSchoolId = 0;
                for (TeamClass t : teamClasses) {
                    if (t.school.id != lastSchoolId) {
                        schoolCount++;
                    }
                }
                if (schoolCount == 1) {
                    schoolClass = teamClasses.get(0).school.name;
                } else {
                    schoolClass = getString(R.string._school_count, teamClasses.get(0).school.name, schoolCount);
                }
            }
            if (schoolClass == null) {
                viewDrawerTvSchoolClass.setVisibility(View.INVISIBLE);
            } else {
                viewDrawerTvSchoolClass.setVisibility(View.VISIBLE);
                viewDrawerTvSchoolClass.setText(schoolClass);
            }
            if (MyApplication.currentUser.sex == null) {
                viewDrawerImgSex.setVisibility(View.INVISIBLE);
            } else {
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
        if (response.responseCode == 401) {
            Intent loginIntent = new Intent(getContext(), LoginActivity.class);
            loginIntent.putExtra("isReLogin", true);
            startActivity(loginIntent);
            return;
        }
        if (response.responseCode == 0) {
            ToastUtil.show(R.string.toast_server_err_0);
            return;
        }
        if (response.responseCode != 200 && response.responseCode != 201) {
            ToastUtil.serverErr(response);
            return;
        }
        if (!response.body.startsWith("[") && !response.body.startsWith("{")) {
            ToastUtil.show(R.string.toast_server_err_1);
            return;
        }
        if (viewDrawerTvNotRatedCount == null) {
            return;
        }
        if (!isVisible()) {
            return;
        }
        switch (response.requestCode) {
            case API_RESULTS_COUNT:
                lastGetCountAt = System.currentTimeMillis();
                CheckCount checkCount = new Gson().fromJson(response.body, CheckCount.class);
                viewDrawerTvNotRatedCount.setText(getString(R.string.total__count, checkCount.check_false));
                viewDrawerTvRatedCount.setText(getString(R.string.total__count, checkCount.check_true));
                break;
            case API_AVATAR:
                ToastUtil.show(R.string.toast_avatar_ok);
                User.Avatar avatar = null;
                try {
                    JSONObject jsonObject = new JSONObject(response.body);
                    String avatarString = jsonObject.get("avatar").toString();
                    avatar = new Gson().fromJson(avatarString, User.Avatar.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (avatar == null) {
                    break;
                }
                SimpleImageLoader.displayImage(avatar.avatar.big.url, viewDrawerImgHead);
                ((MainActivity) getActivity()).setAvatar(avatar.avatar.normal.url);
                MyApplication.currentUser.avatar = avatar;
                String userB64 = Base64.encodeToString(new Gson()
                        .toJson(MyApplication.currentUser).getBytes(), Base64.DEFAULT);
                SharedPreferences.Editor editor = Lutil.preferences.edit();
                editor.putString("UB64", userB64);
                editor.apply();
                break;
        }
    }
}
