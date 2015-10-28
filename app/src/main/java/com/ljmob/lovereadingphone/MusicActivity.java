package com.ljmob.lovereadingphone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ljmob.lovereadingphone.adapter.MusicAdapter;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.entity.Music;
import com.ljmob.lovereadingphone.view.SimpleStringPopup;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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
public class MusicActivity extends AppCompatActivity {
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

    SimpleStringPopup musicTypePopup;

    Article article;
    ImageLoader imageLoader = ImageLoader.getInstance();
    MusicAdapter adapter;
    List<Music> musics;

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
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        if (MyApplication.blurryBg != null) {
            activityMusicImgBackground.setImageBitmap(MyApplication.blurryBg);
            activityMusicMask.setVisibility(View.INVISIBLE);
        } else {
            @DrawableRes int mipmapId;
            try {
                mipmapId = Integer.parseInt(article.cover_img);
            } catch (Exception ignore) {
                mipmapId = 0;
            }
            imageLoader.displayImage(mipmapId == 0 ? article.cover_img : ("drawable://" + mipmapId),
                    activityMusicImgBackground,
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

        //TODO test code
        musics = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Music music = new Music();
            music.name = getString(R.string.test_music_name);
            musics.add(music);
        }
        adapter = new MusicAdapter(musics);
        primaryAbsListView.setAdapter(adapter);
    }

    @OnItemClick(R.id.primaryAbsListView)
    protected void selectAndPlay(View item, int position) {
        MusicAdapter.ViewHolder holder = (MusicAdapter.ViewHolder) item.getTag();
        adapter.setSelectedIndex(position);
        holder.setSelected(true);
    }

    @OnClick(R.id.activity_music_tvStart)
    protected void startReading() {
        int position = adapter.getSelectedIndex();
        if (position == -1) {
            ToastUtil.show(R.string.toast_music_err);
            return;
        }
        Intent intent = new Intent(this, ReadingActivity.class);
        intent.putExtra("music", musics.get(position));
        intent.putExtra("article", article);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.activity_music_lnMusicType)
    protected void showMusicTypes() {
        //TODo
        if (musicTypePopup == null) {
            musicTypePopup = new SimpleStringPopup(this, activityMusicLnMusicType);
            final List<String> strings = new ArrayList<>();
            strings.add("舒缓");
            strings.add("古典");
            strings.add("钢琴曲");
            strings.add("轻音乐");
            musicTypePopup.setStrings(strings);
            musicTypePopup.setSimpleStringListener(new SimpleStringPopup.SimpleStringListener() {
                @Override
                public void selectStringAt(SimpleStringPopup popup, int index) {
                    ToastUtil.show(strings.get(index));
                }
            });
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
                if (MyApplication.blurryBg != null) {
                    MyApplication.blurryBg.recycle();
                }
                MyApplication.blurryBg = ((BitmapDrawable) activityMusicImgBackground
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
                while (activityMusicMask.getAlpha() > 0.05f) {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activityMusicMask.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }
}
