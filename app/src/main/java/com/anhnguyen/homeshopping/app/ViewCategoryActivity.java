package com.anhnguyen.homeshopping.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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
import com.anhnguyen.homeshopping.util.ListDealAdapter;
import com.anhnguyen.homeshopping.util.VPAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewCategoryActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProgressDialog pDialog;

    private int numOfTabs;
    private int count = 1;
    private static boolean firstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(firstStart == true) {
            // Loading dialog
            createLoadingDialog();
            firstStart = false;
        }

        // Get position of selected item
        Intent intent = getIntent();
        int position = intent.getIntExtra("POSITION", 0);

        // Get tab layout
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Get viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewpager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(position).select();
    }

    private void setupViewpager(ViewPager viewPager){
        // Create adapter for viewpager
        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());

        // Get list of category
        ArrayList<Category> categories = AppController.getInstance().categoryList;

        // Number of tabs = number of on air products
        numOfTabs = categories.size();

        for (int i = 0; i < numOfTabs; i++) {
            // Get corresponding category
            Category category = categories.get(i);

            // Create a new Bundle
            Bundle b = new Bundle();

            // Put category to bundle
            b.putSerializable("CATEGORY", category);

            // Create new fragment
            CategoryInfoFragment cf = new CategoryInfoFragment();

            // Get products
            if(category.getProductsList().isEmpty() == true) {
                getProducts(category, cf);
            }

            // Put bundle to fragment
            cf.setArguments(b);

            // Add new fragment to screen
            adapter.addFragment(cf, category.getName());
        }

        viewPager.setAdapter(adapter);
    }

    private void createLoadingDialog(){
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        pDialog.setCanceledOnTouchOutside(false);
    }

    private void getProducts(final Category category, final CategoryInfoFragment frm) {
        String url = Constants.CATEGORY_PRODUCTS + Integer.toString(category.getId());

        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Product response", response.toString());
                if(count == numOfTabs && pDialog != null){
                    pDialog.hide();
                } else {
                    count++;
                }
                System.out.println("Count: " + count);
                System.out.println("Max: " + numOfTabs);
                try {
                    boolean status = response.getBoolean("status");
                    if(status == true) {
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Product p = AppController.productController.decodeProductJson(obj);
                            Product tmp = AppController.productController.findById(AppController.getInstance().hotList, p.getId());
                            if(tmp != null){
                                p.setIsHot(true);
                            }
                            category.addToList(p);
                        }
                    }

                    frm.refreshList();
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
