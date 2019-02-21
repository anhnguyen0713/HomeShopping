package com.anhnguyen.homeshopping.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.util.CategoryGridAdapter;
import com.anhnguyen.homeshopping.util.ListViewNoRemoveAdapter;
import com.anhnguyen.homeshopping.util.MyGridView;

public class HotFragment extends Fragment {
    private ListView listView;
    private MyGridView cateGrid;
    private static ListViewNoRemoveAdapter lAdapter;
    private static CategoryGridAdapter cAdapter;
    private static SwipeRefreshLayout swipeContainer;

    public HotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hot, container, false);

        // List view
        listView = (ListView) view.findViewById(R.id.listView);
        lAdapter = new ListViewNoRemoveAdapter(getActivity(), AppController.getInstance().hotList);

        // Category grid view
        cateGrid = new MyGridView(getContext());
        AbsListView.LayoutParams gridParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        cateGrid.setLayoutParams(gridParams);
        cateGrid.setNumColumns(3);
        cateGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        cateGrid.setPadding(0,0,0,16);

        cAdapter = new CategoryGridAdapter(getActivity(), AppController.getInstance().categoryList);

        cateGrid.setAdapter(cAdapter);

        // Set cate grid as header
        listView.addHeaderView(cateGrid);

        // Set list view adapter
        listView.setAdapter(lAdapter);

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

        // View category when selecting category
        cateGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getContext(), ViewCategoryActivity.class);
                i.putExtra("POSITION", position);
                startActivity(i);
            }
        });

        // Swipe to refresh
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_to_refresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainFeedActivity.getHotProduct();
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

    public static void updateData(){
        cAdapter.notifyDataSetChanged();
        lAdapter.notifyDataSetChanged();
    }

    public static void stopRefresh(){
        swipeContainer.setRefreshing(false);
    }
}
