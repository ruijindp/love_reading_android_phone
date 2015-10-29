package com.ljmob.lovereadingphone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.ljmob.lovereadingphone.adapter.MainPagerAdapter;
import com.ljmob.lovereadingphone.fragment.PlayerBarFragment;
import com.londonx.lutil.util.ToastUtil;

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

    private PlayerBarFragment playerBarFragment;

    boolean isAppBarHided;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        playerBarFragment = (PlayerBarFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_main_fragmentPlayer);

        primaryViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
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
        //TODO filter result for rank
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO get playing from service
        showPlayerBar();
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

    public void showAppBar() {
        if (isAppBarHided) {
            toolbarMain.animate().translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2)).start();
            showPlayerBar();
            isAppBarHided = false;
        }
    }

    public void hideAppBar() {
        if (!isAppBarHided) {
            toolbarMain.animate().translationY(-toolbarMain.getHeight())
                    .setInterpolator(new AccelerateInterpolator(2)).start();
            closePlayerBar();
            isAppBarHided = true;
        }
    }

    public void showPlayerBar() {
        View playBar = playerBarFragment.getView();
        if (playBar == null) {
            return;
        }
        playBar.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator(2)).setStartDelay(100).start();
    }

    public void closePlayerBar() {
        View playBar = playerBarFragment.getView();
        if (playBar == null) {
            return;
        }
        playBar.animate().translationY(playBar.getHeight())
                .setInterpolator(new AccelerateInterpolator(2)).start();
    }
}
