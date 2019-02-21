package com.anhnguyen.homeshopping.controller;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.anhnguyen.homeshopping.model.BaseItem;
import com.anhnguyen.homeshopping.model.Category;
import com.anhnguyen.homeshopping.model.Channel;
import com.anhnguyen.homeshopping.model.Deal;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.model.User;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.util.LruBitmapCache;

import java.util.ArrayList;
import java.util.Locale;

public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();
    public static String currency;

    public static ProductController productController = new ProductController();
    public static ChannelController channelController = new ChannelController();
    public static ExtendProductController extendProductController = new ExtendProductController();
    public static DealController dealController = new DealController();
    public static CategoryController categoryController = new CategoryController();
    public static ProductController notificationController = new ProductController();

    public ArrayList<Product> onairList = new ArrayList<Product>();
    public ArrayList<Product> upcomingList = new ArrayList<Product>();
    public ArrayList<Channel> channelList = new ArrayList<Channel>();
    public ArrayList<Product> favoriteList = new ArrayList<Product>();
    public ArrayList<Product> notiList = new ArrayList<Product>();
    public ArrayList<Product> watchRecentList = new ArrayList<Product>();
    public ArrayList<Deal> dealList = new ArrayList<Deal>();
    public ArrayList<Category> categoryList = new ArrayList<Category>();
    public ArrayList<Product> hotList = new ArrayList<Product>();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;
    private static User mUser;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        if(mUser == null) {
            mUser = User.getInstance();
        }

        // Restore user
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        mUser.setLoggedIn(settings.getBoolean("isLoggedIn", false));
        mUser.setName(settings.getString("username", null));
        mUser.setEmail(settings.getString("email", null));
        mUser.setAvatar(settings.getString("avatar", null));

        Locale current = getResources().getConfiguration().locale;
        if(current.toString().contains("en")){
            currency = Constants.CURRENCY_VIETNAM;
        } else if (current.toString().contains("kr")) {
            currency = Constants.CURRENCY_VIETNAM;
        } else {
            currency = Constants.CURRENCY_VIETNAM;
        }
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequest(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public User getUser(){
        return this.mUser;
    }
}
