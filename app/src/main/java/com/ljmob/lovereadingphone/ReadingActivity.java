package com.ljmob.lovereadingphone;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.ljmob.lovereadingphone.adapter.ReadingStatusPagerAdapter;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.entity.Music;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.entity.Section;
import com.ljmob.lovereadingphone.fragment.NotRatedResultFragment;
import com.ljmob.lovereadingphone.fragment.RatedResultFragment;
import com.ljmob.lovereadingphone.fragment.RecorderFragment;
import com.ljmob.lovereadingphone.fragment.UploadResultFragment;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.ContentFormatter;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.blurry.Blurry;
import pl.droidsonroids.gif.GifImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/23.
 * 朗读界面
 */
public class ReadingActivity extends AppCompatActivity implements
        Runnable, LRequestTool.OnResponseListener
        , LRequestTool.OnUploadListener {

    private static final int API_RESULTS_UPLOAD = 1;

    @Bind(R.id.toolbar_trans)
    Toolbar toolbarTrans;
    @Bind(R.id.activity_reading_tvAuthor)
    TextView activityReadingTvAuthor;
    @Bind(R.id.activity_reading_tvReadCount)
    TextView activityReadingTvReadCount;
    @Bind(R.id.activity_reading_imgBackground)
    ImageView activityReadingImgBackground;
    @Bind(R.id.activity_reading_mask)
    View activityReadingMask;

    @Bind(R.id.activity_reading_lnHeadRecord)
    LinearLayout activityReadingLnHeadRecord;
    @Bind(R.id.activity_reading_lnHeadResult)
    LinearLayout activityReadingLnHeadResult;

    @Bind(R.id.activity_reading_scContent)
    ScrollView activityReadingScContent;
    @Bind(R.id.activity_reading_tvChecker)
    TextView activityReadingTvChecker;
    @Bind(R.id.activity_reading_rbRate)
    AppCompatRatingBar activityReadingRbRate;
    @Bind(R.id.activity_reading_pagerStatus)
    ViewPager activityReadingPagerStatus;
    @Bind(R.id.activity_reading_lnContent)
    LinearLayout activityReadingLnContent;
    @Bind(R.id.activity_reading_imgCountDown)
    GifImageView activityReadingImgCountDown;

    List<Fragment> fragments;
    MenuItem shareMenuItem;

    Article article;
    Music music;
    Result result;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Status currentStatus = Status.record;
    private RecorderFragment recorderFragment;
    private UploadResultFragment uploadResultFragment;
    private NotRatedResultFragment notRatedResultFragment;
    private RatedResultFragment ratedResultFragment;

    private Dialog uploadDialog;
    private Dialog mixDialog;
    LRequestTool requestTool;
    private TextView dialogUploadTvProgress;
    private ProgressBar dialogUploadPbProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = (Result) getIntent().getSerializableExtra("result");
        if (result != null) {
            if (result.score.size() == 0) {
                currentStatus = Status.notRatedResult;
            } else {
                currentStatus = Status.ratedResult;
            }
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
        initData();

        if (MyApplication.blurryBg != null) {
            activityReadingImgBackground.setImageBitmap(MyApplication.blurryBg);
            activityReadingMask.setAlpha(0.4f);
        } else {
            imageLoader.displayImage(NetConstant.ROOT_URL + article.cover_img.cover_img.small.url,
                    activityReadingImgBackground,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            makeBlurry();
                        }
                    });
        }
        makeViewByStatus();
        requestTool = new LRequestTool(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reading, menu);
        shareMenuItem = menu.findItem(R.id.action_share);
        switch (currentStatus) {
            case record:
                shareMenuItem.setVisible(false);
                break;
            default:
                shareMenuItem.setVisible(true);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private void doShare() {
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
    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (recorderFragment.isRecording()) {
            activityReadingScContent.post(new Runnable() {
                @Override
                public void run() {
                    activityReadingScContent.scrollBy(0, 1);
                }
            });
            try {
                Thread.sleep(26);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(LResponse response) {
        if (uploadDialog != null && uploadDialog.isShowing()) {
            uploadDialog.dismiss();
        }
        if (response.responseCode == 401) {
            startActivity(new Intent(this, LoginActivity.class));
            ToastUtil.show(R.string.toast_login_timeout);
            return;
        }
        if (response.responseCode != 200 && response.responseCode != 201) {
            ToastUtil.serverErr(response);
            return;
        }
        switch (response.requestCode) {
            case API_RESULTS_UPLOAD:
                ToastUtil.show(R.string.toast_upload_ok);
                finish();
                break;
        }
    }

    @Override
    public void onStartUpload(LResponse response) {

    }

    @Override
    public void onUploading(float progress) {
        if (dialogUploadTvProgress == null) {
            return;
        }
        dialogUploadTvProgress.setText(getString(R.string.upload_, (int) (progress * 100) + "%"));
        dialogUploadPbProgress.setProgress((int) (progress * 100));
    }

    @Override
    public void onUploaded(LResponse response) {

    }

    private void initData() {
        activityReadingTvAuthor.setText(article.author);
        activityReadingTvReadCount.setText(String.format("%d", article.count));

        if (fragments == null) {
            fragments = new ArrayList<>();
            recorderFragment = new RecorderFragment();
            uploadResultFragment = new UploadResultFragment();
            notRatedResultFragment = new NotRatedResultFragment();
            ratedResultFragment = new RatedResultFragment();

            fragments.add(recorderFragment);
            fragments.add(uploadResultFragment);
            fragments.add(notRatedResultFragment);
            fragments.add(ratedResultFragment);
        }
        activityReadingPagerStatus.setAdapter(
                new ReadingStatusPagerAdapter(getSupportFragmentManager(), fragments));
        for (Section s : article.sections) {
            View sectionView = getLayoutInflater()
                    .inflate(R.layout.item_section, activityReadingLnContent, false);
            SectionHolder holder = new SectionHolder(sectionView);

            ContentFormatter.formatSection(article.article_type,
                    holder.primarySectionTitle, holder.primarySectionContent);
            if (article.sections.size() == 1) {
                holder.primarySectionTitle.setVisibility(View.GONE);
            } else {
                holder.primarySectionTitle.setText(s.title);
            }
            holder.primarySectionContent.setText(s.content);
            activityReadingLnContent.addView(sectionView);
        }
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
                while (activityReadingMask.getAlpha() > 0.4f) {
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
            }
        }).start();
    }

    private void makeViewByStatus() {
        switch (currentStatus) {
            case record:
                activityReadingLnHeadRecord.setVisibility(View.VISIBLE);
                activityReadingLnHeadResult.setVisibility(View.GONE);
                if (shareMenuItem != null) {
                    shareMenuItem.setVisible(false);
                }
                //TODO gif
                recorderFragment.setMetaData(music, article);
                activityReadingPagerStatus.setCurrentItem(0);
                break;
            case upload:
                activityReadingLnHeadRecord.setVisibility(View.VISIBLE);
                activityReadingLnHeadResult.setVisibility(View.GONE);
                if (shareMenuItem != null) {
                    shareMenuItem.setVisible(false);
                }
                activityReadingPagerStatus.setCurrentItem(1);
                break;
            case notRatedResult:
                ToastUtil.show("未打分状态仅教师可见");
                activityReadingLnHeadRecord.setVisibility(View.GONE);
                activityReadingLnHeadResult.setVisibility(View.VISIBLE);
                activityReadingTvChecker.setVisibility(View.GONE);
                activityReadingRbRate.setVisibility(View.GONE);
                activityReadingPagerStatus.setCurrentItem(2);
                break;
            case ratedResult:
                activityReadingLnHeadRecord.setVisibility(View.GONE);
                activityReadingLnHeadResult.setVisibility(View.VISIBLE);
                activityReadingTvChecker.setVisibility(View.VISIBLE);
                activityReadingRbRate.setVisibility(View.VISIBLE);
                if (shareMenuItem != null) {
                    shareMenuItem.setVisible(true);
                }
                activityReadingPagerStatus.setCurrentItem(3);
                break;
        }
        activityReadingScContent.post(new Runnable() {
            @Override
            public void run() {
                activityReadingScContent.smoothScrollTo(0, 0);
            }
        });
    }

    public void setCurrentStatus(Status currentStatus) {
        this.currentStatus = currentStatus;
        makeViewByStatus();
    }

    public void startScrolling() {
        new Thread(this).start();
    }

    public void showMixDialog() {
        if (mixDialog == null) {
            mixDialog = new Dialog(this, R.style.AppTheme_DownloadDialog);
            mixDialog.setContentView(R.layout.dialog_mix);
            mixDialog.setCancelable(false);
        }
        mixDialog.show();
    }

    public void dismissMixDialog() {
        if (mixDialog != null && mixDialog.isShowing()) {
            mixDialog.dismiss();
        }
    }

    public void setRecorderFile(File recorderFile) {
        uploadResultFragment.setRecorderFile(recorderFile);
    }

    public void upload(File uploadFile, String feeling) {
        if (uploadDialog == null) {
            uploadDialog = new Dialog(this, R.style.AppTheme_DownloadDialog);
        }
        uploadDialog.setContentView(R.layout.dialog_upload);
        uploadDialog.setCancelable(false);
        dialogUploadTvProgress = (TextView) uploadDialog.findViewById(R.id.dialog_upload_tvProgress);
        dialogUploadPbProgress = (ProgressBar) uploadDialog.findViewById(R.id.dialog_upload_pbProgress);
        uploadDialog.show();

        dialogUploadTvProgress.setText(getString(R.string.upload_, "0%"));
        dialogUploadPbProgress.setProgress(0);

        requestTool.setOnUploadListener(this);
        DefaultParam param = new DefaultParam();
        param.put("article_id", article.id);
        param.put("music_id", music.id);
        param.put("file_url", uploadFile);
        param.put("feeling", feeling);
        requestTool.doPost(NetConstant.ROOT_URL + NetConstant.API_RESULTS, param, API_RESULTS_UPLOAD);
    }

    public void startCountDown() {
        activityReadingImgCountDown.setVisibility(View.VISIBLE);
    }

    public void stopCountDown() {
        activityReadingImgCountDown.setVisibility(View.INVISIBLE);
    }

    public enum Status {
        record, upload, notRatedResult, ratedResult
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_section.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class SectionHolder {
        @Bind(R.id.primarySectionTitle)
        TextView primarySectionTitle;
        @Bind(R.id.primarySectionContent)
        TextView primarySectionContent;

        SectionHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
