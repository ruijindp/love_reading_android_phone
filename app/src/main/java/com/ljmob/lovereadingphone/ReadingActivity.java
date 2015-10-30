package com.ljmob.lovereadingphone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.ljmob.lovereadingphone.adapter.ReadingStatusPagerAdapter;
import com.ljmob.lovereadingphone.context.MyApplication;
import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.entity.Music;
import com.ljmob.lovereadingphone.entity.Result;
import com.ljmob.lovereadingphone.entity.Section;
import com.ljmob.lovereadingphone.net.NetConstant;
import com.londonx.lutil.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import butterknife.Bind;
import butterknife.ButterKnife;
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
    @Bind(R.id.activity_reading_imgBackground)
    ImageView activityReadingImgBackground;
    @Bind(R.id.activity_reading_mask)
    View activityReadingMask;

    @Bind(R.id.activity_reading_lnHeadRecord)
    LinearLayout activityReadingLnHeadRecord;
    @Bind(R.id.activity_reading_lnHeadResult)
    LinearLayout activityReadingLnHeadResult;
    @Bind(R.id.activity_reading_frameCount)
    FrameLayout activityReadingFrameCount;

    @Bind(R.id.activity_reading_scContent)
    ScrollView activityReadingScContent;
    @Bind(R.id.activity_reading_tvChecker)
    TextView activityReadingTvChecker;
    @Bind(R.id.activity_reading_rbRate)
    AppCompatRatingBar activityReadingRbRate;
    @Bind(R.id.activity_reading_pagerStatus)
    ViewPager activityReadingPagerStatus;

    MenuItem shareMenuItem;

    Article article;
    Music music;
    Result result;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Status currentStatus = Status.record;
    @Bind(R.id.activity_reading_lnContent)
    LinearLayout activityReadingLnContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = (Result) getIntent().getSerializableExtra("result");
        if (result != null) {
            //TODO currentStatus = Status.result;
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
            activityReadingMask.setVisibility(View.INVISIBLE);
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


    private void initData() {
        activityReadingTvAuthor.setText(article.author);
        activityReadingTvReadCount.setText(String.format("%d", article.count));
        activityReadingPagerStatus.setAdapter(
                new ReadingStatusPagerAdapter(getSupportFragmentManager()));
        for (Section s : article.sections) {
            View sectionView = getLayoutInflater()
                    .inflate(R.layout.item_section, activityReadingLnContent, false);
            SectionHolder holder = new SectionHolder(sectionView);
            holder.primarySectionTitle.setText(s.title);
            holder.primarySectionContent.setText(s.content);
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
                if (shareMenuItem != null) {
                    shareMenuItem.setVisible(false);
                }
                activityReadingPagerStatus.setCurrentItem(0);
                break;
            case notRatedResult:
                ToastUtil.show("未打分状态仅教师可见");
                activityReadingLnHeadRecord.setVisibility(View.GONE);
                activityReadingLnHeadResult.setVisibility(View.VISIBLE);
                activityReadingTvChecker.setVisibility(View.GONE);
                activityReadingRbRate.setVisibility(View.GONE);
                activityReadingPagerStatus.setCurrentItem(1);
                break;
            case ratedResult:
                activityReadingLnHeadRecord.setVisibility(View.GONE);
                activityReadingLnHeadResult.setVisibility(View.VISIBLE);
                activityReadingTvChecker.setVisibility(View.VISIBLE);
                activityReadingRbRate.setVisibility(View.VISIBLE);
                if (shareMenuItem != null) {
                    shareMenuItem.setVisible(true);
                }
                activityReadingPagerStatus.setCurrentItem(2);
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

    public enum Status {
        record, notRatedResult, ratedResult
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
