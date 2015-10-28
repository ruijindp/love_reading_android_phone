package com.ljmob.lovereadingphone.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ljmob.lovereadingphone.DetailActivity;
import com.ljmob.lovereadingphone.R;
import com.ljmob.lovereadingphone.entity.Article;
import com.ljmob.lovereadingphone.entity.ArticleShelf;
import com.ljmob.lovereadingphone.util.SimpleImageLoader;
import com.londonx.lutil.adapter.LAdapter;
import com.londonx.lutil.entity.LEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by london on 15/10/22.
 * 首页书架
 */
public class IndexAdapter extends LAdapter {
    Activity activity;

    public IndexAdapter(Activity activity, List<? extends LEntity> lEntities) {
        super(lEntities);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_index, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.setArticleShelf((ArticleShelf) getItem(position));
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_indexx.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    class ViewHolder {
        @Bind(R.id.item_main_imgCover0)
        ImageView itemMainImgCover0;
        @Bind(R.id.item_main_tvReadCount0)
        TextView itemMainTvReadCount0;
        @Bind(R.id.item_main_tvTitle0)
        TextView itemMainTvTitle0;
        @Bind(R.id.item_main_tvAuthor0)
        TextView itemMainTvAuthor0;
        @Bind(R.id.item_main_ln0)
        LinearLayout itemMainLn0;
        @Bind(R.id.item_main_imgCover1)
        ImageView itemMainImgCover1;
        @Bind(R.id.item_main_tvReadCount1)
        TextView itemMainTvReadCount1;
        @Bind(R.id.item_main_tvTitle1)
        TextView itemMainTvTitle1;
        @Bind(R.id.item_main_tvAuthor1)
        TextView itemMainTvAuthor1;
        @Bind(R.id.item_main_ln1)
        LinearLayout itemMainLn1;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setArticleShelf(ArticleShelf shelf) {
            SimpleImageLoader.displayImage(shelf.article[0].cover_img, itemMainImgCover0);
            itemMainTvTitle0.setText(shelf.article[0].title);
            itemMainTvAuthor0.setText(shelf.article[0].author);
            itemMainTvReadCount0.setText(String.format("%d", shelf.article[0].read_count));

            ArticleClickListener listener0 = new ArticleClickListener(shelf.article[0], 0);
            itemMainLn0.setOnClickListener(listener0);

            if (shelf.article[1] != null) {
                SimpleImageLoader.displayImage(shelf.article[1].cover_img, itemMainImgCover1);
                itemMainTvTitle1.setText(shelf.article[1].title);
                itemMainTvAuthor1.setText(shelf.article[1].author);
                itemMainTvReadCount1.setText(String.format("%d", shelf.article[1].read_count));

                ArticleClickListener listener1 = new ArticleClickListener(shelf.article[1], 1);
                itemMainLn1.setOnClickListener(listener1);

                if (itemMainLn1.getVisibility() != View.VISIBLE) {
                    itemMainLn1.setVisibility(View.VISIBLE);
                }
            } else {
                if (itemMainLn1.getVisibility() != View.INVISIBLE) {
                    itemMainLn1.setVisibility(View.INVISIBLE);
                }
            }
        }

        private class ArticleClickListener implements View.OnClickListener {
            Article article;
            int index;

            public ArticleClickListener(Article article, int index) {
                this.article = article;
                this.index = index;
            }

            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(v.getContext(), DetailActivity.class);
                detailIntent.putExtra("article", article);
                View view;
                switch (index) {
                    case 0:
                        view = itemMainImgCover0;
                        break;
                    case 1:
                        view = itemMainImgCover1;
                        break;
                    default:
                        view = itemMainImgCover0;
                        break;
                }
//                ActivityTool.start(activity, detailIntent, view);
                activity.startActivity(detailIntent);
            }
        }
    }
}
