package com.ljmob.lovereadingphone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ljmob.lovereadingphone.context.ActivityTool;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.util.SimpleImageLoader;
import com.londonx.lutil.util.TextUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.blurry.Blurry;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/22.
 * 文章详情
 */
public class DetailActivity extends AppCompatActivity {
    @Bind(R.id.activity_detail_imgBackground)
    ImageView activityDetailImgBackground;
    @Bind(R.id.activity_detail_imgCover)
    ImageView activityDetailImgCover;
    @Bind(R.id.activity_detail_tvTitle)
    TextView activityDetailTvTitle;
    @Bind(R.id.activity_detail_tvAuthor)
    TextView activityDetailTvAuthor;
    @Bind(R.id.activity_detail_tvReadCount)
    TextView activityDetailTvReadCount;
    @Bind(R.id.primaryContentTextView)
    TextView primaryContentTextView;
    @Bind(R.id.activity_detail_fabStart)
    FloatingActionButton activityDetailFabStart;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_detail_mask)
    View activity_detail_mask;

    ImageLoader imageLoader = ImageLoader.getInstance();
    Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        article = (Article) getIntent().getSerializableExtra("article");
        if (article == null) {
            ActivityTool.finish(this);
            return;
        }
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        if (MyApplication.blurryBg != null) {
            MyApplication.blurryBg.recycle();
            MyApplication.blurryBg = null;
            System.gc();
        }

        @DrawableRes int mipmapId;
        try {
            mipmapId = Integer.parseInt(article.cover_img);
        } catch (Exception ignore) {
            mipmapId = 0;
        }

        imageLoader.displayImage(mipmapId == 0 ? article.cover_img : ("drawable://" + mipmapId),
                activityDetailImgBackground,
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
        SimpleImageLoader.displayImage(article.cover_img, activityDetailImgCover);
        activityDetailTvTitle.setText(article.title);
        activityDetailTvAuthor.setText(article.author);
        activityDetailTvReadCount.setText(String.format("%d", article.read_count));
        primaryContentTextView.setText(TextUtil.format(article.content));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityTool.finish(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.activity_detail_fabStart)
    protected void startReading() {
        Intent musicIntent = new Intent(this, MusicActivity.class);
        musicIntent.putExtra("article", article);
        startActivity(musicIntent);
    }

    private void makeBlurry() {
        activityDetailImgBackground.post(new Runnable() {//图片加载完成后
            @Override
            public void run() {
                Blurry.with(DetailActivity.this)
                        .radius(16)
                        .sampling(8)
                        .color(0x33FFFFFF)
                        .capture(activityDetailImgBackground)
                        .into(activityDetailImgBackground);
                if (MyApplication.blurryBg != null) {
                    MyApplication.blurryBg.recycle();
                }
                MyApplication.blurryBg = ((BitmapDrawable) activityDetailImgBackground
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
                while (activity_detail_mask.getAlpha() > 0.05f) {
                    try {
                        Thread.sleep(24);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity_detail_mask.setAlpha(activity_detail_mask.getAlpha() - 0.012f);
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity_detail_mask.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }
}