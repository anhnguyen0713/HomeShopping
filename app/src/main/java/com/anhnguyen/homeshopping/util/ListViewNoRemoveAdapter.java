package com.anhnguyen.homeshopping.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.controller.ChannelController;
import com.anhnguyen.homeshopping.model.Channel;
import com.anhnguyen.homeshopping.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anhnguyen on 11/04/2016.
 * Adapter of list view
 * No remove button on top right corner
 */
public class ListViewNoRemoveAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Product> productList;
    private ViewHolder holder;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ListViewNoRemoveAdapter(Activity activity, ArrayList<Product> productList) {
        this.activity = activity;
        this.productList = productList;
    }

    static class ViewHolder {
        NetworkImageView img_thumbnail;
        NetworkImageView img_logo;
        TextView txt_title;
        TextView txt_oldPrice;
        TextView txt_newPrice;
        TextView txt_time;
        ToggleButton tglBtn;
        ImageView overlay_play;
        ImageView bestseller;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int location) {
        return productList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            /* There is no view at this position, we create a new one.
           In this case by inflating an xml layout */
            convertView = inflater.inflate(R.layout.non_remove_row, null);

            holder = new ViewHolder();

            holder.img_thumbnail = (NetworkImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.img_logo = (NetworkImageView) convertView.findViewById(R.id.img_logo);
            holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
            holder.txt_oldPrice = (TextView) convertView.findViewById(R.id.txt_oldPrice);
            holder.txt_newPrice = (TextView) convertView.findViewById(R.id.txt_newPrice);
            holder.txt_time = (TextView) convertView.findViewById(R.id.txt_time);
            holder.tglBtn = (ToggleButton) convertView.findViewById(R.id.tglBtn_alarm);
            holder.overlay_play = (ImageView) convertView.findViewById(R.id.overlay_play);
            holder.bestseller = (ImageView) convertView.findViewById(R.id.bestseller);

            convertView.setTag(holder);
        } else {
            /* We recycle a View that already exists */
            holder = (ViewHolder) convertView.getTag();
        }

        if (imageLoader == null) {
            imageLoader = AppController.getInstance().getImageLoader();
        }

        // getting product data for the row
        final Product p = productList.get(position);

        // thumbnail image
        holder.img_thumbnail.setImageUrl(p.getImage(), imageLoader);

        // logo channel
        Channel c = AppController.channelController.findById(AppController.getInstance().channelList, p.getChannel_id());
        if (c != null) {
            holder.img_logo.setImageUrl(c.getLogo(), imageLoader);
        } else {
            holder.img_logo.setImageUrl("", imageLoader);
        }

        // title
        holder.txt_title.setText(p.getTitle());

        // old price
        if (p.getOld_price() <= p.getNew_price()) {
            holder.txt_oldPrice.setVisibility(View.INVISIBLE);
        } else {
            holder.txt_oldPrice.setText(p.getOld_price(AppController.currency) + AppController.currency);
            holder.txt_oldPrice.setPaintFlags(holder.txt_oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // new price
        if (p.getNew_price() == 0) {
            holder.txt_newPrice.setVisibility(View.INVISIBLE);
        } else {
            holder.txt_newPrice.setText(p.getNew_price(AppController.currency) + AppController.currency);
        }

        // showing time
        if (p.getStart_time() == 0 || p.getEnd_time() == 0) {
            holder.txt_time.setVisibility(View.INVISIBLE);
        } else {
            String showTime = p.getTime();
            holder.txt_time.setText(showTime);
        }

        // best seller
        if (p.getIsHot()){
            holder.bestseller.setVisibility(View.VISIBLE);
        }

        // toggle button
        long currentTimestamp = System.currentTimeMillis() / 1000;
        if ((long) p.getStart_time() < currentTimestamp) {
            holder.tglBtn.setVisibility(View.INVISIBLE);
        }

        final Product tmp = AppController.notificationController.findById(AppController.getInstance().notiList, p.getId());
        if (tmp != null) {
            holder.tglBtn.setChecked(true);
        } else {
            holder.tglBtn.setChecked(false);
        }

        holder.tglBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked() == true) {
                    AppController.notificationController.addTo(AppController.getInstance().notiList, p);
                    startAlarm(p);
                } else {
                    AppController.notificationController.removeFrom(AppController.getInstance().notiList, tmp);
                    cancelAlarm(p);
                }
            }
        });

        // Overlay play button
        if (p.canWatch()){
            holder.overlay_play.setVisibility(View.VISIBLE);
        } else {
            holder.overlay_play.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private void startAlarm(Product p){
        long currentSeconds = System.currentTimeMillis()/1000L;
        long startStream = (long)p.getStart_time();
        long wait = startStream - currentSeconds;

        Intent intent = new Intent(activity, MyBroadcastReceiver.class);
        intent.putExtra("PRODUCT", p);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(),
                p.getId()+p.getStart_time(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + wait*1000, pendingIntent);
        Toast.makeText(activity, activity.getResources().getString(R.string.alarm_set),
                Toast.LENGTH_LONG).show();
    }

    private void cancelAlarm(Product p){
        Intent intent = new Intent(activity, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity.getApplicationContext(),
                p.getId()+p.getStart_time(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
