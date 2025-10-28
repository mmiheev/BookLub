package com.zeon.book.tools.constant;

public enum IntentName {
    ID("ID"),
    BOOK_NAME("BOOK_NAME"),
    AUTHOR("AUTHOR"),
    DESCRIPTION("DESCRIPTION"),
    GENRE("GENRE"),
    SERIES("SERIES"),
    NOTE("NOTE"),
    FAVORITE("FAVORITE"),
    FINISHED("FINISHED"),
    TITLE_NAME("TITLE_NAME"),
    IS_YEAR("IS_YEAR"),
    YEAR("YEAR"),
    UPDATE("UPDATE");


    private String name;

    IntentName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
