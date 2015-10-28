package com.ljmob.lovereadingphone.adapter;

import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.entity.Music;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by london on 15/10/23.
 * 音乐列表
 */
public class MusicAdapter extends LAdapter {
    private int selectedIndex = -1;
    private int accentColor;
    private int textColor;
    private ViewHolder selectedHolder;

    public MusicAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (accentColor == 0) {
            accentColor = ContextCompat.getColor(parent.getContext(), R.color.colorAccent);
            textColor = ContextCompat.getColor(parent.getContext(), R.color.textPrimary);
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_music, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.setMusic((Music) lEntities.get(position));
        holder.setSelected(position == selectedIndex);
        return convertView;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_music.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    public class ViewHolder {
        @Bind(R.id.item_music_rbSelect)
        AppCompatRadioButton itemMusicRbSelect;
        @Bind(R.id.item_music_tvMusic)
        TextView itemMusicTvMusic;
        @Bind(R.id.item_music_imgGif)
        GifImageView itemMusicImgGif;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setMusic(Music music) {
            itemMusicTvMusic.setText(music.name);
        }

        public void setSelected(boolean isSelected) {
            if (selectedHolder != null && isSelected) {
                selectedHolder.setSelected(false);
            }
            if (isSelected) {
                selectedHolder = this;
                itemMusicRbSelect.setSupportButtonTintList(ColorStateList.valueOf(accentColor));
                itemMusicRbSelect.setChecked(true);
                itemMusicTvMusic.setTextColor(accentColor);
            } else {
                itemMusicRbSelect.setSupportButtonTintList(ColorStateList.valueOf(textColor));
                itemMusicRbSelect.setChecked(false);
                itemMusicTvMusic.setTextColor(textColor);
            }
        }
    }
}
