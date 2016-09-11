package com.example.sic.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

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

        progressBar= (ProgressBar) findViewById(R.id.loading);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.news_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleViewListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                new ParsThread(adapter,progressBar).execute(currentPage);
                currentPage++;
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        if (savedInstanceState != null) {
            Bundle bundle = savedInstanceState.getBundle(SAVED_ITEMS);
            currentPage = bundle.getInt(CURRENT_PAGE);
            ArrayList<NewsItem> news = bundle.getParcelableArrayList(NEWS);
            adapter.addAll(news);
        } else {
            new ParsThread(adapter,progressBar).execute(1);
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

}