package com.happysong.android.entity;

import com.londonx.lutil.entity.LEntity;

/**
 * Created by london on 15/11/5.
 * 班级
 */
public class TeamClass extends LEntity {

    /**
     * name : 1班
     * grade : {"id":1,"name":"一年级"}
     */

    public String name;
    public Grade grade;
    public School school;
}
