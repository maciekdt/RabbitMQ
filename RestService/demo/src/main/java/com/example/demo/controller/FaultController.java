package com.example.demo.controller;

import com.example.demo.exceptions.ArticleExistEx;
import com.example.demo.exceptions.ArticleNotFoundEx;
import com.example.demo.exceptions.ArticleUnavailableEx;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class FaultController {


    @ResponseBody
    @ExceptionHandler(ArticleNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    ResponseEntity<?> PNFEHandler(ArticleNotFoundEx e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header(HttpHeaders.CONTENT_TYPE,
                        MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withStatus(HttpStatus.NOT_FOUND)
                        .withTitle(HttpStatus.NOT_FOUND.name())
                        .withDetail(e.getMessage()));
    }

    @ResponseBody
    @ExceptionHandler(ArticleExistEx.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    ResponseEntity<?> PEEHandler(ArticleExistEx e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .header(HttpHeaders.CONTENT_TYPE,
                        MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withStatus(HttpStatus.CONFLICT)
                        .withTitle(HttpStatus.CONFLICT.name())
                        .withDetail(e.getMessage()));
    }


    @ResponseBody
    @ExceptionHandler(ArticleUnavailableEx.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    ResponseEntity<?> AUEHandler(ArticleUnavailableEx e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .header(HttpHeaders.CONTENT_TYPE,
                        MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withStatus(HttpStatus.CONFLICT)
                        .withTitle(HttpStatus.CONFLICT.name())
                        .withDetail(e.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        // Build an error response with the validation errors
        StringBuilder errorMessage = new StringBuilder("Validation failed for JSON object. Errors: ");
        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("  ");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE,
                        MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withStatus(HttpStatus.BAD_REQUEST)
                        .withTitle(HttpStatus.BAD_REQUEST.name())
                        .withDetail(errorMessage.toString()));
    }



}
