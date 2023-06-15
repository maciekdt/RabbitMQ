package com.example.demo.repo;

import com.example.demo.exceptions.ArticleExistEx;
import com.example.demo.exceptions.ArticleNotFoundEx;
import com.example.demo.model.Article;

import java.util.List;

public interface ArticleRepository {
    List<Article> getAllArticles();
    Article getArticle(int id) throws ArticleNotFoundEx;
    Article sellArticle(int id, boolean sold) throws ArticleNotFoundEx;
    Article deleteArticle(int id) throws ArticleNotFoundEx;
    Article addArticle(int id, String name, int price, boolean sold, boolean available) throws ArticleExistEx;
    int countArticles();
}
