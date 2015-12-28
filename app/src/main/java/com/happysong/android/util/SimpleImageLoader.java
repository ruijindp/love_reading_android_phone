package com.happysong.android.util;

import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.happysong.android.net.NetConstant;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by london on 15/10/22.
 * 简单ImageLoader
 */
public class SimpleImageLoader {
    protected static final ImageLoader LOADER = ImageLoader.getInstance();

    public static void displayImage(String picApiUrl, ImageView imageView) {
        if (picApiUrl.startsWith("/")) {
            LOADER.displayImage(NetConstant.ROOT_URL + picApiUrl, imageView);
        } else {
            try {
                @DrawableRes int mipmapId = Integer.parseInt(picApiUrl);
                imageView.setImageResource(mipmapId);
            } catch (Exception ignore) {
                LOADER.displayImage(picApiUrl, imageView);
            }
        }
    }
}
