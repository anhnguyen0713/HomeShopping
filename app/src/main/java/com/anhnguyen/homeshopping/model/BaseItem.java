package com.anhnguyen.homeshopping.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class BaseItem implements Serializable{
    protected int id;
    protected int channel_id;
    protected String title;
    protected String link;
    protected String image;
    protected int start_time;
    protected int end_time;

    public BaseItem(){

    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(int channel_id) {
        this.channel_id = channel_id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getStart_time() {
        return this.start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getEnd_time() {
        return this.end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    private String formatTime(int timestamp, String format){
        Timestamp ts = new Timestamp((long)timestamp*1000);
        DateFormat f = new SimpleDateFormat(format);

        String formattedTime = f.format(ts);

        return formattedTime;
    }

    public String getTime(){
        String showTime = formatTime(start_time, "HH:mm") + " - "
                + formatTime(end_time, "HH:mm");
        return showTime;
    }

    public String getTime(String start_format, String end_format){
        String showTime = formatTime(start_time, start_format) + " - "
                + formatTime(end_time, end_format);
        return showTime;
    }
}
