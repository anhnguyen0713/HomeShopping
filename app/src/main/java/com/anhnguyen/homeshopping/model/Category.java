package com.anhnguyen.homeshopping.model;

import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.util.Constants;

import java.io.Serializable;
import java.util.ArrayList;

public class Category implements Serializable{
    private int id;
    private String name_en;
    private String name_vi;
    private String image;

    private ArrayList<Product> productsList = new ArrayList<>();

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName(){
        if(AppController.currency.equals(Constants.CURRENCY_US)){
            return name_en;
        } else if(AppController.currency.equals(Constants.CURRENCY_VIETNAM)){
            return name_vi;
        }

        return "";
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public void setName_vi(String name_vi) {
        this.name_vi = name_vi;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

    public void setProductsList(ArrayList<Product> productsList) {
        this.productsList = productsList;
    }

    public ArrayList<Product> getProductsList() {
        return productsList;
    }

    public void addToList(Product p){
        productsList.add(p);
    }
}
