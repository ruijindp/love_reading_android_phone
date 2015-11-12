package com.ljmob.lovereadingphone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ljmob.lovereadingphone.entity.Image;
import com.ljmob.lovereadingphone.util.SimpleImageLoader;

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

    Image image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image = (Image) getIntent().getSerializableExtra("image");
        if (image == null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_zoom);
        ButterKnife.bind(this);
        activityZoomImg.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        SimpleImageLoader.displayImage(image.url, activityZoomImg);
        activityZoomImg.setSingleTapListener(this);
    }

    @Override
    public void onSingleTapConfirmed() {
        finish();
    }
}
