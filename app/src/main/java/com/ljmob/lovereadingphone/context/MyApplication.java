package com.ljmob.lovereadingphone.context;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.entity.User;
import com.londonx.lutil.Lutil;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by london on 15/10/21.
 * Application context
 */
public class MyApplication extends Application {
    public static User currentUser;
    public static Bitmap blurryBg;

    @Override
    public void onCreate() {
        super.onCreate();
        Lutil.init(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("default.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
        //init ImageView
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this);
        DisplayImageOptions.Builder imageOptions = new DisplayImageOptions.Builder();
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 2;

        imageOptions.bitmapConfig(Bitmap.Config.RGB_565)
                .decodingOptions(options)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(0);

        builder.diskCacheFileCount(1000)
                .threadPoolSize(6)
                .memoryCache(new WeakMemoryCache())
                .defaultDisplayImageOptions(imageOptions.build());
        ImageLoader.getInstance().init(builder.build());

        //TODO set currentUser
        currentUser = new User();
        currentUser.role = User.Role.student;
    }
}
