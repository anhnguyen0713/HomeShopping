package com.anhnguyen.homeshopping.controller;

import java.util.ArrayList;

public class ExtendProductController extends ProductController {
    public static final int MAX_ITEM = 20;

    @Override
    public void addTo(ArrayList list, Object id) {
        int size = list.size();

        if (size < MAX_ITEM) {
            list.add(0, id);
        } else {
            list.remove(MAX_ITEM - 1);
            list.add(0, id);
        }
    }
}
