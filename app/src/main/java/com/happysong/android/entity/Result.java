package com.happysong.android.entity;

import com.londonx.lutil.entity.LEntity;

import java.util.List;

/**
 * Created by london on 15/10/23.
 * 朗读结果
 */
public class Result extends LEntity {
    public Article article;
    public boolean check;
    public boolean is_vote;
    public long created_at;
    public String feeling;
    public String file_url;
    public Music music;
    public List<Score> score;
    public User user;
    public int votes;
}
