package com.happysong.android.entity;

import com.londonx.lutil.entity.LEntity;

/**
 * Created by london on 15/10/30.
 * 图片
 */
public class Image extends LEntity {
    public String url;
    public ApiUrl normal;
    public ApiUrl small;
    public ApiUrl big;

    public static class ApiUrl extends LEntity {
        public String url;
    }
}
