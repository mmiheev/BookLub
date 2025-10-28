package com.zeon.book.models;

public class Quote {
    private int id;
    private int num;
    private String quote;
    private String author;

    public Quote(int id, int num, String quote, String author) {
        this.id = id;
        this.num = num;
        this.quote = quote;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
