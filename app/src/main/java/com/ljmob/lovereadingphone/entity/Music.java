package com.ljmob.lovereadingphone.entity;

import com.londonx.lutil.entity.LEntity;

/**
 * Created by london on 15/10/23.
 * 音乐
 */
public class Music extends LEntity{
    public String name;
    public String file_url;

    public class Type extends LEntity {
        public String name;
    }
}
