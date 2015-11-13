package com.ljmob.quicksharesdk;

import com.ljmob.quicksharesdk.entity.Shareable;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;

import cn.sharesdk.framework.Platform;

/**
 * Created by london on 15/7/29.
 * 分享
 */
public class ShareTool implements
        LRequestTool.OnResponseListener,
        LRequestTool.OnDownloadListener {
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

        ToastUtil.show(R.string.quick_share_ok);
    }
}