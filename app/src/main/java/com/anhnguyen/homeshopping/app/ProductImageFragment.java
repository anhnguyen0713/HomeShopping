package com.anhnguyen.homeshopping.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.NetworkImageView;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;

public class ProductImageFragment extends Fragment {
    private String image_link;

    public ProductImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.image_link = getArguments().getString("IMAGE_LINK");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_image, container, false);

        // Get image
        NetworkImageView img_product = (NetworkImageView)view.findViewById(R.id.img_product);
        // Set image link
        img_product.setImageUrl(image_link, AppController.getInstance().getImageLoader());

        return view;
    }

}
