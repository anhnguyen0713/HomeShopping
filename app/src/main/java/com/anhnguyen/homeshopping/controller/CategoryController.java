package com.anhnguyen.homeshopping.controller;

import com.anhnguyen.homeshopping.model.Category;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoryController extends BaseItemController {
    // Json node tag
    public final String TAG_ID = "id";
    public final String TAG_NAME_EN = "name_en";
    public final String TAG_NAME_VI = "name_vi";
    public final String TAG_IMAGE = "image";

    public Category decodeJson(JSONObject obj){
        Category category = new Category();

        try{
            category.setId(obj.getInt(TAG_ID));
            category.setName_en(obj.getString(TAG_NAME_EN));
            category.setName_vi(obj.getString(TAG_NAME_VI));
            category.setImage(obj.getString(TAG_IMAGE));
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return category;
    }
}
