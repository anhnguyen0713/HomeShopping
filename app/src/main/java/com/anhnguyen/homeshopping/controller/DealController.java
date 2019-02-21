package com.anhnguyen.homeshopping.controller;

import com.anhnguyen.homeshopping.model.Deal;
import com.anhnguyen.homeshopping.model.Product;

import org.json.JSONException;
import org.json.JSONObject;

public class DealController extends BaseItemController {
    // Json node tag
    public final String TAG_ID = "id";
    public final String TAG_CHANNEL_ID = "channel_id";
    public final String TAG_TITLE = "title";
    public final String TAG_IMAGE_LINK = "image_link";
    public final String TAG_DEAL_LINK = "event_link";
    public final String TAG_START_TIME = "start_time";
    public final String TAG_END_TIME = "end_time";

    public Deal decodeJson(JSONObject obj){
        Deal deal = new Deal();

        try{
            deal.setId(obj.getInt(TAG_ID));
            deal.setTitle(obj.getString(TAG_TITLE));
            deal.setImage(obj.getString(TAG_IMAGE_LINK));
            deal.setLink(obj.getString(TAG_DEAL_LINK));
            deal.setStart_time(Integer.parseInt(obj.getString(TAG_START_TIME)));
            deal.setEnd_time(Integer.parseInt(obj.getString(TAG_END_TIME)));
            deal.setChannel_id(obj.getInt(TAG_CHANNEL_ID));
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return deal;
    }
}
