package com.happysong.android;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.gson.Gson;
import com.happysong.android.adapter.ReadingStatusPagerAdapter;
import com.happysong.android.context.MyApplication;
import com.happysong.android.entity.Article;
import com.happysong.android.entity.Music;
import com.happysong.android.entity.QiniuToken;
import com.happysong.android.entity.Result;
import com.happysong.android.entity.Section;
import com.happysong.android.fragment.CountDownFragment;
import com.happysong.android.fragment.MyReadingFragment;
import com.happysong.android.fragment.NotRatedResultFragment;
import com.happysong.android.fragment.RatedResultFragment;
import com.happysong.android.fragment.RecorderFragment;
import com.happysong.android.fragment.UploadResultFragment;
import com.happysong.android.net.NetConstant;
import com.happysong.android.service.PlayerService;
import com.happysong.android.util.ContentFormatter;
import com.happysong.android.util.DefaultParam;
import com.happysong.android.util.HeadSetTool;
import com.ljmob.quicksharesdk.ShareTool;
import com.ljmob.quicksharesdk.entity.Shareable;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.londonx.qiniuuploader.QiniuUploader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import jp.wasabeef.blurry.Blurry;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/23.
 * 朗读界面
 */
public class ReadingActivity extends AppCompatActivity implements
        Runnable, LRequestTool.OnResponseListener
        , ServiceConnection {

    private static final int API_RESULTS_UPLOAD = 1;
    private static final int API_HISTORY = 2;
    private static final int API_QINIU_TOKEN = 3;

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

    @Bind(R.id.activity_reading_tvReader)
    TextView activityReadingTvReader;
    @Bind(R.id.activity_reading_tvSchoolClass)
    TextView activityReadingTvSchoolClass;

    List<Fragment> fragments;
    MenuItem shareMenuItem;

    Article article;
    Music music;
    Result result;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Status currentStatus = Status.record;

    private CountDownFragment countDownFragment;

    private RecorderFragment recorderFragment;
    private UploadResultFragment uploadResultFragment;
    private NotRatedResultFragment notRatedResultFragment;
    private RatedResultFragment ratedResultFragment;

    private Dialog uploadDialog;
    private Dialog mixDialog;
    private LRequestTool requestTool;
    private TextView dialogUploadTvProgress;
    private ProgressBar dialogUploadPbProgress;
    private Shareable shareable;
    private PlayerService playerService;
    private boolean isRetry;

    private File fileWaiting;//七牛token失效时才不为null
    private QiniuUploader qiniuUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = (Result) getIntent().getSerializableExtra("result");
        requestTool = new LRequestTool(this);
        if (result != null) {
            if (result.score.size() == 0) {
                currentStatus = Status.notRatedResult;
            } else {
                currentStatus = Status.ratedResult;
                if (MyApplication.currentUser != null) {
                    DefaultParam param = new DefaultParam();
                    param.put("result_id", result.id);
                    requestTool.doPost(NetConstant.ROOT_URL + NetConstant.API_HISTORY, param, API_HISTORY);
                }
            }
            article = result.article;
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
            if (!HeadSetTool.isHeadSetConnected(this)) {
                ToastUtil.show(R.string.toast_headset);
            }
            currentStatus = Status.record;
        }
        setContentView(R.layout.activity_reading);
        ButterKnife.bind(this);
        bindService(new Intent(this, PlayerService.class), this, Context.BIND_AUTO_CREATE);
        setSupportActionBar(toolbarTrans);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(article.title);
        }
        initData();

        if (MyApplication.blurryBg != null && !MyApplication.blurryBg.isRecycled()
                && MyApplication.blurryName.equals(article.qiniu_url == null ?
                article.cover_img.cover_img.small.url :
                article.qiniu_url)) {
            activityReadingImgBackground.setImageBitmap(MyApplication.blurryBg);
            activityReadingMask.setAlpha(0.4f);
        } else {
            imageLoader.displayImage(article.qiniu_url == null ?
                            (NetConstant.ROOT_URL + article.cover_img.cover_img.small.url) :
                            article.qiniu_url,
                    activityReadingImgBackground,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            makeBlurry();
                        }
                    });
        }
        if (currentStatus != Status.record) {
            hideCountDown();
        }
        makeViewByStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                exitByStatus();
                break;
            case R.id.action_share:
                if (result == null) {
                    break;
                }
                doShare();

                shareable = new Shareable();
                shareable.content = result.user.team_classes.get(0).school.name
                        + result.user.team_classes.get(0).grade.name + result.user.team_classes.get(0).name;
                if (result.feeling.length() != 0) {
                    shareable.content += "\n“"
                            + result.feeling + "”";
                }
                shareable.imgFullUrl = result.article.qiniu_url == null ?
                        (NetConstant.ROOT_URL + result.article.cover_img.cover_img.url) :
                        result.article.qiniu_url;
                shareable.title = result.article.title + " - " + result.user.name;
                shareable.url = String.format(shareable.url, result.id);
                break;
            case R.id.action_share_wechat:
                new ShareTool(new Wechat(this), shareable).share();
                break;
            case R.id.action_share_wechat_moment:
                new ShareTool(new WechatMoments(this), shareable).share();
                break;
            case R.id.action_share_qq:
                new ShareTool(new QQ(this), shareable).share();
                break;
            case R.id.action_share_qq_zone:
                new ShareTool(new QZone(this), shareable).share();
                break;
            case R.id.action_share_sina_weibo:
                new ShareTool(new SinaWeibo(this), shareable).share();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        exitByStatus();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long sleep = (long) (320 * (1 / getResources().getDimension(R.dimen.auto_scroll_speed)));
        while (recorderFragment.isRecording() ||//正在录制
                uploadResultFragment.isPlaying() ||//正在回放
                (playerService != null && playerService.isPlaying())//正在播放
                ) {
            if (activityReadingScContent == null) {
                break;
            }
            activityReadingScContent.post(new Runnable() {
                @Override
                public void run() {
                    activityReadingScContent.scrollBy(0, 1);
                }
            });
            try {
                Thread.sleep(sleep);
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
            Intent loginIntent = new Intent(this, LoginActivity.class);
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
        switch (response.requestCode) {
            case API_RESULTS_UPLOAD:
                ToastUtil.show(R.string.toast_upload_ok);
                MyReadingFragment.hasDataChanged = true;
                finish();
                break;
            case API_QINIU_TOKEN:
                if (qiniuUploader == null) {
                    if (uploadDialog != null && uploadDialog.isShowing()) {
                        uploadDialog.dismiss();
                    }
                    ToastUtil.show(R.string.toast_qiniu_token);
                    break;
                }
                QiniuToken token = new Gson().fromJson(response.body, QiniuToken.class);
                QiniuUploader.setToken(token.uptoken);
                qiniuUploader.upload(fileWaiting);
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        playerService = ((PlayerService.PlayerBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
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
            if (result != null) {
                if (result.score.size() == 0) {
                    notRatedResultFragment.setResult(result);
                } else {
                    ratedResultFragment.setResult(result);
                }
            }

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
                if (s.author == null || s.author.length() == 0) {
                    holder.primarySectionTitle.setText(String.format("%s", s.title));
                } else {
                    holder.primarySectionTitle.setText(String.format("%s（%s）", s.title, s.author));
                }
            }
            holder.primarySectionContent.setText(s.content);
            activityReadingLnContent.addView(sectionView);
        }
    }

    private void makeBlurry() {
        activityReadingImgBackground.post(new Runnable() {//图片加载完成后
            @Override
            public void run() {
                try {
                    Blurry.with(ReadingActivity.this)
                            .radius(16)
                            .sampling(8)
                            .color(0x33FFFFFF)
                            .capture(activityReadingImgBackground)
                            .into(activityReadingImgBackground);
                } catch (Exception ignore) {
                }
                if (MyApplication.blurryBg != null && !MyApplication.blurryBg.isRecycled()) {
                    MyApplication.blurryBg.recycle();
                }
                MyApplication.blurryBg = ((BitmapDrawable) activityReadingImgBackground
                        .getDrawable()).getBitmap();
                MyApplication.blurryName = article.qiniu_url == null ?
                        article.cover_img.cover_img.small.url :
                        article.qiniu_url;

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
                activityReadingLnHeadRecord.setVisibility(View.GONE);
                activityReadingLnHeadResult.setVisibility(View.VISIBLE);
                activityReadingTvChecker.setVisibility(View.GONE);
                activityReadingRbRate.setVisibility(View.GONE);
                activityReadingPagerStatus.setCurrentItem(2);
                activityReadingTvReader.setText(result.user.name);

                if (result.user.team_classes.size() == 0) {//学生
                    activityReadingTvSchoolClass.setVisibility(View.GONE);
                } else {
                    activityReadingTvSchoolClass.setVisibility(View.VISIBLE);
                    String schoolClass = String.format("%s - %s%s",
                            result.user.team_classes.get(0).school.name,
                            result.user.team_classes.get(0).grade.name,
                            result.user.team_classes.get(0).name);
                    activityReadingTvSchoolClass.setText(schoolClass);
                }
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
                activityReadingRbRate.setRating(result.score.get(0).score);
                activityReadingTvReader.setText(result.user.name);
                if (result.user.team_classes.size() == 0) {//学生
                    activityReadingTvSchoolClass.setVisibility(View.GONE);
                } else {
                    activityReadingTvSchoolClass.setVisibility(View.VISIBLE);
                    String schoolClass = String.format("%s - %s%s",
                            result.user.team_classes.get(0).school.name,
                            result.user.team_classes.get(0).grade.name,
                            result.user.team_classes.get(0).name);
                    activityReadingTvSchoolClass.setText(schoolClass);
                }
                activityReadingTvChecker.setText(getString(R.string.checker_, result.score.get(0).user.name));
                if (ratedResultFragment.getResult() == null) {
                    ratedResultFragment.setResult(result);
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

    private void exitByStatus() {
        if (currentStatus == Status.record) {
            if (recorderFragment.isRecording() || recorderFragment.isPaused()) {
                new MaterialDialog.Builder(this)
                        .theme(Theme.LIGHT)
                        .title(R.string.dialog_recording)
                        .content(R.string.recording_exit_confirm)
                        .positiveText(R.string.recording_exit)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog,
                                                @NonNull DialogAction dialogAction) {
                                finish();
                            }
                        })
                        .show();
                return;
            }
        }
        if (currentStatus == Status.upload) {
            new MaterialDialog.Builder(this)
                    .theme(Theme.LIGHT)
                    .title(R.string.dialog_upload)
                    .content(R.string.upload_exit_confirm)
                    .positiveText(R.string.recording_exit)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog,
                                            @NonNull DialogAction dialogAction) {
                            finish();
                        }
                    })
                    .show();
            return;
        }
        finish();
    }

    private void doShare() {
        BottomSheet shareSheet = new BottomSheet.Builder(this)
                .title(R.string.share_)
                .sheet(R.menu.menu_share)
                .listener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        onOptionsItemSelected(item);
                        return false;
                    }
                })
                .build();
        shareSheet.show();
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

    public void upload(File uploadFile, final String feeling) {
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

        qiniuUploader = new QiniuUploader();
        qiniuUploader.setUploadListener(new QiniuUploader.UploadListener() {
            @Override
            public void onUploaded(@NonNull String fileKey, int index) {
                DefaultParam param = new DefaultParam();
                param.put("article_id", article.id);
                param.put("music_id", music.id);
                param.put("file_key", fileKey);
                param.put("feeling", feeling);
                requestTool.doPost(NetConstant.ROOT_URL + NetConstant.API_RESULTS, param, API_RESULTS_UPLOAD);
            }

            @Override
            public void onUploading(@NonNull String fileKey, float progress) {
                if (dialogUploadTvProgress == null) {
                    return;
                }
                dialogUploadTvProgress.setText(getString(R.string.upload_, (int) (progress * 100) + "%"));
                dialogUploadPbProgress.setProgress((int) (progress * 100));
            }

            @Override
            public void onUploadingErr(@NonNull Error error) {
                switch (error) {
                    case network:
                        ToastUtil.show(R.string.toast_qiniu_token_network);
                        break;
                    case internal:
                        ToastUtil.show(R.string.toast_qiniu_token_internal);
                        break;
                }
            }
        });
        if (QiniuUploader.isTokenValid()) {
            qiniuUploader.upload(uploadFile);
        } else {
            this.fileWaiting = uploadFile;
            requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_QINIU_TOKEN,
                    new DefaultParam(), API_QINIU_TOKEN);
        }
    }

    public void startCountDown() {
        if (countDownFragment == null) {
            countDownFragment = (CountDownFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.activity_reading_fragmentCountDown);
        }
        countDownFragment.setVisible(true);
        if (isRetry) {
            countDownFragment.resetAnim();
        } else {
            isRetry = true;//第二次startCountDown一定是重读
            countDownFragment.startAnim();
        }
    }

    public void stopCountDown() {
        if (countDownFragment == null) {
            countDownFragment = (CountDownFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.activity_reading_fragmentCountDown);
        }
        countDownFragment.setVisible(false);
    }

    public void hideCountDown() {
        if (countDownFragment == null) {
            countDownFragment = (CountDownFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.activity_reading_fragmentCountDown);
        }
        countDownFragment.setVisible(false);
    }

    public void setRatedResult(@NonNull Result result) {
        if (result.score.size() == 0) {
            return;
        }
        this.result = result;
        ratedResultFragment.setResult(result);
        currentStatus = Status.ratedResult;
        makeViewByStatus();
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
