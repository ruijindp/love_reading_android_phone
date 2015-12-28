package com.happysong.android.entity;

import com.happysong.android.fragment.RecorderFragment;
import com.londonx.lutil.entity.LEntity;

/**
 * Created by london on 15/11/4.
 * 分数
 */
public class Score extends LEntity {
    public float score;
    public String comment;
    public long created_at;
    public User user;
    public RecorderFragment.Status status;

    public enum Status {
        teacher, student
    }
}
