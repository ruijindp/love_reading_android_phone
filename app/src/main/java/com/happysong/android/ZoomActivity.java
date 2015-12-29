package com.happysong.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.happysong.android.entity.Image;
import com.happysong.android.util.SimpleImageLoader;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by london on 15/11/11.
 * 图片放大
 */
public class ZoomActivity extends AppCompatActivity implements
        ImageViewTouch.OnImageViewTouchSingleTapListener {
    @Bind(R.id.activity_zoom_img)
    ImageViewTouch activityZoomImg;

    String qiniuUrl;
    Image image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image = (Image) getIntent().getSerializableExtra("image");
        qiniuUrl = getIntent().getStringExtra("qiniuUrl");
        if (image == null && qiniuUrl == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_zoom);
        ButterKnife.bind(this);
        activityZoomImg.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        SimpleImageLoader.displayImage(image == null ? qiniuUrl : image.url, activityZoomImg);
        activityZoomImg.setSingleTapListener(this);
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
    public void onSingleTapConfirmed() {
        finish();
    }
}
