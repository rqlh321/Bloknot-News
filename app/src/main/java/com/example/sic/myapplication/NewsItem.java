package com.example.sic.myapplication;

/**
 * Created by sic on 09.09.2016.
 */
public class NewsItem {
    String title;
    String url;

    public NewsItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}

