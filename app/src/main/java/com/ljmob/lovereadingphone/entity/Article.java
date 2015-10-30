package com.ljmob.lovereadingphone.entity;

import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/10/22.
 * 文章
 */
public class Article extends LEntity {
    public String title;
    public String author;
    public CoverImgEntity cover_img;
    public Grade grade;
    public ArticleTypeEntity article_type;
    public Subject subject;
    public Unit unit;
    public List<Section> sections;
    public int count;

    public static class CoverImgEntity extends LEntity {
        public Image cover_img;
    }

    public static class ArticleTypeEntity extends LEntity {
        public String name;
    }
}
