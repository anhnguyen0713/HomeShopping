package com.anhnguyen.homeshopping.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.controller.ProductController;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.util.CustomRequest;
import com.anhnguyen.homeshopping.util.ListViewNoRemoveAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private LinearLayout btn_searchTitle;
    private EditText editTxt_search;
    private ListView listView;

    private ListViewNoRemoveAdapter adapter;

    private String keyword;

    private ArrayList<Product> resultList = new ArrayList<>();

    private ProgressDialog pDialog;
    private Toast toast;
    private AlertDialog aDialog;

    private ProductController productController = new ProductController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load fontawesome
        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        // Remove default app name in top app bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get listview
        listView = (ListView) findViewById(R.id.listView);
        // Initialize adapter
        adapter = new ListViewNoRemoveAdapter(this, resultList);
        // Set adapter
        listView.setAdapter(adapter);

        btn_searchTitle = (LinearLayout) findViewById(R.id.btn_searchTitle);

        // Style top left button
        TextView back_icon = (TextView) btn_searchTitle.getChildAt(0);
        back_icon.setTypeface(fontawesome);
        back_icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        TextView text = (TextView) btn_searchTitle.getChildAt(1);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        btn_searchTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTxt_search = (EditText) findViewById(R.id.editTxt_search);

        // When touch search icon
        editTxt_search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTxt_search.getRight() - editTxt_search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        keyword = editTxt_search.getText().toString().trim();
                        if (keyword.matches("")) {
                            toast = new Toast(SearchActivity.this);
                            toast.makeText(SearchActivity.this, "Bạn chưa nhập từ khóa", Toast.LENGTH_LONG).show();

                            return false;
                        }

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editTxt_search.getWindowToken(),
                                InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        searchByKeyword();

                        return true;
                    }
                }
                return false;
            }
        });

        // When press Done
        editTxt_search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    keyword = editTxt_search.getText().toString().trim();
                    if (keyword.matches("")) {
                        toast = new Toast(SearchActivity.this);
                        toast.makeText(SearchActivity.this, "Bạn chưa nhập từ khóa", Toast.LENGTH_LONG).show();

                        return false;
                    }

                    searchByKeyword();

                    return true;
                }
                return false;
            }
        });

        // Set event click list view item
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
    }

    private void searchByKeyword() {
        createSearchDialog();

        resultList.clear();

        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Search response", response.toString());
                hideSearchDialog();

                try {
                    int status = response.getInt("status");
                    switch (status) {
                        case 200:
                            JSONArray data = response.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);

                                Product p = productController.decodeProductJson(obj);
                                p.setStart_time(0);
                                p.setEnd_time(0);

                                resultList.add(p);
                            }
                            if (resultList.isEmpty() == true) {
                                toast.makeText(SearchActivity.this, getResources().getString(R.string.error_no_search_result), Toast.LENGTH_LONG).show();
                                return;
                            }
                            break;
                        case 404:
                            JSONObject _data = response.getJSONObject("data");
                            String message = _data.getString("message");

                            toast.makeText(SearchActivity.this, message, Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }
        };

        Response.ErrorListener eListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                hideSearchDialog();
                createErrorDialog();
            }
        };

        CustomRequest jsonArrReq = new CustomRequest(Request.Method.POST,
                Constants.SEARCH_URL, null, rListener, eListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("keyword", keyword);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonArrReq);
    }

    private void createSearchDialog(){
        pDialog = new ProgressDialog(SearchActivity.this);
        pDialog.show();
        pDialog.setContentView(R.layout.search_dialog);
    }

    private void hideSearchDialog(){
        pDialog.hide();
    }

    private void createErrorDialog() {
        // Set view for alert dialog
        aDialog = new android.support.v7.app.AlertDialog.Builder(SearchActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.error_dialog, null);
        aDialog.setView(layout);

        aDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_btn_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        aDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getResources().getString(R.string.txt_btn_try), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchByKeyword();
            }
        });
        aDialog.show();
    }
}
