package com.example.sic.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class RecycleViewListAdapter extends RecyclerView.Adapter<RecycleViewListAdapter.ViewHolder> {
    private ArrayList<NewsItem> list = new ArrayList<>();
    private Context context;

    public RecycleViewListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.folderText.setText(list.get(position).getTitle());
        Glide.with(context)
                .load(list.get(position).getUrl())
                .animate(R.anim.pop_enter)
                .bitmapTransform(new RoundedCornersTransformation(context, 10, 10))
                .into(holder.folderCover);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAll(ArrayList<NewsItem> newList) {
        list.addAll(newList);
        notifyDataSetChanged();
    }

    public ArrayList<NewsItem> getList() {
        return list;
    }

    public boolean contains(String title) {
        for (NewsItem item : list) {
            if (item.getTitle().equals(title)) {
                return true;
            }
        }
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        TextView folderText;
        ImageView folderCover;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            folderText = (TextView) view.findViewById(R.id.folder_text);
            folderCover = (ImageView) view.findViewById(R.id.folder_preview_image);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

}