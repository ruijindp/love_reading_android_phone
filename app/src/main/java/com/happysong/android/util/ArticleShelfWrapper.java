package com.happysong.android.util;

import com.happysong.android.entity.Article;
import com.happysong.android.entity.ArticleShelf;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by london on 15/10/30.
 * Article Shelf包装器
 */
public class ArticleShelfWrapper {
    public static List<ArticleShelf> wrap(List<Article> articles) {
        List<ArticleShelf> shelves = new ArrayList<>();
        for (int i = 0; i < articles.size(); i += 2) {
            ArticleShelf shelf = new ArticleShelf();
            shelf.articles[0] = articles.get(i);
            if (i + 1 < articles.size()) {
                shelf.articles[1] = articles.get(i + 1);
            } else {
                shelf.articles[1] = null;
            }
            shelves.add(shelf);
        }
        return shelves;
    }
}
