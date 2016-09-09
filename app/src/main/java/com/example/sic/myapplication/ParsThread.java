package com.example.sic.myapplication;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sic on 09.09.2016.
 */
class ParsThread extends AsyncTask<Integer, Void, ArrayList<NewsItem>> {
    RecycleViewListAdapter adapter;

    public ParsThread(RecycleViewListAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected ArrayList<NewsItem> doInBackground(Integer... params) {
        ArrayList<NewsItem> parsNewsList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("http://bloknot-taganrog.ru/?PAGEN_1=" + params[0]).get();
            Elements contentPictures = doc.select(".preview_picture");
            for (Element content : contentPictures) {
                String title = content.attributes().get("title");
                String url = content.attributes().get("src");
                if (url.contains("//s0.")) {//локальная
                    url = url.replace("//s0.", "http://");
                    if (!adapter.contains(title)) {//уникальная
                        NewsItem item = new NewsItem(title, url);
                        parsNewsList.add(item);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parsNewsList;
    }

    @Override
    protected void onPostExecute(ArrayList<NewsItem> result) {
        adapter.addAll(result);
    }
}
