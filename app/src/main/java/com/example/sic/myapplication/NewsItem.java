package com.example.sic.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sic on 09.09.2016.
 */
public class NewsItem implements Parcelable {
    String title;
    String url;

    public NewsItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    protected NewsItem(Parcel in) {
        title = in.readString();
        url = in.readString();
    }

    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(url);
    }
}