package com.anhnguyen.homeshopping.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.util.GridViewAdapter;
import com.anhnguyen.homeshopping.util.ListViewNoRemoveAdapter;
import com.anhnguyen.homeshopping.util.MyGridView;

import java.util.ArrayList;
import java.util.List;

public class LiveTVFragment extends Fragment {
    // Get list of on air product
    private ArrayList<Product> onairList = AppController.getInstance().onairList;
    // Get list of upcoming product
    private ArrayList<Product> upcomingList = AppController.getInstance().upcomingList;

    private MyGridView gridView;
    private ListView listView;
    private static SwipeRefreshLayout swipeContainer;

    private static ListViewNoRemoveAdapter listViewNoRemoveAdapter;
    private static GridViewAdapter gridViewAdapter;

    private static Handler handler = new Handler();

    private static final int UPDATE_INTERVAL = 1 * 1000 * 60; // in milliseconds

    public LiveTVFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_tv, container, false);

        // Get list view
        listView = (ListView) view.findViewById(R.id.listView);
        // Initialize adapter list view
        listViewNoRemoveAdapter = new ListViewNoRemoveAdapter(getActivity(), upcomingList);

        // Create grid view
        gridView = new MyGridView(getContext());
        AbsListView.LayoutParams gridParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        gridView.setLayoutParams(gridParams);
        gridView.setNumColumns(2);
        gridView.setHorizontalSpacing(16);
        gridView.setVerticalSpacing(16);
        gridView.setPadding(16, 24, 16, 24);

        // Initialize adapter grid view
        gridViewAdapter = new GridViewAdapter(getActivity(), onairList);

        // Set adapter grid view
        gridView.setAdapter(gridViewAdapter);

        // Get advertisement banner
        View banner = getLayoutInflater(savedInstanceState).inflate(R.layout.ads_banner, null);
        NetworkImageView img_banner = (NetworkImageView) banner.findViewById(R.id.img_ads_banner);
        img_banner.setImageUrl(Constants.BANNER_URL, AppController.getInstance().getImageLoader());

        // Add banner as list view header
        listView.addHeaderView(banner, null, false);

        // Get on air tag
        View tag_onair = getLayoutInflater(savedInstanceState).inflate(R.layout.tag_group_product, null);
        TextView txt_tag_onair = (TextView) tag_onair.findViewById(R.id.txt_tag);
        txt_tag_onair.setText(R.string.tag_onair);

        // Add tag as header
        listView.addHeaderView(tag_onair, null, false);

        // Add grid view as header of list view
        listView.addHeaderView(gridView);

        // Get upcoming tag
        View tag_upcoming = getLayoutInflater(savedInstanceState).inflate(R.layout.tag_group_product, null);
        TextView txt_tag_upcoming = (TextView) tag_upcoming.findViewById(R.id.txt_tag);
        txt_tag_upcoming.setText(R.string.tag_upcoming);

        // Add tag as header
        listView.addHeaderView(tag_upcoming, null, false);

        // Set adapter list view
        listView.setAdapter(listViewNoRemoveAdapter);

        // Set listener when click item grid view
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product p =(Product) gridView.getItemAtPosition(position);
                Intent viewProduct_intent = new Intent(getContext(), ViewVideoProductActivity.class);
                viewProduct_intent.putExtra("PRODUCT", p);
                startActivity(viewProduct_intent);
            }
        });

        // Set listener when click item list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product p = (Product) listView.getItemAtPosition(position);
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
                MainFeedActivity.getLiveTVData();
                MainFeedActivity.getChannelData();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnableCode);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateData();
    }

    public static void startHandler(){
        handler.postDelayed(runnableCode, UPDATE_INTERVAL);
    }

    public static void removeCallback(){
        handler.removeCallbacks(runnableCode);
    }

    public static void updateData(){
        gridViewAdapter.notifyDataSetChanged();
        listViewNoRemoveAdapter.notifyDataSetChanged();
    }

    private static Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            boolean isChanged = false;
            long current = System.currentTimeMillis() / 1000;

            // Check on air list
            if (AppController.getInstance().onairList.isEmpty() == false) {
                for (int i = 0; i < AppController.getInstance().onairList.size(); i++) {
                    Product p = AppController.getInstance().onairList.get(i);
                    long end = (long) p.getEnd_time();
                    if (current > end) {
                        AppController.productController.removeFrom(AppController.getInstance().onairList, p);
                        isChanged = true;
                    }
                }
            }

            // Check upcoming list
            if (AppController.getInstance().upcomingList.isEmpty() == false) {
                for (int i = 0; i < AppController.getInstance().upcomingList.size(); i++) {
                    Product p = AppController.getInstance().upcomingList.get(i);
                    long start = (long) p.getStart_time();
                    long end = (long) p.getEnd_time();
                    if (current >= start && current <= end) {
                        AppController.productController.removeFrom(AppController.getInstance().upcomingList, p);
                        AppController.productController.addTo(AppController.getInstance().onairList, 0, p);
                        isChanged = true;
                    }
                }
            }

            if (isChanged == true) {
                listViewNoRemoveAdapter.notifyDataSetChanged();
                gridViewAdapter.notifyDataSetChanged();
            }

            System.out.println("Run......................");

            handler.postDelayed(runnableCode, UPDATE_INTERVAL);
        }
    };

    public static void stopRefresh(){
        swipeContainer.setRefreshing(false);
    }
}
