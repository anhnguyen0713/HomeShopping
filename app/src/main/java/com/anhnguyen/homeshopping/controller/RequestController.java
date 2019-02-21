package com.anhnguyen.homeshopping.controller;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;


public class RequestController {
    public static void makeGetJsonArrayRequest(String url, Response.Listener rListener, Response.ErrorListener eListener){
        // Make request
        JsonArrayRequest arrReq = new JsonArrayRequest(url, rListener, eListener);

        // Add request to request queue
        AppController.getInstance().addToRequestQueue(arrReq);
    }

    public static void makeGetJsonObjRequest(String url, Response.Listener rListener, Response.ErrorListener eListener){
        // Make request
        JsonObjectRequest objReq = new JsonObjectRequest(Request.Method.GET, url, null, rListener, eListener);

        // Add request to request queue
        AppController.getInstance().addToRequestQueue(objReq);
    }
}
