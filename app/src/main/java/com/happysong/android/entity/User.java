package com.happysong.android.entity;

import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/10/21.
 * 用户
 */
public class User extends LEntity {

    /**
     * account=a&password=123456
     * token : Z_xvfXszjVu_9BgfzSde
     * name : 测试1
     * avatar : {"avatar":{"url":"/user_avatar/Z_xvfXszjVu_9BgfzSde.png","normal":{"url":"/user_avatar/Z_xvfXszjVu_9BgfzSde.png"},"small":{"url":"/user_avatar/Z_xvfXszjVu_9BgfzSde.png"},"large":{"url":"/user_avatar/Z_xvfXszjVu_9BgfzSde.png"},"big":{"url":"/user_avatar/Z_xvfXszjVu_9BgfzSde.png"}}}
     * role : teacher
     */

    public String token;
    public String name;
    public Role role = Role.student;
    public String qiniu_url;
    public Avatar avatar;
    public Sex sex;
    public List<TeamClass> team_classes;

    public class Avatar extends LEntity {
        public Image avatar;
    }

    public enum Role {
        teacher, student
    }

    public enum Sex {
        boy, girl
    }
}
