package com.ljmob.lovereadingphone.entity;

import com.londonx.lutil.entity.LEntity;

/**
 * Created by london on 15/10/21.
 * 用户
 */
public class User extends LEntity {
    public String token = "none";
    public Role role = Role.student;

    public enum Role {
        student, teacher
    }
}
