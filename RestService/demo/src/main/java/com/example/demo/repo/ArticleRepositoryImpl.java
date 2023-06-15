package com.example.demo.repo;

import com.example.demo.exceptions.ArticleExistEx;
import com.example.demo.exceptions.ArticleNotFoundEx;
import com.example.demo.exceptions.ArticleUnavailableEx;
import com.example.demo.model.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleRepositoryImpl implements ArticleRepository {
    private List<Article> personList;
    public ArticleRepositoryImpl() {
        personList = new ArrayList<>();
        personList.add(new Article(1, "Pen" , 10, false, true, "img_1.jpg"));
        personList.add(new Article(2, "Banana", 15, false, false, "img_2.jpg"));
        personList.add(new Article(3, "It", 11, false, true, "img_1.jpg"));
    }
    public List<Article> getAllArticles() {
        return personList;
    }

    public Article getArticle(int id) throws ArticleNotFoundEx {
        for (Article thePerson : personList) {
            if (thePerson.getId() == id) {
                return thePerson;
            }
        }
        throw new ArticleNotFoundEx(id);
    }
    public Article addArticle(int id, String name, int price, boolean sold, boolean available) throws ArticleExistEx {
        for (Article thePerson : personList) {
            if (thePerson.getId() == id) {
                throw new ArticleExistEx(id);
            }
        }
        Article person = new Article(id, name, price, sold, available, null);
        personList.add(person);
        return person;
    }

    public Article deleteArticle(int id) throws ArticleNotFoundEx {
        for (Article thePerson : personList) {
            if (thePerson.getId() == id) {
                personList.remove(thePerson);
                return thePerson;
            }
        }
        throw new ArticleNotFoundEx(id);
    }
    public Article sellArticle(int id, boolean sold) throws ArticleNotFoundEx {
        for (Article thePerson : personList) {
            if (thePerson.getId() == id) {
                if(!thePerson.isAvailable())
                    throw new ArticleUnavailableEx(id);
                thePerson.setSold(sold);
                return thePerson;
            }
        }
        throw new ArticleNotFoundEx(id);
    }
    public int countArticles() {
        return personList.size();
    }
}
