package com.anhnguyen.homeshopping.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.util.CustomRequest;
import com.anhnguyen.homeshopping.util.ListViewNoRemoveAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ComparePriceActivity extends AppCompatActivity {
    private ArrayList<Product> compareList = new ArrayList<>();
    private ProgressDialog pDialog;
    private ListView listView;
    private SwipeRefreshLayout swipeContainer;
    private ListViewNoRemoveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_price);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get product
        Intent i = getIntent();
        final Product p = (Product) i.getSerializableExtra("PRODUCT");

        createLoadingDialog();
        getCompareList(p);

        // Load fontawesome
        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        // Remove default app name in top app bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LinearLayout btn_title = (LinearLayout) findViewById(R.id.btn_title);

        // Style top left button
        TextView back_icon = (TextView) btn_title.getChildAt(0);
        back_icon.setTypeface(fontawesome);
        back_icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        TextView text = (TextView) btn_title.getChildAt(1);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        // Set event of back to main screen of top left button
        btn_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // List view
        listView = (ListView) findViewById(R.id.listView);
        // Adapter
        adapter = new ListViewNoRemoveAdapter(this, compareList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product p = (Product) listView.getItemAtPosition(position);
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
                getCompareList(p);
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

    private void getCompareList(final Product p){
        if(compareList.isEmpty() == false){
            compareList.clear();
        }

        compareList.add(p);

        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.hide();
                swipeContainer.setRefreshing(false);
                Log.d("Comapre response", response.toString());
                try {
                    boolean status = response.getBoolean("status");
                    if (status == true) {
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Product p = AppController.productController.decodeProductJson(obj);

                            compareList.add(p);
                        }

                        adapter.notifyDataSetChanged();
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

        CustomRequest getCompareReq = new CustomRequest(Request.Method.POST, Constants.COMPARE_URL,
                null, rListener, eListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("product_id", Integer.toString(p.getId()));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(getCompareReq);
    }

}
