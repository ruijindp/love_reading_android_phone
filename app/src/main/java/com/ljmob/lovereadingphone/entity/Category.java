package com.ljmob.lovereadingphone.entity;

import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/11/2.
 * 分类
 */
public class Category extends LEntity {
    public Subject subject;
    public List<Grade> grades;
}
