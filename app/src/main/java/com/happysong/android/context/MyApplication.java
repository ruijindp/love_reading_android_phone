package com.happysong.android.context;

import android.Manifest;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.util.Base64;

import com.google.gson.Gson;
import com.happysong.android.R;
import com.happysong.android.entity.User;
import com.happysong.android.util.PermissionUtil;
import com.londonx.lutil.Lutil;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import cn.sharesdk.framework.ShareSDK;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by london on 15/10/21.
 * Application context
 */
public class MyApplication extends Application {
    public static User currentUser;
    public static Bitmap blurryBg;
    public static String blurryName = "none";

    @Override
    public void onCreate() {
        super.onCreate();
        Lutil.init(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                        .setDefaultFontPath("fonts/default-Regular.ttf")
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
                .showImageOnLoading(ContextCompat.getDrawable(this, R.mipmap.bg_admin))
                .showImageOnFail(R.mipmap.bg_admin)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(0);

        builder.diskCacheFileCount(1000)
                .threadPoolSize(10)
                .memoryCache(new WeakMemoryCache())
                .defaultDisplayImageOptions(imageOptions.build());
        ImageLoader.getInstance().init(builder.build());

        String UB64 = Lutil.preferences.getString("UB64", "");
        if (UB64.length() != 0) {
            String userString = new String(Base64.decode(UB64, Base64.DEFAULT));
            if (userString.startsWith("{") && userString.endsWith("}")) {
                currentUser = new Gson()
                        .fromJson(userString, User.class);
            }
            if (currentUser != null) {
                MobclickAgent.onProfileSignIn(String.format("%d_%s", currentUser.id, currentUser.role));
            }
        }

        ShareSDK.initSDK(this);

        PermissionUtil.init(this, new String[]{Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
}
