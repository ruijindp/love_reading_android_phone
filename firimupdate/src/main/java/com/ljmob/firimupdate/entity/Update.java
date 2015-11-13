package com.ljmob.firimupdate.entity;

/**
 * Created by london on 15/6/19.
 * Update info
 */
public class Update {
    //{"name":"我爱朗读",
    // "version":"9",
    // "changelog":"本次更新依照梅陇中学要求，实现大量系统优化与二期项目功能上线：\n1.新增换肤功能（4套皮肤）\n2.新增分享上传功能\n3.新增范例赏析功能\n4.改进最美声音界面\n5.已赞的文章现在会直接显示已赞\n6.朗读背景去除默认音量限制\n7.调整热门文章显示逻辑",
    // "versionShort":"1.8.2",
    // "installUrl":"http://fir.im/api/v2/app/install/552a5177ebc861d9360026ed",
    // "update_url":"http://fir.im/reading"}
    public String name;
    public int version;
    public String changelog;
    public String versionShort;
    public String installUrl;
    public String update_url;
}
