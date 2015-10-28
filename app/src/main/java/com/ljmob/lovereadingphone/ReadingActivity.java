package com.ljmob.lovereadingphone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.cocosw.bottomsheet.BottomSheet;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.entity.Music;
import com.ljmob.lovereadingphone.entity.Result;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.blurry.Blurry;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/23.
 * 朗读界面
 */
public class ReadingActivity extends AppCompatActivity {

    @Bind(R.id.toolbar_trans)
    Toolbar toolbarTrans;
    @Bind(R.id.activity_reading_tvAuthor)
    TextView activityReadingTvAuthor;
    @Bind(R.id.activity_reading_tvReadCount)
    TextView activityReadingTvReadCount;
    @Bind(R.id.primaryContentTextView)
    TextView primaryContentTextView;
    @Bind(R.id.activity_reading_imgBackground)
    ImageView activityReadingImgBackground;
    @Bind(R.id.activity_reading_mask)
    View activityReadingMask;

    @Bind(R.id.activity_reading_imgRetry)
    ImageView activityReadingImgRetry;
    @Bind(R.id.activity_reading_imgRecord)
    ImageView activityReadingImgRecord;
    @Bind(R.id.activity_reading_imgDone)
    ImageView activityReadingImgDone;
    @Bind(R.id.activity_reading_lnHeadRecord)
    LinearLayout activityReadingLnHeadRecord;
    @Bind(R.id.activity_reading_lnHeadResult)
    LinearLayout activityReadingLnHeadResult;
    @Bind(R.id.activity_reading_frameCount)
    FrameLayout activityReadingFrameCount;
    @Bind(R.id.activity_reading_controllerRecord)
    FrameLayout activityReadingControllerRecord;
    @Bind(R.id.activity_reading_tvTimerCurrent)
    TextView activityReadingTvTimerCurrent;
    @Bind(R.id.activity_reading_sbPlayer)
    SeekBar activityReadingSbPlayer;
    @Bind(R.id.activity_reading_tvTimerTotal)
    TextView activityReadingTvTimerTotal;
    @Bind(R.id.activity_reading_imgPlay)
    ImageView activityReadingImgPlay;
    @Bind(R.id.activity_reading_imgLetMeTry)
    ImageView activityReadingImgLetMeTry;
    @Bind(R.id.activity_reading_imgFeeling)
    ImageView activityReadingImgFeeling;
    @Bind(R.id.activity_reading_imgLike)
    ImageView activityReadingImgLike;
    @Bind(R.id.activity_reading_tvLike)
    TextView activityReadingTvLike;
    @Bind(R.id.activity_reading_controllerResult)
    LinearLayout activityReadingControllerResult;

    MenuItem shareMenuItem;

    Article article;
    Music music;
    Result result;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Status currentStatus = Status.record;
    @Bind(R.id.activity_reading_scContent)
    ScrollView activityReadingScContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = (Result) getIntent().getSerializableExtra("result");
        if (result != null) {
            currentStatus = Status.result;
        } else {
            article = (Article) getIntent().getSerializableExtra("article");
            if (article == null) {
                ToastUtil.show(R.string.toast_article_err);
                finish();
                return;
            }
            music = (Music) getIntent().getSerializableExtra("music");
            if (music == null) {
                ToastUtil.show(R.string.toast_music_err);
                finish();
                return;
            }
            currentStatus = Status.record;
        }
        setContentView(R.layout.activity_reading);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarTrans);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(article.title);
        }
        setInitData();

        if (MyApplication.blurryBg != null) {
            activityReadingImgBackground.setImageBitmap(MyApplication.blurryBg);
            activityReadingMask.setVisibility(View.INVISIBLE);
        } else {
            @DrawableRes int mipmapId;
            try {
                mipmapId = Integer.parseInt(article.cover_img);
            } catch (Exception ignore) {
                mipmapId = 0;
            }
            imageLoader.displayImage(mipmapId == 0 ? article.cover_img : ("drawable://" + mipmapId),
                    activityReadingImgBackground,
                    new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            makeBlurry();
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                        }
                    });
        }
        makeViewByStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reading, menu);
        shareMenuItem = menu.findItem(R.id.action_share);
        switch (currentStatus) {
            case record:
                shareMenuItem.setVisible(false);
                break;
            case result:
                shareMenuItem.setVisible(true);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.equals(shareMenuItem)) {
            doShare();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void doShare() {
        //TODO shareSDk
        BottomSheet shareSheet = new BottomSheet.Builder(this)
                .title(R.string.share_)
                .sheet(R.menu.menu_share)
                .listener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ToastUtil.show(item.getTitle() + "");
                        return false;
                    }
                })
                .build();
        shareSheet.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void setInitData() {
        activityReadingTvAuthor.setText(article.author);
        activityReadingTvReadCount.setText(String.format("%d", article.read_count));
        primaryContentTextView.setText(article.content);
    }

    @OnClick(R.id.activity_reading_imgRetry)
    protected void retry() {
        ToastUtil.show("retry");
    }

    @OnClick(R.id.activity_reading_imgRecord)
    protected void recordOrPause() {
        ToastUtil.show("recordOrPause");
    }

    @OnClick(R.id.activity_reading_imgDone)
    protected void done() {
        MaterialDialog feelingDialog = new MaterialDialog.Builder(this)
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
                        //TODO test code
                        EditText etFeeling = (EditText) materialDialog.getCustomView();
                        String feeling = "";
                        if (etFeeling != null) {
                            feeling = etFeeling.getText().toString();
                        }
                        currentStatus = Status.result;
                        makeViewByStatus();
                        ToastUtil.show(feeling);
                    }
                })
                .build();
        feelingDialog.show();
    }

    private void makeBlurry() {
        activityReadingImgBackground.post(new Runnable() {//图片加载完成后
            @Override
            public void run() {
                Blurry.with(ReadingActivity.this)
                        .radius(16)
                        .sampling(8)
                        .color(0x33FFFFFF)
                        .capture(activityReadingImgBackground)
                        .into(activityReadingImgBackground);
                if (MyApplication.blurryBg != null) {
                    MyApplication.blurryBg.recycle();
                }
                MyApplication.blurryBg = ((BitmapDrawable) activityReadingImgBackground
                        .getDrawable()).getBitmap();
                fadeMaskOut();
            }
        });
    }

    private void fadeMaskOut() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (activityReadingMask.getAlpha() > 0.05f) {
                    try {
                        Thread.sleep(24);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activityReadingMask.setAlpha(activityReadingMask.getAlpha() - 0.012f);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityReadingMask.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    private void makeViewByStatus() {
        switch (currentStatus) {
            case record:
                activityReadingLnHeadRecord.setVisibility(View.VISIBLE);
                activityReadingLnHeadResult.setVisibility(View.GONE);
                activityReadingControllerRecord.setVisibility(View.VISIBLE);
                activityReadingControllerResult.setVisibility(View.INVISIBLE);
                if (shareMenuItem != null) {
                    shareMenuItem.setVisible(false);
                }
                break;
            case result:
                activityReadingLnHeadRecord.setVisibility(View.GONE);
                activityReadingLnHeadResult.setVisibility(View.VISIBLE);
                activityReadingControllerRecord.setVisibility(View.INVISIBLE);
                activityReadingControllerResult.setVisibility(View.VISIBLE);
                if (shareMenuItem != null) {
                    shareMenuItem.setVisible(true);
                }
                break;
        }
        activityReadingScContent.post(new Runnable() {
            @Override
            public void run() {
                activityReadingScContent.smoothScrollTo(0, 0);
            }
        });
    }

    private enum Status {
        record, result
    }
}
