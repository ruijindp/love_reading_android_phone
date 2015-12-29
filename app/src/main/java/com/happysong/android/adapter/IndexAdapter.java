package com.happysong.android.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happysong.android.DetailActivity;
import com.happysong.android.R;
import com.happysong.android.entity.Article;
import com.happysong.android.entity.ArticleShelf;
import com.happysong.android.util.SimpleImageLoader;
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
    private long lastStartTime;

    public IndexAdapter(List<? extends LEntity> lEntities) {
        super(lEntities);
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
        @Bind(R.id.item_index_tvUnit)
        TextView itemIndexTvUnit;
        @Bind(R.id.item_index_imgCover0)
        ImageView itemIndexImgCover0;
        @Bind(R.id.item_index_tvReadCount0)
        TextView itemIndexTvReadCount0;
        @Bind(R.id.item_index_tvTitle0)
        TextView itemIndexTvTitle0;
        @Bind(R.id.item_index_tvAuthor0)
        TextView itemIndexTvAuthor0;
        @Bind(R.id.item_index_ln0)
        LinearLayout itemIndexLn0;
        @Bind(R.id.item_index_imgCover1)
        ImageView itemIndexImgCover1;
        @Bind(R.id.item_index_tvReadCount1)
        TextView itemIndexTvReadCount1;
        @Bind(R.id.item_index_tvTitle1)
        TextView itemIndexTvTitle1;
        @Bind(R.id.item_index_tvAuthor1)
        TextView itemIndexTvAuthor1;
        @Bind(R.id.item_index_ln1)
        LinearLayout itemIndexLn1;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void setArticleShelf(ArticleShelf shelf) {
            if (itemIndexTvUnit.getVisibility() != View.GONE) {
                itemIndexTvUnit.setVisibility(View.GONE);
            }
            SimpleImageLoader
                    .displayImage(shelf.articles[0].qiniu_url == null ?
                            shelf.articles[0].cover_img.cover_img.normal.url :
                            shelf.articles[0].qiniu_url, itemIndexImgCover0);
            itemIndexTvTitle0.setText(shelf.articles[0].title);
            if (shelf.articles[0].author.length() == 0) {
                itemIndexTvAuthor0.setVisibility(View.INVISIBLE);
            } else {
                itemIndexTvAuthor0.setVisibility(View.VISIBLE);
                itemIndexTvAuthor0.setText(shelf.articles[0].author);
            }
            itemIndexTvReadCount0.setText(String.format("%d", shelf.articles[0].count));

            ArticleClickListener listener0 = new ArticleClickListener(shelf.articles[0], 0);
            itemIndexLn0.setOnClickListener(listener0);

            if (shelf.articles[1] != null) {
                SimpleImageLoader
                        .displayImage(shelf.articles[1].qiniu_url == null ?
                                shelf.articles[1].cover_img.cover_img.normal.url :
                                shelf.articles[1].qiniu_url, itemIndexImgCover1);
                itemIndexTvTitle1.setText(shelf.articles[1].title);
                if (shelf.articles[1].author.length() == 0) {
                    itemIndexTvAuthor1.setVisibility(View.INVISIBLE);
                } else {
                    itemIndexTvAuthor1.setVisibility(View.VISIBLE);
                    itemIndexTvAuthor1.setText(shelf.articles[1].author);
                }
                itemIndexTvReadCount1.setText(String.format("%d", shelf.articles[1].count));

                ArticleClickListener listener1 = new ArticleClickListener(shelf.articles[1], 1);
                itemIndexLn1.setOnClickListener(listener1);

                if (itemIndexLn1.getVisibility() != View.VISIBLE) {
                    itemIndexLn1.setVisibility(View.VISIBLE);
                }
            } else {
                if (itemIndexLn1.getVisibility() != View.INVISIBLE) {
                    itemIndexLn1.setVisibility(View.INVISIBLE);
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
                if (System.currentTimeMillis() - lastStartTime < 500) {//防止重复点击
                    return;
                }
                lastStartTime = System.currentTimeMillis();
                v.getContext().startActivity(detailIntent);
            }
        }
    }
}
