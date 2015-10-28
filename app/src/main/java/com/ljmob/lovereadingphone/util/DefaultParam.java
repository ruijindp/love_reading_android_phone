package com.ljmob.lovereadingphone.util;

import com.ljmob.lovereadingphone.context.MyApplication;

import java.util.HashMap;

/**
 * Created by london on 15/10/21.
 * 默认包含用户信息的{@link java.util.HashMap}
 */
public class DefaultParam extends HashMap<String, Object> {
    public DefaultParam() {
        if (MyApplication.currentUser != null) {
            put("token", MyApplication.currentUser.token);
        }
    }
}
