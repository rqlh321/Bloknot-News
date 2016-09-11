package com.example.sic.myapplication.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sic.myapplication.EndlessRecyclerOnScrollListener;
import com.example.sic.myapplication.NewsItem;
import com.example.sic.myapplication.R;
import com.example.sic.myapplication.RecycleViewListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {
    private final String SAVED_ITEMS = "saved";
    private final String NEWS = "news";
    private final String CURRENT_PAGE = "current_page";
    int currentPage = 1;
    private RecycleViewListAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.loading);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.news_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleViewListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                getNews(currentPage);
                currentPage++;
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        if (savedInstanceState != null) {
            progressBar.setVisibility(View.GONE);
            Bundle bundle = savedInstanceState.getBundle(SAVED_ITEMS);
            currentPage = bundle.getInt(CURRENT_PAGE);
            ArrayList<NewsItem> news = bundle.getParcelableArrayList(NEWS);
            adapter.addAll(news);
        } else {
            currentPage = 1;
            getNews(currentPage);
            currentPage++;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(NEWS, adapter.getList());
        bundle.putInt(CURRENT_PAGE, currentPage);
        outState.putBundle(SAVED_ITEMS, bundle);
        super.onSaveInstanceState(outState);
    }

    private void getNews(int page) {
        Observable.just(page)
                .map(new Func1<Integer, ArrayList<NewsItem>>() {
                    @Override
                    public ArrayList<NewsItem> call(Integer integer) {
                        ArrayList<NewsItem> parsNewsList = new ArrayList<>();
                        try {
                            Document doc = Jsoup.connect("http://bloknot-taganrog.ru/?PAGEN_1=" + integer).get();
                            Elements contentPictures = doc.select(".preview_picture");
                            for (Element content : contentPictures) {
                                String title = content.attributes().get("title");
                                String url = content.attributes().get("src");
                                if (url.contains("//s0.")) {//локальная новость
                                    url = url.replace("//s0.", "http://");
                                    if (!adapter.contains(title)) {//должна быть уникальной
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
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<NewsItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<NewsItem> newsItems) {
                        adapter.addAll(newsItems);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}