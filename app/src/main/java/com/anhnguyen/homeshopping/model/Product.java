package com.anhnguyen.homeshopping.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.util.Constants;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Product extends BaseItem implements Serializable {
    private int old_price;
    private int new_price;
    private String description;
    private String video_link;
    private String stream_link;
    private boolean isHot;

    public Product() {
        isHot = false;
    }

    public int getOld_price() {
        return this.old_price;
    }

    public String getOld_price(String currency) {
        return convertCurrency(old_price, currency);
    }

    public void setOld_price(int old_price) {
        this.old_price = old_price;
    }

    public void setOld_price(double old_price, String currency) {
        if (currency.equals(Constants.CURRENCY_US)) {
            this.old_price = usdToVnd(old_price);
        } else if (currency.equals(Constants.CURRENCY_KOREA)) {
            this.old_price = krwToVnd(old_price);
        }
    }

    public int getNew_price() {
        return this.new_price;
    }

    public String getNew_price(String currency) {
        return convertCurrency(new_price, currency);
    }

    public void setNew_price(int new_price) {
        this.new_price = new_price;
    }

    public void setNew_price(double new_price, String currency) {
        if (currency.equals(Constants.CURRENCY_US)) {
            this.new_price = usdToVnd(new_price);
        } else if (currency.equals(Constants.CURRENCY_KOREA)) {
            this.new_price = krwToVnd(new_price);
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideo_link() {
        return this.video_link;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public String getStream_link(){
        return this.stream_link;
    }

    public void setStream_link(String stream_link) {
        this.stream_link = stream_link;
    }

    public int calcDiscount() {
        if (old_price != 0) {
            double oldP = (double) old_price;
            double newP = (double) new_price;
            double discount = ((oldP - newP) / oldP) * 100;

            return (int) discount;
        }
        return 0;
    }

    private String convertCurrency(int price, String currency) {
        String formattedPrice = "";

        if (currency.equals(Constants.CURRENCY_VIETNAM)) {
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            formattedPrice = formatter.format(price);
        } else if (currency.equals(Constants.CURRENCY_US)) {
            DecimalFormat formatter = new DecimalFormat("###.##");
            formattedPrice = formatter.format(vndToUsd(price));
        } else if (currency.equals(Constants.CURRENCY_KOREA)) {
            DecimalFormat formatter = new DecimalFormat("###.##");
            formattedPrice = formatter.format(vndToKrw(price));
        }

        return formattedPrice;
    }

    private double vndToUsd(double price) {
        return price / 22289;
    }

    private double vndToKrw(double price) {
        return price / 19180;
    }

    private int usdToVnd(double price) {
        return (int) (price * 22289);
    }

    private int krwToVnd(double price) {
        return (int) (price * 19180);
    }

    public String getTimeRemained() {
        long current = System.currentTimeMillis()/1000;
        long remain = (long)start_time - current;

        String output = "00:00:00";

        long seconds = remain;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 60;

        String secondsD = String.valueOf(seconds);
        String minutesD = String.valueOf(minutes);
        String hoursD = String.valueOf(hours);

        if (seconds < 10)
            secondsD = "0" + seconds;
        if (minutes < 10)
            minutesD = "0" + minutes;
        if (hours < 10)
            hoursD = "0" + hours;

        if(AppController.currency.equals(Constants.CURRENCY_VIETNAM)){
            output = hoursD + " giờ " + minutesD + " phút " + secondsD + " giây";
        } else if(AppController.currency.equals(Constants.CURRENCY_US)) {
            output = hoursD + "h " + minutesD + "m " + secondsD + "s";
        }

        return output;
    }

    public boolean canWatch(){
        if(!(video_link.matches("")||video_link.equals(null))){
            return true;
        }

        long current = System.currentTimeMillis()/1000;
        long start = (long)start_time;
        long end = (long)end_time;
        if(current >= start && current <= end) {
            return true;
        }

        return false;
    }

    public void setIsHot(boolean isHot) {
        this.isHot = isHot;
    }

    public boolean getIsHot() {
        return isHot;
    }
}
