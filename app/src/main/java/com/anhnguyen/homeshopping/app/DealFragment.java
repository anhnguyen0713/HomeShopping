package com.anhnguyen.homeshopping.app;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Deal;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.util.CustomRequest;
import com.anhnguyen.homeshopping.util.ListDealAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DealFragment extends Fragment {
    private LinearLayout btn_deal;
    private ListView listView;
    private static ListDealAdapter eventAdapter;
    private static SwipeRefreshLayout swipeContainer;
    private ArrayList<Deal> dealList = AppController.getInstance().dealList;

    public DealFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deal, container, false);

        // List events
        listView = (ListView) view.findViewById(R.id.listView);
        // Initialize adapter
        eventAdapter = new ListDealAdapter(getActivity(), dealList);
        // Set up adapter
        listView.setAdapter(eventAdapter);

        // Go view event detail when selected
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Deal deal = (Deal) listView.getItemAtPosition(position);

                // Create next activity intent
                Intent i = new Intent(getContext(), ViewDealActivity.class);

                // Pass item position to next activity
                i.putExtra("DEAL", deal);

                // Start new activity
                startActivity(i);
            }
        });

        // Swipe to refresh
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainFeedActivity.getEvents();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    public static void updateData(){
        eventAdapter.notifyDataSetChanged();
    }

    public static void stopRefresh(){
        if(swipeContainer != null) {
            swipeContainer.setRefreshing(false);
        }
    }
}
