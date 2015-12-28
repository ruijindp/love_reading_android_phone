package com.happysong.android.util;

import android.text.format.DateFormat;

import com.happysong.android.entity.Article;
import com.happysong.android.entity.Music;
import com.happysong.android.entity.User;
import com.londonx.lutil.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by london on 15/11/3.
 * 获取默认录音文件
 */
public class RecorderFileUtil {
    public static File getDefaultRecorderFile(User user, Music music, Article article) {
        File recFile = null;
        try {
            recFile = new File(FileUtil.getCacheFolder(),
                    user.name + "_" +
                            music.name + "_" +
                            article.title + "_" +
                            DateFormat.format("yyyy-MM-dd-HH-mm-ss", System.currentTimeMillis()) +
                            ".wav");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recFile;
    }
}
