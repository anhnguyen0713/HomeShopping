package com.anhnguyen.homeshopping.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Category;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.util.CustomRequest;
import com.anhnguyen.homeshopping.util.GridViewAdapter;
import com.anhnguyen.homeshopping.util.HeaderGridView;

import org.json.JSONArray;
import org.json.JSONObject;

public class CategoryInfoFragment extends Fragment {
    private GridViewAdapter gridViewAdapter;
    private HeaderGridView gridView;
    private SwipeRefreshLayout swipeContainer;

    private Category category;

    public CategoryInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        category = (Category) getArguments().getSerializable("CATEGORY");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_info, container, false);

        // Gridview
        gridView = (HeaderGridView) view.findViewById(R.id.gridView);
        // Gridview adapter
        gridViewAdapter = new GridViewAdapter(getActivity(), category.getProductsList());
        // Set adapter
        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product p = (Product) gridView.getItemAtPosition(position);
                if(p.canWatch() == false){
                    Intent i = new Intent(getContext(), ViewNonVideoProductActivity.class);
                    i.putExtra("SELECTED_PRODUCT", p);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getContext(), ViewVideoProductActivity.class);
                    i.putExtra("PRODUCT", p);
                    startActivity(i);
                }
            }
        });

        // Swipe to refresh
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProducts();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    public void refreshList() {
        gridViewAdapter.notifyDataSetChanged();
    }

    private void getProducts() {
        category.getProductsList().clear();
        String url = Constants.CATEGORY_PRODUCTS + Integer.toString(category.getId());

        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                swipeContainer.setRefreshing(false);
                try {
                    boolean status = response.getBoolean("status");
                    if (status == true) {
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Product p = AppController.productController.decodeProductJson(obj);

                            category.addToList(p);
                        }
                    }

                    gridViewAdapter.notifyDataSetChanged();
                 } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener eListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        CustomRequest getProductsReq = new CustomRequest(Request.Method.GET, url,
                null, rListener, eListener);

        AppController.getInstance().addToRequestQueue(getProductsReq);
    }
}
