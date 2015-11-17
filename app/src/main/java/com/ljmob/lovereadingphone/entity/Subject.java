package com.ljmob.lovereadingphone.entity;

import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/10/30.
 * 科目
 */
public class Subject extends LEntity {
    public String name;
    public ImageUrl img_url;
    public List<Grade> grades;

    public class ImageUrl extends LEntity {
        public Image img_url;
    }
}
