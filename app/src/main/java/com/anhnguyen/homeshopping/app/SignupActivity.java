package com.anhnguyen.homeshopping.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.User;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.util.CustomRequest;
import com.anhnguyen.homeshopping.util.Helper;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout btn_signup;

    private Button btn_submit_signup;

    private EditText editTxt_email;
    private EditText editTxt_username;
    private EditText editTxt_password;
    private EditText editTxt_repassword;

    private ProgressDialog pDialog;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load fontawesome
        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        // Remove default app name in top app bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_signup = (LinearLayout) findViewById(R.id.btn_signup);

        // Style top left button
        TextView back_icon = (TextView) btn_signup.getChildAt(0);
        back_icon.setTypeface(fontawesome);
        back_icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        TextView text = (TextView) btn_signup.getChildAt(1);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        // Set event back to main screen of top left button
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTxt_email = (EditText) findViewById(R.id.editTxt_email);
        editTxt_username = (EditText) findViewById(R.id.editTxt_username);
        editTxt_password = (EditText) findViewById(R.id.editTxt_password);
        editTxt_repassword = (EditText) findViewById(R.id.editTxt_repassword);

        btn_submit_signup = (Button) findViewById(R.id.btn_submit_signup);
        // When click submit button, do registration
        btn_submit_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Do registration here
        final String email = editTxt_email.getText().toString().trim();
        final String username = editTxt_username.getText().toString().trim();
        final String password = editTxt_password.getText().toString().trim();
        final String repassword = editTxt_repassword.getText().toString().trim();

        // Check empty field
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_empty_field), Toast.LENGTH_LONG).show();
            return;
        }

        // Check validation email
        if (Helper.isValidEmail(email) == false) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_invalid_email), Toast.LENGTH_LONG).show();
            return;
        }

        // Check password and re-password match
        if (!(password.equals(repassword))) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_pw_not_match), Toast.LENGTH_LONG).show();
            return;
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.progress_register));
        pDialog.show();

        this.register(email, username, password);
    }

    public void register(String _email, String _username, String _password) {
        final String email = _email;
        final String username = _username;
        final String password = _password;

        // Handle when registration succeeded
        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Register response", response.toString());

                try {
                    Boolean status = response.getBoolean("status");
                    JSONObject data = response.getJSONObject("data");

                    if(status == false){
                        // Register failed
                        String message = data.getString("message");
                        toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        // Register succeed
                        toast.makeText(getApplicationContext(), getResources().getString(R.string.register_succeed), Toast.LENGTH_LONG).show();
                        AppController.getInstance().getUser().setLoggedIn(true);

                        JSONObject user = data.getJSONObject("user");
                        AppController.getInstance().getUser().setLoggedIn(true);
                        AppController.getInstance().getUser().setId(user.getInt("id"));
                        AppController.getInstance().getUser().setEmail(user.getString("email"));
                        AppController.getInstance().getUser().setName(user.getString("username"));
                        AppController.getInstance().getUser().setAvatar(user.getString("avatar"));

                        // Clear notification list
                        AppController.productController.clear(AppController.getInstance().notiList);

                        // Clear current favorite list
                        AppController.extendProductController.clear(AppController.getInstance().favoriteList);

                        // Clear current watch recent list
                        AppController.extendProductController.clear(AppController.getInstance().watchRecentList);

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

        // Handle when registration failed
        Response.ErrorListener eListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Register error", "Error: " + error.getMessage());
                AppController.getInstance().getUser().setLoggedIn(true);
                pDialog.hide();
                toast.makeText(getApplicationContext(), getResources().getString(R.string.error_register_fail), Toast.LENGTH_LONG).show();
                return;
            }
        };

        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST,
                Constants.REGISTER_URL, null, rListener, eListener) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
