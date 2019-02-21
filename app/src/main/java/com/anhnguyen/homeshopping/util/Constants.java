package com.anhnguyen.homeshopping.util;

/**
 * Created by anhnguyen on 15/04/2016.
 * Stores all constants of application
 */
public class Constants {
    // YouTube API key
    public static final String YOUTUBE_KEY = "AIzaSyDbdftMXOiFqtRLTkbUdvGwF21MuITv7ug";

    // URL for requesting all product json array
    public static final String ALL_PRODUCT_URL = "http://homieshoppy.herokuapp.com/api/v2/broadcast";

    // URL for requesting channel json array
    public static final String CHANNELS_URL = "http://homieshoppy.herokuapp.com/api/v1/channels";

    // URL for registering new user
    public static final String REGISTER_URL = "http://homieshoppy.herokuapp.com/api/v2/register";

    // URL for logging in
    // Parameters: username + password
    public static final String LOGIN_URL = "http://homieshoppy.herokuapp.com/api/v2/login";

    // URL of banner image
    public static final String BANNER_URL = "http://www.scj.vn/images/banner/home_top_banner/vi_2377.jpg";

    // URL of searching
    public static final String SEARCH_URL = "http://homieshoppy.herokuapp.com/api/v2/searchByProductName";

    // URL of product description
    // Parameter: product_id
    public static final String DETAIL_URL = "http://homieshoppy.herokuapp.com/api/v1/product/detail/";

    // URL for adding to favorite
    // Parameter: user_id, product_id
    public static final String FAV_ADD_URL = "http://homieshoppy.herokuapp.com/api/v2/favorite/create";

    // URL for removing from favorite
    // Parameter: user_id, product_id
    public static final String FAV_REMOVE_URL = "http://homieshoppy.herokuapp.com/api/v2/favorite/delete";

    // URL for getting all favorite products of user
    // Parameter: user_id
    public static final String FAV_RETRIEVE_URL = "http://homieshoppy.herokuapp.com/api/v2/favorite/list";

    // URL for getting all watch recent products of user
    // Parameter: user_id
    public static final String WR_RETRIEVE_URL = "http://homieshoppy.herokuapp.com/api/v2/recent/list";

    // URL for adding to watch recent
    // Parameter: user_id, product_id
    public static final String WR_ADD_URL = "http://homieshoppy.herokuapp.com/api/v2/recent/create";

    // URL for getting all deals
    public static final String DEAL_ALL_URL = "http://homieshoppy.herokuapp.com/api/v2/event/index";

    // URL for getting products of deal
    // Parameter: event_id
    public static final String DEAL_DETAIL_URL = "http://homieshoppy.herokuapp.com/api/v2/event/product";

    // URL for getting all categories
    public static final String CATEGORY_URL = "http://homieshoppy.herokuapp.com/api/v2/category";

    // URL for getting all products of 1 category
    // Need concatenating category_id
    public static final String CATEGORY_PRODUCTS = "http://homieshoppy.herokuapp.com/api/v2/category/";

    // URL for getting hot product
    public static final String HOT_PRODUCTS_URL = "http://homieshoppy.herokuapp.com/api/v2/hot";

    // URL for price comparison
    // Parameter: [POST] product_id
    public static final String COMPARE_URL = "http://homieshoppy.herokuapp.com/api/v2/compare";

    public static final String FONTAWESOME = "fontawesome-webfont.ttf";

    // URL of default avatar image
    public static final String DEFAULT_AVATAR = "https://cdn4.iconfinder.com/data/icons/gray-user-management/512/rounded-512.png";
    public static final String DEFAULT_AVATAR_2 = "http://data.whicdn.com/images/5979948/large.jpg";

    // Preference tag of user
    public static final String PREFS_NAME = "MyPrefsFile";

    // Currency unit
    public static final String CURRENCY_VIETNAM = "VND";
    public static final String CURRENCY_US = "USD";
    public static final String CURRENCY_KOREA = "KRW";

    // File saving data
    public static final String FILE_FAVORITE = "favorite.ser";
    public static final String FILE_WATCHRECENT = "watchrecent.ser";
    public static final String FILE_NOTIFICATION = "notification.ser";
}
