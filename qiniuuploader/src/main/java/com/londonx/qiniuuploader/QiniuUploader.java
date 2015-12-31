package com.londonx.qiniuuploader;

import android.support.annotation.NonNull;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by london on 15/12/29.
 * Qiniu http://www.qiniu.com
 */
public class QiniuUploader {
    private static long TOKEN_TIME;
    private static String TOKEN;

    private static UploadManager uploadManager;

    private UploadListener uploadListener;


    public QiniuUploader() {
        if (uploadManager == null) {
            uploadManager = new UploadManager();
        }
    }

    public static void setToken(String TOKEN) {
        QiniuUploader.TOKEN = TOKEN;
        TOKEN_TIME = System.currentTimeMillis();
    }

    public void upload(final File... files) {
        if (!isTokenValid()) {//1 hour token
            throw new IllegalStateException("TOKEN is invalid recall setToken(String) first");
        }
        if (TOKEN == null) {
            throw new NullPointerException("TOKEN is NULL, call setToken(String)");
        }
        if (files.length == 0) {
            if (uploadListener != null) {
                uploadListener.onUploaded("", -1);
            }
            return;
        }
        for (int i = 0; i < files.length; i++) {
            final int index = i;//File index in array
            File f = files[i];
            if (!f.exists()) {
                Log.e("L", "upload: FileNotFound",
                        new FileNotFoundException(f.getAbsolutePath() + " NotFound !!! Cannot upload this file"));
                continue;
            }
            if (f.length() == 0) {
                Log.w("L", "upload: File length is 0", null);
            }
            UploadOptions options = new UploadOptions(null, null, false, new UpProgressHandler() {
                @Override
                public void progress(String key, double percent) {
                    if (uploadListener != null) {
                        uploadListener.onUploading(key, (float) percent);
                    }
                }
            }, null);
            uploadManager.put(f, getFileMD5(f), TOKEN,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject res) {
                            if (uploadListener == null) {
                                return;
                            }
                            if (!info.isOK()) {
                                if (info.isNetworkBroken() || info.isServerError()) {
                                    uploadListener.onUploadingErr(UploadListener.Error.network);
                                }
                                return;
                            }
                            uploadListener.onUploaded(key, index);
                        }
                    }, options);
        }
    }

    public void setUploadListener(UploadListener uploadListener) {
        this.uploadListener = uploadListener;
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public static boolean isTokenValid() {
        return System.currentTimeMillis() - TOKEN_TIME < 3600000;
    }

    public interface UploadListener {
        enum Error {
            network, internal
        }

        /**
         * called when upload is finished
         *
         * @param fileKey the file key on Qiniu
         *                example: http://7xpl2d.com1.z0.glb.clouddn.com/<fileKey>
         * @param index   the index of uploaded file in files array;
         */
        void onUploaded(@NonNull String fileKey, int index);

        void onUploading(@NonNull String fileKey, float progress);

        /**
         * called when uploading failed
         *
         * @param error network or internal error
         */
        void onUploadingErr(@NonNull Error error);
    }
}
