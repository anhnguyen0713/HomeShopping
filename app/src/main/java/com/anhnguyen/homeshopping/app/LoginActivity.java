package com.anhnguyen.homeshopping.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.util.CustomRequest;
import com.anhnguyen.homeshopping.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout btn_login;

    private Button btn_submit_login;

    private EditText editTxt_email;
    private EditText editTxt_password;

    private TextView txt_forgotPw;

    private ProgressDialog pDialog;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default app name in top app bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Load fontawesome
        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        btn_login = (LinearLayout) findViewById(R.id.btn_login);

        // Style top left button
        TextView back_icon = (TextView) btn_login.getChildAt(0);
        back_icon.setTypeface(fontawesome);
        back_icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        TextView text = (TextView) btn_login.getChildAt(1);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_submit_login = (Button) findViewById(R.id.btn_submit_login);
        btn_submit_login.setOnClickListener(this);

        editTxt_email = (EditText) findViewById(R.id.editTxt_email);
        editTxt_password = (EditText) findViewById(R.id.editTxt_password);

        txt_forgotPw = (TextView) findViewById(R.id.txt_forgotPassword);
        txt_forgotPw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_submit_login) {
            // Do login here
            final String email = editTxt_email.getText().toString().trim();
            final String password = editTxt_password.getText().toString().trim();

            // Check empty field
            if (email.isEmpty() || password.isEmpty()) {
                toast.makeText(getApplicationContext(), getResources().getString(R.string.error_empty_field), Toast.LENGTH_LONG).show();
                return;
            }

            // Check validation email
            if (Helper.isValidEmail(email) == false) {
                toast.makeText(getApplicationContext(), getResources().getString(R.string.error_invalid_email), Toast.LENGTH_LONG).show();
                return;
            }

            pDialog = new ProgressDialog(this);
            pDialog.setMessage(getResources().getString(R.string.progress_login));
            pDialog.show();

            this.login(email, password);
        } else if (v == txt_forgotPw) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.func_not_complete), Toast.LENGTH_LONG).show();
        }

    }

    private void login(String _email, String _password) {
        final String email = _email;
        final String password = _password;

        // Handle when request succeeded
        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Login response", response.toString());
                pDialog.hide();
                try {
                    JSONObject data = response.getJSONObject("data");
                    Boolean status = response.getBoolean("status");
                    if (status == false) {
                        // Login failed
                        String message = data.getString("message");
                        toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        // Login success
                        toast.makeText(getApplicationContext(), getResources().getString(R.string.login_succeed), Toast.LENGTH_LONG).show();

                        JSONObject user = data.getJSONObject("user");

                        AppController.getInstance().getUser().setLoggedIn(true);
                        AppController.getInstance().getUser().setId(user.getInt("id"));
                        AppController.getInstance().getUser().setEmail(user.getString("email"));
                        AppController.getInstance().getUser().setName(user.getString("username"));
                        AppController.getInstance().getUser().setAvatar(user.getString("avatar"));

                        // Clear current favorite list
                        AppController.extendProductController.clear(AppController.getInstance().favoriteList);

                        // Get new favorite list
                        getFavList(AppController.getInstance().getUser().getId());

                        // Clear current watch recent list
                        AppController.extendProductController.clear(AppController.getInstance().watchRecentList);

                        // Get new watch recent list
                        getWacthRecentList(AppController.getInstance().getUser().getId());

                        // Clear notification list
                        AppController.productController.clear(AppController.getInstance().notiList);

                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Handle when request failed
        Response.ErrorListener eListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Login error", "Error: " + error.getMessage());
                pDialog.hide();
                toast.makeText(getApplicationContext(), getResources().getString(R.string.error_login_fail), Toast.LENGTH_LONG).show();
                return;
            }
        };

        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST,
                Constants.LOGIN_URL, null, rListener, eListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void getFavList(final int user_id) {
        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Get fav", response.toString());
                try{
                    Boolean status = response.getBoolean("status");
                    if(status == true){
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);

                            Product p = AppController.productController.decodeProductJson(obj);

                            AppController.extendProductController.addTo(AppController.getInstance().favoriteList, p);
                        }
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

        CustomRequest getReq = new CustomRequest(Request.Method.POST, Constants.FAV_RETRIEVE_URL, null,
                rListener, eListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Integer.toString(user_id));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(getReq);
    }

    private void getWacthRecentList(final int user_id){
        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Get wr", response.toString());
                try{
                    Boolean status = response.getBoolean("status");
                    if(status == true){
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);

                            Product p = AppController.productController.decodeProductJson(obj);

                            AppController.extendProductController.addTo(AppController.getInstance().watchRecentList, p);
                        }
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

        CustomRequest getReq = new CustomRequest(Request.Method.POST, Constants.WR_RETRIEVE_URL, null,
                rListener, eListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Integer.toString(user_id));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(getReq);
    }
}
