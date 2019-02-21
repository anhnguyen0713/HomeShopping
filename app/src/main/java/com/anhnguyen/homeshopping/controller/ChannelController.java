package com.anhnguyen.homeshopping.controller;

import com.anhnguyen.homeshopping.model.Channel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChannelController extends BaseItemController{
    // JSON node tag
    public final String TAG_ID = "id";
    public final String TAG_NAME = "name";
    public final String TAG_LOGO = "logo";
    public final String TAG_HOMEPAGE = "homepage";
    public final String TAG_HOTLINE = "hotline";
    public final String TAG_DESCRIPTION = "description";

    public Channel decodeChannelJson(JSONObject obj) {
        Channel c = new Channel();

        try {
            c.setId(obj.getInt(TAG_ID));
            c.setName(obj.getString(TAG_NAME));
            c.setLogo(obj.getString(TAG_LOGO));
            c.setHomepage(obj.getString(TAG_HOMEPAGE));
            c.setHotline(obj.getString(TAG_HOTLINE));
            c.setDescription(obj.getString(TAG_DESCRIPTION));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return c;
    }

    public Channel findById(ArrayList<Channel> channelList, int id) {
        for (int i = 0; i < channelList.size(); i++) {
            Channel c = channelList.get(i);
            if(c.getId() == id){
                return c;
            }
        }
        return null;
    }
}
