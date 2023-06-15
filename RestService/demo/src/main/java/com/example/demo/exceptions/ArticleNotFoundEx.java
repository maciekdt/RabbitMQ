package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ArticleNotFoundEx extends RuntimeException {
    public ArticleNotFoundEx(int id) {
        super("Article (ID=" + id + ") does not exist");
    }
}
