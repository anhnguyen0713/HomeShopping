package com.anhnguyen.homeshopping.model;

import com.anhnguyen.homeshopping.controller.AppController;

import java.io.Serializable;
import java.util.ArrayList;

public class Deal extends BaseItem implements Serializable{
    private ArrayList<Product> products = new ArrayList<>();

    public Deal(){

    }

    public ArrayList<Product> getProducts(){
        return products;
    }

    public void addProduct(Product p){
        products.add(p);
    }

    public void addProduct(Product p, int position){
        products.add(position, p);
    }

    public void removeProduct(int position){
        products.remove(position);
    }

    public void removeProduct(Product p){
        products.remove(p);
    }

    public boolean contains(Product p){
        Product _p = AppController.productController.findById(products, p.getId());
        if(_p == null){
            return false;
        } else {
            return true;
        }
    }

    public int getNumOfProducts(){
        return products.size();
    }
}
