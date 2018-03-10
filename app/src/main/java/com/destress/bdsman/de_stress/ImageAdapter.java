package com.destress.bdsman.de_stress;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by vipul on 10-03-2018.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList< ArrayList<Bitmap> > mImages;
    private int mHeight, mWidth;

    public int getCount(){
        return mImages.size();
    }

    public ArrayList<Bitmap> getItem(int position) {
        return mImages.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public ImageAdapter(Context c, ArrayList< ArrayList<Bitmap> > images, int height, int width) {
        mContext = c;
        mImages = images;
        mHeight = height;
        mWidth = width;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView;
        if(convertView == null){
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1, 1, 1, 1);
        }else{
            imageView = (ImageView) convertView;
        }

        for(Bitmap image: mImages.get(position) ){
            imageView.setImageBitmap(image);
        }

        return imageView;
    }
}