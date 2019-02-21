package com.anhnguyen.homeshopping.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.controller.RequestController;
import com.anhnguyen.homeshopping.model.Category;
import com.anhnguyen.homeshopping.model.Channel;
import com.anhnguyen.homeshopping.model.Deal;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.util.VPAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainFeedActivity extends AppCompatActivity {
    public static final int NUM_REQUEST = 5;
    public static int count_request = 0;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static ProgressDialog pDialog;
    public static android.support.v7.app.AlertDialog aDialog;

    public static boolean firstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default app name in top app bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get data
        getLiveTVData();

        getCategories();

        getEvents();

        getHotProduct();

        getChannelData();

        if(firstStart == true){
            // Retrieve favorite list from file
            ArrayList<Product> pAr = AppController.extendProductController.readFrom(this, Constants.FILE_FAVORITE);
            if(pAr != null){
                AppController.getInstance().favoriteList = pAr;
            }

            // Retrieve watch recent list from file
            ArrayList<Product> wAr = AppController.extendProductController.readFrom(this, Constants.FILE_WATCHRECENT);
            if(wAr != null){
                AppController.getInstance().watchRecentList = wAr;
            }

            // Retrieve notification list from file
            ArrayList<Product> nAr = AppController.extendProductController.readFrom(this, Constants.FILE_NOTIFICATION);
            if(nAr != null){
                AppController.getInstance().notiList = nAr;
            }

            firstStart = false;
        }

        // Update notification list
        if(AppController.getInstance().notiList.isEmpty() == false){
            long current = System.currentTimeMillis()/1000;

            for (int i = 0; i < AppController.getInstance().notiList.size(); i++) {
                Product p = AppController.getInstance().notiList.get(i);
                long start = (long)p.getStart_time();
                if(current > start) {
                    Product tmp = AppController.notificationController.findById(AppController.getInstance().notiList, p.getId());
                    AppController.notificationController.removeFrom(AppController.getInstance().notiList, tmp);
                }
            }
        }

        // Show starting screen
        pDialog = new ProgressDialog(this, R.style.AppTheme_NoActionBar);
        pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pDialog.show();
        pDialog.setContentView(R.layout.start_dialog);

        // Initialize error dialog
        aDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.error_dialog, null);
        aDialog.setView(layout);

        // Search area
        EditText editTxt_search = (EditText) findViewById(R.id.editTxt_search);
        editTxt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        // Get button PROFILE
        ImageButton btn_myProfile = (ImageButton) findViewById(R.id.btn_myProfile);
        // Go to activity Profile when touch button
        btn_myProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myProfile_intent = new Intent(getApplicationContext(), MyProfileActivity.class);
                startActivity(myProfile_intent);
            }
        });

        // Get button NOTIFICATION
        ImageButton btn_notification = (ImageButton) findViewById(R.id.btn_notification);
        // Go to activity Notification when touch button
        btn_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notification_intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(notification_intent);
            }
        });

        // Tab layout
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // View pager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewpager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
        tabLayout.getTabAt(1).select();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        count_request = 0;
    }

    @Override
    public void onStop(){
        super.onStop();

        // Save favorite list to file
        AppController.extendProductController.saveTo(this, AppController.getInstance().favoriteList, Constants.FILE_FAVORITE);

        // Save watch recent list to file
        AppController.extendProductController.saveTo(this, AppController.getInstance().watchRecentList, Constants.FILE_WATCHRECENT);

        // Save notification list to file
        AppController.notificationController.saveTo(this, AppController.getInstance().notiList, Constants.FILE_NOTIFICATION);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save user
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isLoggedIn", AppController.getInstance().getUser().isLoggedIn());
        editor.putString("username", AppController.getInstance().getUser().getName());
        editor.putString("email", AppController.getInstance().getUser().getEmail());
        editor.putString("avatar", AppController.getInstance().getUser().getAvatar());

        // Commit the edits
        editor.commit();
    }

    public static void showErrorDialog() {
        aDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LiveTVFragment.removeCallback();
                System.exit(1);
            }
        });
        aDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Thử lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get data
                getLiveTVData();
                getCategories();
                getEvents();
            }
        });
        aDialog.show();
    }

    private void setupViewpager(ViewPager viewPager){
        // Create adapter for viewpager
        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());

        // All products fragment
        HotFragment aFragment = new HotFragment();
        adapter.addFragment(aFragment, "Hot");

        // Live TV fragment
        LiveTVFragment vFragment = new LiveTVFragment();
        adapter.addFragment(vFragment, "Live");

        // Deal fragment
        DealFragment dFragment = new DealFragment();
        adapter.addFragment(dFragment, "Deal");

        viewPager.setAdapter(adapter);
    }

    private void setupTabLayout(){
        tabLayout.getTabAt(0).setCustomView(styleTab(R.string.fa_star_o, R.string.txt_btn_hot));
        tabLayout.getTabAt(1).setCustomView(styleTab(R.string.fa_television, R.string.live_tv));
        tabLayout.getTabAt(2).setCustomView(styleTab(R.string.fa_angle_double_down, R.string.txt_btn_deal));
    }

    private ViewGroup styleTab(int icon_id, int name_id){
        // Load fontawesome
        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_tab, null);
        TextView icon = (TextView) view.getChildAt(0);
        icon.setText(getResources().getString(icon_id));
        icon.setTypeface(fontawesome);
        TextView text = (TextView) view.getChildAt(1);
        text.setText(getResources().getString(name_id));

        return view;
    }

    public static void getCategories(){
        AppController.categoryController.clear(AppController.getInstance().categoryList);

        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HotFragment.stopRefresh();
                Log.d("Cate response", response.toString());
                try {
                    Boolean status = response.getBoolean("status");
                    if(status == true) {
                        count_request++;
                        if(count_request == NUM_REQUEST) {
                            pDialog.hide();
                        }

                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Category category = AppController.categoryController.decodeJson(obj);
                            AppController.categoryController.addTo(AppController.getInstance().categoryList, category);
                        }

                        HotFragment.updateData();
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
                if(!aDialog.isShowing()) {
                    showErrorDialog();
                }
            }
        };

        JsonObjectRequest cateReq = new JsonObjectRequest(Request.Method.GET, Constants.CATEGORY_URL, null,
                rListener, eListener);

        AppController.getInstance().addToRequestQueue(cateReq);
    }

    public static void getEvents(){
        AppController.dealController.clear(AppController.getInstance().dealList);

        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                DealFragment.stopRefresh();
                Log.d("Event response", response.toString());
                try {
                    boolean status = response.getBoolean("status");
                    if(status == true) {
                        count_request++;
                        if(count_request == NUM_REQUEST) {
                            MainFeedActivity.pDialog.hide();
                        }

                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Deal d =AppController.dealController.decodeJson(obj);

                            AppController.dealController.addTo(AppController.getInstance().dealList, d);
                        }

                        DealFragment.updateData();
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
                if(!MainFeedActivity.aDialog.isShowing()) {
                    MainFeedActivity.showErrorDialog();
                }
            }
        };

        JsonObjectRequest getAllDealReq = new JsonObjectRequest(Request.Method.GET, Constants.DEAL_ALL_URL, null,
                rListener, eListener);

        AppController.getInstance().addToRequestQueue(getAllDealReq);
    }


    public static void getLiveTVData() {
        String url = Constants.ALL_PRODUCT_URL;

        AppController.productController.clear(AppController.getInstance().onairList);
        AppController.productController.clear(AppController.getInstance().upcomingList);
        AppController.channelController.clear(AppController.getInstance().channelList);

        Response.Listener<JSONArray> rListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                LiveTVFragment.stopRefresh();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        // get json object from json array
                        JSONObject obj = jsonArray.getJSONObject(i);

                        int type = obj.getInt("item_type");
                        if (type == 0) {
                            // decode json to product
                            Product p = AppController.productController.decodeProductJson(obj);
                            // add product to product list
                            AppController.productController.addTo(AppController.getInstance().onairList, p);
                        } else if (type == 1) {
                            // decode json to product
                            Product p = AppController.productController.decodeProductJson(obj);
                            // add product to product list
                            AppController.productController.addTo(AppController.getInstance().upcomingList, p);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                LiveTVFragment.updateData();

//                if (AppController.getInstance().upcomingList.isEmpty() == false
//                        || AppController.getInstance().onairList.isEmpty() == false) {
//                    getChannelData();
//                }
//
//                if (AppController.getInstance().upcomingList.isEmpty() == true
//                        && AppController.getInstance().onairList.isEmpty() == true) {
//                    if(!aDialog.isShowing()) {
//                        showErrorDialog();
//                    }
//                }
            }
        };

        Response.ErrorListener eListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if(!aDialog.isShowing()) {
                    showErrorDialog();
                }
            }
        };

        RequestController.makeGetJsonArrayRequest(url, rListener, eListener);
    }

    public static void getChannelData() {
        String url = Constants.CHANNELS_URL;

        Response.Listener<JSONArray> rListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                count_request++;
                if(count_request == NUM_REQUEST) {
                    MainFeedActivity.pDialog.hide();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        // get json object from json array
                        JSONObject obj = jsonArray.getJSONObject(i);

                        Channel c = AppController.channelController.decodeChannelJson(obj);
                        AppController.channelController.addTo(AppController.getInstance().channelList, c);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (AppController.getInstance().channelList.isEmpty() == false) {

                    LiveTVFragment.updateData();
                    LiveTVFragment.startHandler();

                    count_request++;
                    if(count_request == NUM_REQUEST) {
                        pDialog.hide();
                    }
                } else {
                    getChannelData();
                }
            }
        };

        Response.ErrorListener eListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if(!aDialog.isShowing()) {
                    showErrorDialog();
                }
            }
        };

        RequestController.makeGetJsonArrayRequest(url, rListener, eListener);

    }

    public static void getHotProduct(){
        AppController.productController.clear(AppController.getInstance().hotList);

        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HotFragment.stopRefresh();
                Log.d("Event response", response.toString());
                try {
                    boolean status = response.getBoolean("status");
                    if(status == true) {
                        count_request++;
                        if(count_request == NUM_REQUEST) {
                            MainFeedActivity.pDialog.hide();
                        }

                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Product p = AppController.productController.decodeProductJson(obj);
                            p.setIsHot(true);

                            AppController.productController.addTo(AppController.getInstance().hotList, p);
                        }

                        HotFragment.updateData();
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
                if(!MainFeedActivity.aDialog.isShowing()) {
                    MainFeedActivity.showErrorDialog();
                }
            }
        };

        JsonObjectRequest getHotReq = new JsonObjectRequest(Request.Method.GET, Constants.HOT_PRODUCTS_URL, null,
                rListener, eListener);

        AppController.getInstance().addToRequestQueue(getHotReq);
    }
}
