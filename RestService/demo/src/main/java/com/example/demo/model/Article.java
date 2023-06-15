package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Article {

    @NotNull
    private Integer id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private Integer price;

    @NotNull
    private Boolean sold;

    @NotNull
    private Boolean available;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public Article(){};

    public Article(int id, String firstName, int age, boolean sold, boolean available, String fileName) {
        this.id = id;
        this.name = firstName;
        this.price = age;
        this.sold = sold;
        this.available = available;
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isSold() { return sold; }

    public void setSold(boolean sold) { this.sold = sold; }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
