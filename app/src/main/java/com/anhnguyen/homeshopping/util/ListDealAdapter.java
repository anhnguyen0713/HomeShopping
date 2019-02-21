package com.anhnguyen.homeshopping.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Channel;
import com.anhnguyen.homeshopping.model.Deal;

import java.util.List;

public class ListDealAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Deal> dealList;
    private ViewHolder holder;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ListDealAdapter(Activity activity, List<Deal> dealList) {
        this.activity = activity;
        this.dealList = dealList;
    }

    static class ViewHolder {
        NetworkImageView img_banner;
        TextView txt_title;
        TextView txt_time;
    }

    @Override
    public int getCount() {
        return dealList.size();
    }

    @Override
    public Object getItem(int location) {
        return dealList.get(location);
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
            convertView = inflater.inflate(R.layout.event_row, null);

            holder = new ViewHolder();

            holder.img_banner = (NetworkImageView) convertView.findViewById(R.id.img_banner);
            holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
            holder.txt_time = (TextView) convertView.findViewById(R.id.txt_time);

            convertView.setTag(holder);
        } else {
            /* We recycle a View that already exists */
            holder = (ViewHolder) convertView.getTag();
        }

        if (imageLoader == null) {
            imageLoader = AppController.getInstance().getImageLoader();
        }

        // Get corresponding deal
        Deal deal = new Deal();
        if(dealList.isEmpty() == false) {
            deal = dealList.get(position);
        }

        // Banner image
        holder.img_banner.setImageUrl(deal.getImage(), AppController.getInstance().getImageLoader());

        // Title
        holder.txt_title.setText(deal.getTitle());

        // Time
        holder.txt_time.setText(deal.getTime("dd/MM/yyyy", "dd/MM/yyyy"));

        return convertView;
    }
}
