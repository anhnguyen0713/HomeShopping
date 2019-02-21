package com.anhnguyen.homeshopping.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.controller.TypefaceController;
import com.anhnguyen.homeshopping.model.Channel;
import com.anhnguyen.homeshopping.model.Product;

import java.util.List;

/**
 * Created by anhnguyen on 11/04/2016.
 * Adapter of on air product grid view
 */
public class GridViewAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Product> onairProducts;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public GridViewAdapter(Activity activity, List<Product> onairProducts) {
        this.activity = activity;
        this.onairProducts = onairProducts;
    }

    @Override
    public int getCount() {
        return onairProducts.size();
    }

    @Override
    public Object getItem(int location) {
        return onairProducts.get(location);
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
            convertView = inflater.inflate(R.layout.girdview_cell, null);
        }

        if (imageLoader == null) {
            imageLoader = AppController.getInstance().getImageLoader();
        }

        // getting product data for the row
        Product p = onairProducts.get(position);

        // thumbnail image
        NetworkImageView img_thumbnail = (NetworkImageView) convertView.findViewById(R.id.img_thumbnail);
        img_thumbnail.setImageUrl(p.getImage(), imageLoader);

        // logo channel
        NetworkImageView img_logo = (NetworkImageView) convertView.findViewById(R.id.img_logo);
        Channel c = AppController.channelController.findById(AppController.getInstance().channelList, p.getChannel_id());
        if(c != null){
            img_logo.setImageUrl(c.getLogo(), imageLoader);
        } else {
            img_logo.setImageUrl("", imageLoader);
        }

        // title
        TextView txt_title = (TextView) convertView.findViewById(R.id.txt_title);
        txt_title.setText(p.getTitle());

        // old price
        TextView txt_oldPrice = (TextView) convertView.findViewById(R.id.txt_oldPrice);
        if(p.getOld_price() <= p.getNew_price()){
            txt_oldPrice.setVisibility(View.INVISIBLE);
        } else {
            txt_oldPrice.setText(p.getOld_price(AppController.currency) + AppController.currency);
            txt_oldPrice.setPaintFlags(txt_oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // new price
        TextView txt_price = (TextView) convertView.findViewById(R.id.txt_newPrice);
        txt_price.setText(p.getNew_price(AppController.currency)+AppController.currency);

        // Overlay play button
        ImageView overlay_play = (ImageView) convertView.findViewById(R.id.overlay_play);
        if (p.canWatch()){
            overlay_play.setVisibility(View.VISIBLE);
        } else {
            overlay_play.setVisibility(View.INVISIBLE);
        }

        // Best seller
        ImageView bestseller = (ImageView) convertView.findViewById(R.id.bestseller);
        if(p.getIsHot()) {
            bestseller.setVisibility(View.VISIBLE);
        }

        // Discount
        TextView txt_discount = (TextView) convertView.findViewById(R.id.txt_discount);
        if(p.getNew_price() >= p.getOld_price()){
            txt_discount.setVisibility(View.INVISIBLE);
        } else {
            int discount = p.calcDiscount();
            txt_discount.setText("-" + Integer.toString(discount) + "%");
        }

        return convertView;
    }


}
