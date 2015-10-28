package com.ljmob.lovereadingphone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ljmob.lovereadingphone.adapter.MainPagerAdapter;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.User;
import com.ljmob.lovereadingphone.fragment.PlayBarFragment;
import com.londonx.lutil.util.FileUtil;
import com.londonx.lutil.util.ToastUtil;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
    public static final int INTENT_FILTER = 1;

    private static final int PAGE_ARTICLE = 0;
    private static final int PAGE_RECOMMEND = 1;
    private static final int PAGE_RANK = 2;

    //Header
    @Bind(R.id.toolbar_main_imgHead)
    CircleImageView toolbarMainImgHead;
    @Bind(R.id.activity_main_drawer)
    DrawerLayout activityMainDrawer;
    @Bind(R.id.toolbar_main)
    Toolbar toolbarMain;
    @Bind(R.id.primaryViewPager)
    ViewPager primaryViewPager;
    @Bind(R.id.toolbar_imgIndex)
    ImageView toolbarImgIndex;
    @Bind(R.id.toolbar_imgArticle)
    ImageView toolbarImgArticle;
    @Bind(R.id.toolbar_imgRank)
    ImageView toolbarImgRank;
    @Bind(R.id.toolbar_imgRight)
    ImageView toolbarImgRight;

    //drawer
    @Bind(R.id.activity_main_imgDrawerBg)
    ImageView activityMainImgDrawerBg;
    @Bind(R.id.activity_main_imgHead)
    CircleImageView activityMainImgHead;
    @Bind(R.id.activity_main_tvUserName)
    TextView activityMainTvUserName;
    @Bind(R.id.activity_main_imgSex)
    ImageView activityMainImgSex;
    @Bind(R.id.activity_main_tvSchoolClass)
    TextView activityMainTvSchoolClass;
    @Bind(R.id.activity_main_lnMyReading)
    LinearLayout activityMainLnMyReading;
    @Bind(R.id.activity_main_lnStudentReading)
    LinearLayout activityMainLnStudentReading;
    @Bind(R.id.activity_main_lnListened)
    LinearLayout activityMainLnListened;
    @Bind(R.id.activity_main_lnCleanCache)
    LinearLayout activityMainLnCleanCache;
    @Bind(R.id.activity_main_lnFeedBack)
    LinearLayout activityMainLnFeedBack;
    @Bind(R.id.activity_main_lnChangePassword)
    LinearLayout activityMainLnChangePassword;
    @Bind(R.id.activity_main_lnExit)
    LinearLayout activityMainLnExit;
    private PlayBarFragment playBarFragment;

    boolean isAppBarHided;
    private MaterialDialog cacheDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        playBarFragment = (PlayBarFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_main_fragmentPlayer);

        primaryViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));

        if (MyApplication.currentUser != null &&
                MyApplication.currentUser.role == User.Role.student) {//学生无“学生朗读”
            activityMainLnMyReading.setVisibility(View.VISIBLE);
            activityMainLnStudentReading.setVisibility(View.GONE);
        } else {//教师无“我的朗读”
            activityMainLnMyReading.setVisibility(View.GONE);
            activityMainLnStudentReading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        if (activityMainDrawer.isDrawerOpen(Gravity.LEFT)) {
            activityMainDrawer.closeDrawers();
            return;
        }
        if (primaryViewPager.getCurrentItem() != PAGE_ARTICLE) {
            pageTo(PAGE_ARTICLE);
            return;
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //TODO filter result
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO get playing from service
        showPlayBar();
    }

    @OnClick(R.id.toolbar_main_imgHead)
    protected void openDrawer() {
        activityMainDrawer.openDrawer(Gravity.LEFT);
    }

    @OnClick(R.id.toolbar_imgIndex)
    protected void pageToIndex() {
        pageTo(PAGE_ARTICLE);
    }

    @OnClick(R.id.toolbar_imgArticle)
    protected void pageToArticle() {
        pageTo(PAGE_RECOMMEND);
    }

    @OnClick(R.id.toolbar_imgRank)
    protected void pageToRank() {
        pageTo(PAGE_RANK);
    }

    @OnClick(R.id.toolbar_imgRight)
    protected void doSearch() {
        ToastUtil.show("doSearch");
    }

    @OnPageChange(R.id.primaryViewPager)
    public void pageTo(int index) {
        showAppBar();
        if (primaryViewPager.getCurrentItem() == index) {
            return;
        }
        primaryViewPager.setCurrentItem(index);
    }

    @OnClick(R.id.toolbar_imgRight)
    protected void rightButtonClicked() {
        if (primaryViewPager.getCurrentItem() == PAGE_RANK) {
            Intent filter = new Intent(this, FilterActivity.class);
            startActivityForResult(filter, INTENT_FILTER);
        }
    }

    @OnClick(R.id.activity_main_imgHead)
    protected void changeAvatar() {
        ToastUtil.show("changeAvatar");
    }

    @OnClick(R.id.activity_main_lnMyReading)
    protected void myReading() {
        startActivity(new Intent(this, MyReadingActivity.class));
    }

    @OnClick(R.id.activity_main_lnStudentReading)
    protected void studentReading() {
        startActivity(new Intent(this, MyReadingActivity.class));
    }

    @OnClick(R.id.activity_main_lnListened)
    protected void listened() {
        startActivity(new Intent(this, ListenedActivity.class));
    }

    @OnClick(R.id.activity_main_lnCleanCache)
    protected void cleanCache() {
        if (cacheDialog == null) {
            cacheDialog = new MaterialDialog.Builder(this)
                    .theme(Theme.LIGHT)
                    .title(R.string.dialog_clean_cache)
                    .content(getString(R.string.total_) + formatFileSize(FileUtil.getCacheSize()))
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.show(R.string.toast_clean_ok);
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
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

    @OnClick(R.id.activity_main_lnFeedBack)
    protected void feedback() {
        startActivity(new Intent(this, FeedbackActivity.class));
    }

    @OnClick(R.id.activity_main_lnChangePassword)
    protected void changePassword() {
        startActivity(new Intent(this, ChangePasswordActivity.class));
    }

    @OnClick(R.id.activity_main_lnExit)
    protected void exit() {
        ToastUtil.show("exit");
    }

    public void showAppBar() {
        if (isAppBarHided) {
            toolbarMain.animate().translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2)).start();
            isAppBarHided = false;
        }
    }

    public void hideAppBar() {
        if (!isAppBarHided) {
            toolbarMain.animate().translationY(-toolbarMain.getHeight())
                    .setInterpolator(new AccelerateInterpolator(2)).start();
            isAppBarHided = true;
        }
    }

    public void showPlayBar() {
        View playBar = playBarFragment.getView();
        if (playBar == null) {
            return;
        }
        playBar.animate().translationY(0)
                .setInterpolator(new AccelerateInterpolator(2)).setStartDelay(100).start();
    }

    public void closePlayBar() {
        View playBar = playBarFragment.getView();
        if (playBar == null) {
            return;
        }
        playBar.animate().translationY(playBar.getHeight())
                .setInterpolator(new DecelerateInterpolator(2)).start();
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
}
