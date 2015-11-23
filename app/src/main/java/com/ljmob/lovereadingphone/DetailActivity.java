package com.ljmob.lovereadingphone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.ljmob.lovereadingphone.context.ActivityTool;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.entity.Section;
import com.ljmob.lovereadingphone.entity.User;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.ljmob.lovereadingphone.util.ContentFormatter;
import com.ljmob.lovereadingphone.util.SimpleImageLoader;
import com.londonx.lutil.util.ConnectionChecker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
    @Bind(R.id.activity_detail_fabStart)
    FloatingActionButton activityDetailFabStart;
    @Bind(R.id.activity_detail_lnContent)
    LinearLayout activityDetailLnContent;
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

        if (MyApplication.blurryBg != null && !MyApplication.blurryBg.isRecycled() &&
                MyApplication.blurryName.equals(article.cover_img.cover_img.small.url)) {
            activityDetailImgBackground.setImageBitmap(MyApplication.blurryBg);
            activity_detail_mask.setAlpha(0.4f);
        } else {
            if (MyApplication.blurryBg != null && !MyApplication.blurryBg.isRecycled()) {
                MyApplication.blurryBg.recycle();
                MyApplication.blurryBg = null;
                System.gc();
            }
            imageLoader.displayImage(NetConstant.ROOT_URL + article.cover_img.cover_img.small.url,
                    activityDetailImgBackground, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            makeBlurry();
                        }
                    });
        }
        SimpleImageLoader.displayImage(article.cover_img.cover_img.normal.url, activityDetailImgCover);
        activityDetailTvTitle.setText(article.title);
        activityDetailTvAuthor.setText(article.author);
        activityDetailTvReadCount.setText(String.format("%d", article.count));

        for (Section s : article.sections) {
            View sectionView = getLayoutInflater().inflate(R.layout.item_section,
                    activityDetailLnContent, false);
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
            activityDetailLnContent.addView(sectionView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.currentUser == null) {
            activityDetailFabStart.setVisibility(View.VISIBLE);
        } else {
            if (MyApplication.currentUser.role != User.Role.student) {
                activityDetailFabStart.setVisibility(View.INVISIBLE);
            } else {
                activityDetailFabStart.setVisibility(View.VISIBLE);
            }
        }
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

    @OnClick(R.id.activity_detail_imgCover)
    protected void zoomImage() {
        Intent intent = new Intent(this, ZoomActivity.class);
        intent.putExtra("image", article.cover_img.cover_img);
        startActivity(intent);
    }


    @OnClick(R.id.activity_detail_fabStart)
    protected void startReading() {
        if (MyApplication.currentUser == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            return;
        }
        if (ConnectionChecker.getNetworkType() != ConnectionChecker.NetworkStatus.WiFi) {
            new MaterialDialog.Builder(this)
                    .theme(Theme.LIGHT)
                    .title(R.string.dialog_wifi)
                    .content(R.string.no_wifi)
                    .positiveText(R.string.return_no_wifi)
                    .negativeText(R.string.continue_no_wifi)
                    .neutralText(R.string.goto_settings)
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog,
                                            @NonNull DialogAction dialogAction) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog,
                                            @NonNull DialogAction dialogAction) {
                            Intent musicIntent = new Intent(DetailActivity.this, MusicActivity.class);
                            musicIntent.putExtra("article", article);
                            startActivity(musicIntent);
                        }
                    })
                    .show();
            return;
        }
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
                if (MyApplication.blurryBg != null && !MyApplication.blurryBg.isRecycled()) {
                    MyApplication.blurryBg.recycle();
                }
                MyApplication.blurryBg = ((BitmapDrawable) activityDetailImgBackground
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
                while (activity_detail_mask.getAlpha() > 0.40f) {
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
            }
        }).start();
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