package com.ljmob.lovereadingphone.util;

import android.text.format.DateFormat;

import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.entity.Music;
import com.ljmob.lovereadingphone.entity.User;
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
