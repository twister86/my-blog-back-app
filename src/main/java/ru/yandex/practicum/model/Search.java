package ru.yandex.practicum.model;

import java.sql.Array;

public class Search {
    private String searchString;

    private String tagsString;

    public Search(String searchString, String tagsString) {
        this.searchString = searchString;
        this.tagsString = tagsString;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getTagsString() {
        return tagsString;
    }

    public void setTagsString(String tagsString) {
        this.tagsString = tagsString;
    }
}
