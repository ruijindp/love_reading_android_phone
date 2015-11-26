package com.ljmob.lovereadingphone;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ljmob.lovereadingphone.adapter.MusicAdapter;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.entity.Music;
import com.ljmob.lovereadingphone.entity.MusicType;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.service.PlayerService;
import com.ljmob.lovereadingphone.util.DefaultParam;
import com.ljmob.lovereadingphone.util.PermissionUtil;
import com.ljmob.lovereadingphone.view.SimpleStringPopup;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LMediaPlayer;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import jp.wasabeef.blurry.Blurry;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/23.
 * 音乐选择
 */
public class MusicActivity extends AppCompatActivity implements
        LRequestTool.OnResponseListener,
        LRequestTool.OnDownloadListener,
        ServiceConnection, MediaPlayer.OnPreparedListener {
    private static final int API_MUSICS = 1;
    private static final int API_MUSIC_TYPES = 2;
    private static final int DOWNLOAD_FILE = 3;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_music_imgBackground)
    ImageView activityMusicImgBackground;
    @Bind(R.id.activity_music_mask)
    View activityMusicMask;
    @Bind(R.id.activity_music_lnMusicType)
    LinearLayout activityMusicLnMusicType;
    @Bind(R.id.primaryAbsListView)
    ListView primaryAbsListView;
    @Bind(R.id.activity_music_tvStart)
    TextView activityMusicTvStart;
    @Bind(R.id.activity_music_tvCurrentMusicType)
    TextView activityMusicTvCurrentMusicType;

    SimpleStringPopup musicTypePopup;

    Article article;
    Music selectedMusic;
    ImageLoader imageLoader = ImageLoader.getInstance();
    LRequestTool requestTool;
    MusicAdapter adapter;
    List<Music> musics;
    List<MusicType> musicTypes;
    MusicType selectedMusicType;
    LMediaPlayer mediaPlayer;

    private TextView dialogMusicDownloadTvProgress;
    private ProgressBar dialogMusicDownloadPbProgress;
    private boolean isDownloaded;
    private Dialog downloadDialog;

    private PlayerService playerService;

    private long lastPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        article = (Article) getIntent().getSerializableExtra("article");
        if (article == null) {
            ToastUtil.show(R.string.toast_article_err);
            finish();
            return;
        }

        setContentView(R.layout.activity_music);
        ButterKnife.bind(this);
        bindService(new Intent(this, PlayerService.class), this, Context.BIND_AUTO_CREATE);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        if (MyApplication.blurryBg != null && !MyApplication.blurryBg.isRecycled() &&
                article.cover_img.cover_img.small.url.equals(MyApplication.blurryName)) {
            activityMusicImgBackground.setImageBitmap(MyApplication.blurryBg);
            activityMusicMask.setAlpha(0.4f);
        } else {
            imageLoader.displayImage(NetConstant.ROOT_URL + article.cover_img.cover_img.small.url,
                    activityMusicImgBackground,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            makeBlurry();
                        }
                    });
        }
        requestTool = new LRequestTool(this);
        initData();
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
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @OnItemClick(R.id.primaryAbsListView)
    protected void selectAndPlay(View item, final int position) {
        if (musics.indexOf(selectedMusic) == position) {
            return;
        }
        selectedMusic = musics.get(position);

        if (playerService.isPlaying()) {
            playerService.getPlayer().pause();
        }
        MusicAdapter.ViewHolder holder = (MusicAdapter.ViewHolder) item.getTag();
        adapter.setSelectedIndex(position);
        adapter.setPlayingIndex(-1);
        holder.setSelected(true);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        } else {
            mediaPlayer = new LMediaPlayer(null, null, this);
        }
        if (downloadDialog != null) {
            return;
        }
        mediaPlayer.playUrl(NetConstant.ROOT_URL + selectedMusic.file_url);
    }

    @OnClick(R.id.activity_music_tvStart)
    protected void startReading() {
        if (System.currentTimeMillis() - lastPress < 500) {//防止两次进朗读
            return;
        }
        lastPress = System.currentTimeMillis();

        int position = adapter.getSelectedIndex();
        if (position == -1 || selectedMusic == null) {
            ToastUtil.show(R.string.toast_music_err);
            return;
        }
        mediaPlayer.stop();

        if (!PermissionUtil.isAllPermissionAllowed()) {
            new MaterialDialog.Builder(this)
                    .theme(Theme.LIGHT)
                    .title(R.string.dialog_permission_denied)
                    .content(R.string.content_permission_denied)
                    .positiveText(R.string.goto_settings)
                    .negativeText(android.R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog
                                , @NonNull DialogAction dialogAction) {
                            materialDialog.dismiss();
                            startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                        }
                    })
                    .show();
            return;
        }

        requestTool.setOnDownloadListener(this);
        requestTool.download(NetConstant.ROOT_URL + selectedMusic.file_url, DOWNLOAD_FILE);


        downloadDialog = new Dialog(this, R.style.AppTheme_DownloadDialog);
        downloadDialog.setCancelable(false);
        downloadDialog.setContentView(R.layout.dialog_music_donwload);
        dialogMusicDownloadTvProgress = (TextView) downloadDialog
                .findViewById(R.id.dialog_music_download_tvProgress);
        dialogMusicDownloadPbProgress = (ProgressBar) downloadDialog
                .findViewById(R.id.dialog_music_download_pbProgress);
        dialogMusicDownloadPbProgress.setProgress(0);
        dialogMusicDownloadTvProgress.setText(getString(R.string.download_, "0%"));
        activityMusicTvStart.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDownloaded) {
                    return;
                }
                downloadDialog.show();
            }
        }, 100);
    }

    @OnClick(R.id.activity_music_lnMusicType)
    protected void showMusicTypes() {
        if (musicTypePopup == null) {
            return;
        }
        musicTypePopup.showAsDropDown(activityMusicLnMusicType);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeBlurry() {
        activityMusicImgBackground.post(new Runnable() {//图片加载完成后
            @Override
            public void run() {
                Blurry.with(MusicActivity.this)
                        .radius(16)
                        .sampling(8)
                        .color(0x33FFFFFF)
                        .capture(activityMusicImgBackground)
                        .into(activityMusicImgBackground);
                if (MyApplication.blurryBg != null && !MyApplication.blurryBg.isRecycled()) {
                    MyApplication.blurryBg.recycle();
                }
                MyApplication.blurryBg = ((BitmapDrawable) activityMusicImgBackground
                        .getDrawable()).getBitmap();
                MyApplication.blurryName = article.cover_img.cover_img.small.url;
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
                while (activityMusicMask.getAlpha() > 0.4f) {
                    try {
                        Thread.sleep(24);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activityMusicMask.setAlpha(activityMusicMask.getAlpha() - 0.012f);
                        }
                    });
                }
            }
        }).start();
    }

    private void initData() {
        getMusic();
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_MUSIC_TYPES,
                new DefaultParam(), API_MUSIC_TYPES);
    }

    private void getMusic() {
        DefaultParam param = new DefaultParam();
        if (selectedMusicType != null && selectedMusicType.id != 0) {
            param.put("music_type_id", selectedMusicType.id);
        }
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_MUSICS, param, API_MUSICS);
    }

    @Override
    public void onResponse(LResponse response) {
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
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response);
            return;
        }
        if (!response.body.startsWith("[") && !response.body.startsWith("{")) {
            ToastUtil.show(R.string.toast_server_err_1);
            return;
        }
        switch (response.requestCode) {
            case API_MUSICS:
                musics = new Gson()
                        .fromJson(response.body, new TypeToken<List<Music>>() {
                        }.getType());
                if (adapter == null) {
                    adapter = new MusicAdapter(musics);
                    primaryAbsListView.setAdapter(adapter);
                } else {
                    adapter.setNewData(musics);
                }
                break;
            case API_MUSIC_TYPES:
                musicTypes = new Gson().fromJson(response.body,
                        new TypeToken<List<MusicType>>() {
                        }.getType());
                MusicType defaultMusicType = new MusicType();
                defaultMusicType.id = 0;
                defaultMusicType.name = getString(R.string.all);

                musicTypePopup = new SimpleStringPopup(this, activityMusicLnMusicType);
                musicTypes.add(0, defaultMusicType);
                final List<String> strings = new ArrayList<>();
                for (MusicType mt : musicTypes) {
                    strings.add(mt.name);
                }
                musicTypePopup.setStrings(strings);
                musicTypePopup.setSimpleStringListener(new SimpleStringPopup.SimpleStringListener() {
                    @Override
                    public void selectStringAt(SimpleStringPopup popup, int index) {
                        if (index == 0 && selectedMusicType == null) {
                            return;
                        }
                        adapter.setSelectedIndex(-1);
                        selectedMusicType = musicTypes.get(index);
                        getMusic();
                        activityMusicTvCurrentMusicType.setText(selectedMusicType.name);
                    }
                });
                break;
        }
    }

    @Override
    public void onStartDownload(LResponse response) {
    }

    @Override
    public void onDownloading(float progress) {
        if (dialogMusicDownloadPbProgress == null) {
            return;
        }
        dialogMusicDownloadPbProgress.setProgress((int) (100 * progress));
        dialogMusicDownloadTvProgress.setText(getString(R.string.download_, (int) (100 * progress) + "%"));
    }

    @Override
    public void onDownloaded(LResponse response) {
        isDownloaded = true;

        Intent intent = new Intent(this, ReadingActivity.class);
        intent.putExtra("music", selectedMusic);
        intent.putExtra("article", article);
        if (downloadDialog != null && downloadDialog.isShowing()) {
            downloadDialog.dismiss();
        }
        startActivity(intent);
        finish();
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
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.play();
        primaryAbsListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setPlayingIndex(musics.indexOf(selectedMusic));
            }
        }, 1500);
    }
}
