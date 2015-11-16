package com.ljmob.lovereadingphone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback;
import com.afollestad.materialdialogs.Theme;
import com.ljmob.firimupdate.FirimUpdate;
import com.ljmob.firimupdate.entity.Update;
import com.ljmob.lovereadingphone.adapter.MainPagerAdapter;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.fragment.DrawerFragment;
import com.ljmob.lovereadingphone.fragment.IndexFragment;
import com.ljmob.lovereadingphone.fragment.PlayerBarFragment;
import com.ljmob.lovereadingphone.fragment.RankFragment;
import com.ljmob.lovereadingphone.fragment.RecommendFragment;
import com.ljmob.lovereadingphone.service.PlayerService;
import com.ljmob.lovereadingphone.util.PermissionUtil;
import com.ljmob.lovereadingphone.util.SimpleImageLoader;
import com.soundcloud.android.crop.Crop;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements ServiceConnection, FirimUpdate.OnUpdateListener {
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
    private DrawerFragment drawerFragment;

    boolean isAppBarHided;
    boolean isAvatarSet;

    private PlayerService playerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bindService(new Intent(this, PlayerService.class), this, Context.BIND_AUTO_CREATE);
        playerBarFragment = (PlayerBarFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_main_fragmentPlayer);
        drawerFragment = (DrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_main_fragmentDrawer);

        primaryViewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));

        new FirimUpdate(this).check(this, "564484db748aac4b76000008", "e9400a3620552593c1851beecb8431a0");

        if (!PermissionUtil.isAllPermissionAllowed()) {
            PermissionUtil.request(this);
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
        switch (requestCode) {
            case IndexFragment.ACTION_CATEGORY:
                ((MainPagerAdapter) primaryViewPager.getAdapter()).getItem(0)
                        .onActivityResult(requestCode, resultCode, data);
                break;
            case Crop.REQUEST_PICK:
                drawerFragment.onActivityResult(requestCode, resultCode, data);
                break;
            case Crop.REQUEST_CROP:
                drawerFragment.onActivityResult(requestCode, resultCode, data);
                break;
            case RecommendFragment.ACTION_RECOMMEND_FILTER:
                ((MainPagerAdapter) primaryViewPager.getAdapter()).getItem(1)
                        .onActivityResult(requestCode, resultCode, data);
                break;
            case RankFragment.ACTION_RANK_FILTER:
                ((MainPagerAdapter) primaryViewPager.getAdapter()).getItem(2)
                        .onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerService == null || playerService.getResult() == null) {
            if (playerBarFragment != null) {
                playerBarFragment.hideView(false);
            }
        } else {
            showPlayerBar();
        }
        if (MyApplication.currentUser != null) {
            if (!isAvatarSet) {
                SimpleImageLoader.displayImage(MyApplication.currentUser.avatar.avatar.normal.url,
                        toolbarMainImgHead);
                isAvatarSet = true;
            }
        } else {
            if (isAvatarSet) {
                toolbarMainImgHead.setImageResource(R.mipmap.icon_admin);
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        playerService = ((PlayerService.PlayerBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        playerService = null;
    }

    @Override
    public void onUpdateFound(final Update newUpdate) {
        new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title(R.string.dialog_update)
                .content(newUpdate.changelog + "\n" + getString(R.string.update_now_))
                .positiveText(R.string.update)
                .negativeText(R.string.next_time)
                .onPositive(new SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog,
                                        @NonNull DialogAction dialogAction) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri content_url = Uri.parse(newUpdate.installUrl);
                        intent.setData(content_url);
                        startActivity(intent);
                    }
                })
                .show();
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

    @OnPageChange(R.id.primaryViewPager)
    public void pageTo(int index) {
        showAppBar();
        switch (index) {
            case 0:
                showView(toolbarImgIndex);
                hideView(toolbarImgArticle);
                hideView(toolbarImgRank);
                toolbarImgRight.setImageResource(R.mipmap.icon_search);
                break;
            case 1:
                hideView(toolbarImgIndex);
                showView(toolbarImgArticle);
                hideView(toolbarImgRank);
                toolbarImgRight.setImageResource(R.mipmap.icon_search);
                break;
            case 2:
                hideView(toolbarImgIndex);
                hideView(toolbarImgArticle);
                showView(toolbarImgRank);
                toolbarImgRight.setImageResource(R.mipmap.icon_filter);
                break;
        }
        if (primaryViewPager.getCurrentItem() == index) {
            return;
        }
        primaryViewPager.setCurrentItem(index);
    }

    @OnClick(R.id.toolbar_imgRight)
    protected void rightButtonClicked() {
        if (primaryViewPager.getCurrentItem() != PAGE_RANK) {
            Intent intent = new Intent(this, SearchActivity.class);
            if (primaryViewPager.getCurrentItem() == PAGE_ARTICLE) {
                intent.putExtra("article", true);
            } else if (primaryViewPager.getCurrentItem() == PAGE_RECOMMEND) {
                intent.putExtra("article", false);
            }
            startActivity(intent);
            return;
        }
        Intent filter = new Intent(this, FilterActivity.class);
        startActivityForResult(filter, RankFragment.ACTION_RANK_FILTER);
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
            hidePlayerBar();
            isAppBarHided = true;
        }
    }

    public void showPlayerBar() {
        if (playerService != null && playerService.getResult() != null) {
            playerBarFragment.showView();
        }
    }

    public void hidePlayerBar() {
        if (playerService != null && playerService.getResult() != null) {
            playerBarFragment.hideView(true);
        }
    }

    public void clearAvatar() {
        toolbarMainImgHead.setImageResource(R.mipmap.icon_admin);
        isAvatarSet = false;
    }

    public void setAvatar(String avatarApiUrl) {
        SimpleImageLoader.displayImage(avatarApiUrl, toolbarMainImgHead);
    }

    private void showView(View view) {
        if (view.getAlpha() != 1.0f) {
            view.animate().alpha(1.0f).setDuration(200).start();
        }
    }

    private void hideView(View view) {
        if (view.getAlpha() != 0.54f) {
            view.animate().alpha(0.54f).setDuration(200).start();
        }
    }
}
