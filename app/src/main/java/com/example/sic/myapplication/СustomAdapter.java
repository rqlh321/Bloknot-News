package com.example.sic.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sic on 14.02.2016.
 */
public class СustomAdapter extends ArrayAdapter<String> {
    private ArrayList<Bitmap> picturesBM;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater =LayoutInflater.from(getContext());
        View customView=mInflater.inflate(R.layout.list_item, parent, false);

        String singleTitleItem=getItem(position);

        TextView mTextView= (TextView) customView.findViewById(R.id.title);
        ImageView mImageView= (ImageView) customView.findViewById(R.id.pic);
        if(picturesBM.size()>position)
            mImageView.setImageBitmap(picturesBM.get(position));
        mTextView.setText(singleTitleItem);

        return customView;
    }

    public СustomAdapter(Context context, ArrayList<String> titles, ArrayList<Bitmap> picturesBM) {
        super(context,R.layout.list_item, titles);
        this.picturesBM=picturesBM;
    }
}
