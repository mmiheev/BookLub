package com.zeon.book.tools.constant;

public enum Key {
    SERIES("series"),
    AUTHOR("author"),
    GENRE("genre")
    ;

    private String name;

    Key(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
