package com.ljmob.quicksharesdk;

import android.content.pm.PackageManager;

import com.ljmob.quicksharesdk.entity.Shareable;
import com.londonx.lutil.Lutil;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by london on 15/7/29.
 * 分享
 */
public class ShareTool implements
        LRequestTool.OnResponseListener,
        LRequestTool.OnDownloadListener, PlatformActionListener {
    private Platform platform;
    private LRequestTool lRequestTool;
    private Shareable shareable;

    public ShareTool(Platform platform, Shareable shareable) {
        this.platform = platform;
        this.shareable = shareable;
        lRequestTool = new LRequestTool(this);
        lRequestTool.setOnDownloadListener(this);
    }

    public void share() {
        if (platform.getName().equals(Wechat.NAME) ||
                platform.getName().equals(WechatMoments.NAME)) {
            try {
                Lutil.context.getPackageManager().getPackageInfo("com.tencent.mm", 0);
            } catch (PackageManager.NameNotFoundException ignore) {
                ToastUtil.show(R.string.quick_share_no_wechat);
                return;
            }
        }
        platform.setPlatformActionListener(this);
        if (platform instanceof SinaWeibo) {
            if (platform.isAuthValid()) {
                ToastUtil.show(R.string.quick_share_ok);
            }
        } else {
            ToastUtil.show(R.string.quick_share_prepare);
        }
        if (shareable.imgFullUrl == null || shareable.imgFullUrl.length() == 0) {
            onDownloaded(new LResponse());
        } else {
            lRequestTool.download(shareable.imgFullUrl, 0);
        }
    }

    @Override
    public void onResponse(LResponse response) {
    }

    @Override
    public void onStartDownload(LResponse response) {
    }

    @Override
    public void onDownloading(float progress) {
    }

    @Override
    public void onDownloaded(LResponse response) {
        Platform.ShareParams shareParams = new Platform.ShareParams();
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        shareParams.setTitle(shareable.title);
        shareParams.setText(shareable.content);
        if (response.downloadFile != null && response.downloadFile.length() > 0) {
            shareParams.setImagePath(response.downloadFile.getAbsolutePath());
        }
        shareParams.setUrl(shareable.url);
        shareParams.setTitleUrl(shareable.url);
        platform.share(shareParams);
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        ToastUtil.show(R.string.quick_share_ok);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        ToastUtil.show(R.string.quick_share_failed);
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        ToastUtil.show(R.string.quick_share_canceled);
    }
}
