package com.anhnguyen.homeshopping.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Category;

import java.util.ArrayList;

public class CategoryGridAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Category> cateList = new ArrayList<>();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CategoryGridAdapter(Activity activity, ArrayList<Category> cateList) {
        this.activity = activity;
        this.cateList = cateList;
    }

    @Override
    public int getCount() {
        return cateList.size();
    }

    @Override
    public Object getItem(int location) {
        return cateList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.category_cell, null);
        }

        if (imageLoader == null) {
            imageLoader = AppController.getInstance().getImageLoader();
        }

        // getting product data for the row
        Category cate = cateList.get(position);

        // Image
        NetworkImageView image = (NetworkImageView) convertView.findViewById(R.id.img_category);
        if(cate.getImage() != null) {
            image.setImageUrl(cate.getImage(), AppController.getInstance().getImageLoader());
        }

        // Name
        TextView txt_cate_name = (TextView) convertView.findViewById(R.id.txt_cate_name);
        txt_cate_name.setText(cate.getName());

        return convertView;
    }

}
