package com.anhnguyen.homeshopping.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Deal;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.util.CustomRequest;
import com.anhnguyen.homeshopping.util.GridViewAdapter;
import com.anhnguyen.homeshopping.util.HeaderGridView;
import com.anhnguyen.homeshopping.util.VPAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewDealActivity extends AppCompatActivity {
    private LinearLayout btn_deal_title;
    private ProgressDialog pDialog;
    private GridViewAdapter gridViewAdapter;
    private HeaderGridView gridView;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_deal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        final Deal deal = (Deal) i.getSerializableExtra("DEAL");

        createLoadingDialog();
        getProducts(deal);

        // Load fontawesome
        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        // Remove default app name in top app bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_deal_title = (LinearLayout) findViewById(R.id.btn_deal_title);

        // Style top left button
        TextView back_icon = (TextView) btn_deal_title.getChildAt(0);
        back_icon.setTypeface(fontawesome);
        back_icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        TextView text = (TextView) btn_deal_title.getChildAt(1);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        // Set event of back to main screen of top left button
        btn_deal_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Gridview
        gridView = (HeaderGridView) findViewById(R.id.gridView);

        // Gridview adapter
        gridViewAdapter = new GridViewAdapter(this, deal.getProducts());

        // Get deal banner
        View banner = getLayoutInflater().inflate(R.layout.event_banner, null);
        NetworkImageView img_banner = (NetworkImageView) banner.findViewById(R.id.img_ads_banner);
        img_banner.setImageUrl(deal.getImage(), AppController.getInstance().getImageLoader());
        img_banner.setDefaultImageResId(R.drawable.image_holder);

        // Add banner as header
        gridView.addHeaderView(banner, null, false);

        // Set adapter
        gridView.setAdapter(gridViewAdapter);

        // View item when selected
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product p = (Product) gridView.getItemAtPosition(position);
                if(p.canWatch() == false){
                    Intent i = new Intent(getApplicationContext(), ViewNonVideoProductActivity.class);
                    i.putExtra("SELECTED_PRODUCT", p);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getApplicationContext(), ViewVideoProductActivity.class);
                    i.putExtra("PRODUCT", p);
                    startActivity(i);
                }
            }
        });

        // Swipe to refresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProducts(deal);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void createLoadingDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        pDialog.setCanceledOnTouchOutside(false);
    }

    private void getProducts(final Deal deal) {
        if (deal.getProducts() != null) {
            deal.getProducts().clear();
        }

        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.hide();
                swipeContainer.setRefreshing(false);
                Log.d("Product response", response.toString());
                try {
                    boolean status = response.getBoolean("status");
                    if (status == true) {
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Product p = AppController.productController.decodeProductJson(obj);

                            deal.addProduct(p);
                        }

                        gridViewAdapter.notifyDataSetChanged();
                    }
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

        CustomRequest getProductsReq = new CustomRequest(Request.Method.POST, Constants.DEAL_DETAIL_URL,
                null, rListener, eListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("event_id", Integer.toString(deal.getId()));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(getProductsReq);
    }
}
