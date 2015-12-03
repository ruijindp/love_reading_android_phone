package com.ljmob.lovereadingphone.entity;

import com.londonx.lutil.entity.LEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by london on 15/10/30.
 * 科目
 */
public class Subject extends LEntity {
    public String name;
    @Deprecated
    public ImageUrl img_url;
    public List<Edition> editions;
    public List<Grade> grades;
    public List<Category> cate_items;
    public Type type;

    /**
     * @deprecated no ImageUrl anymore
     */
    @Deprecated
    public class ImageUrl extends LEntity {
        public Image img_url;
    }

    public enum Type implements Serializable {
        subject, category
    }
}
