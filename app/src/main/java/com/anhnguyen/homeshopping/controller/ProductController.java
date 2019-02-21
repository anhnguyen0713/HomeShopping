package com.anhnguyen.homeshopping.controller;

import com.anhnguyen.homeshopping.model.Product;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductController extends BaseItemController{
    // Json node tag
    public final String TAG_ID = "id";
    public final String TAG_CHANNEL_ID = "channel_id";
    public final String TAG_TITLE = "title";
    public final String TAG_IMAGE_LINK = "image_link";
    public final String TAG_VIDEO_LINK = "video_link";
    public final String TAG_STREAM_LINK = "stream_link";
    public final String TAG_PRODUCT_LINK = "product_link";
    public final String TAG_OLD_PRICE = "old_price";
    public final String TAG_NEW_PRICE = "new_price";
    public final String TAG_DESCRIPTION = "description";
    public final String TAG_START_TIME = "start_time";
    public final String TAG_END_TIME = "end_time";

    public Product decodeProductJson(JSONObject obj){
        Product p = new Product();

        try{
            p.setId(obj.getInt(TAG_ID));
            p.setTitle(obj.getString(TAG_TITLE));
            p.setImage(obj.getString(TAG_IMAGE_LINK));
            p.setVideo_link(obj.getString(TAG_VIDEO_LINK));
            p.setStream_link(obj.getString(TAG_STREAM_LINK));
            p.setLink(obj.getString(TAG_PRODUCT_LINK));
            p.setOld_price(Integer.parseInt(obj.getString(TAG_OLD_PRICE)));
            p.setNew_price(Integer.parseInt(obj.getString(TAG_NEW_PRICE)));
            p.setStart_time(Integer.parseInt(obj.getString(TAG_START_TIME)));
            p.setEnd_time(Integer.parseInt(obj.getString(TAG_END_TIME)));
            p.setChannel_id(obj.getInt(TAG_CHANNEL_ID));
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return p;
    }

    public Product findById(ArrayList<Product> productList, int id) {
        for (int i = 0; i < productList.size(); i++) {
            Product p = productList.get(i);
            if(p.getId() == id){
                return p;
            }
        }
        return null;
    }
}
