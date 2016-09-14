package com.example.sic.myapplication.Activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sic.myapplication.EndlessRecyclerOnScrollListener;
import com.example.sic.myapplication.NewsItem;
import com.example.sic.myapplication.R;
import com.example.sic.myapplication.RecycleViewListAdapter;
import com.example.sic.myapplication.ServiceManager;

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
    private ServiceManager serviceManager;
    private CollapsingToolbarLayout mainActivityView;
    private static int currentPage = 1;
    private static RecycleViewListAdapter adapter;
    private static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.loading);
        mainActivityView = (CollapsingToolbarLayout) findViewById(R.id.main_activity_view);
        serviceManager = new ServiceManager(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.news_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleViewListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                if (!serviceManager.isNetworkAvailable()) {
                    connectionLost();
                }
                getNews();
                currentPage++;
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
            getNews();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!serviceManager.isNetworkAvailable()) {
            connectionLost();
        }
    }

    private void connectionLost() {
        Snackbar snackbar = Snackbar.make(mainActivityView, "No internet connection!", Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public static void getNews() {
        progressBar.setVisibility(View.VISIBLE);
        Observable.just(currentPage)
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