package com.example.demo.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ArticleUnavailableEx extends RuntimeException {
    public ArticleUnavailableEx(int id){super("Article (ID=" + id +") is unavailable");}
}
