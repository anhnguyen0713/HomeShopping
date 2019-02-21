package com.anhnguyen.homeshopping.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.util.ConnectionDetector;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.util.CustomRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductInfoFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    private boolean hasVideo = false;
    private Product p;

    private ProgressDialog pDialog;
    private Toast toast;

    private ToggleButton tglBtn_fav;
    private WebView webView_description;

    public ProductInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.p = (Product) getArguments().getSerializable("PRODUCT");
        this.hasVideo = getArguments().getBoolean("HAS_VIDEO");

        // Check if product already in watch recent list
        if(AppController.extendProductController.findById(AppController.getInstance().watchRecentList, p.getId()) == null) {
            // Add product to watch recent list
            if(AppController.getInstance().getUser().isLoggedIn() == true){
                System.out.println("User id "+ AppController.getInstance().getUser().getId()+"");
                requestAddToWR(AppController.getInstance().getUser().getId(), p.getId());
            } else {
                AppController.extendProductController.addTo(AppController.getInstance().watchRecentList, p);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get product description
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage(getResources().getString(R.string.progress_loading));
        pDialog.show();
        pDialog.setCanceledOnTouchOutside(false);

        // Make request to get description
        String url = Constants.DETAIL_URL + p.getId();

        JsonObjectRequest objReq = new JsonObjectRequest(Request.Method.GET, url,
                null, this, this);

        AppController.getInstance().addToRequestQueue(objReq);

        if (hasVideo == true) {
            // Create Video fragment
            ProductVideoFragment videoFragment = new ProductVideoFragment();

            // Pass video link to fragment
            Bundle b = new Bundle();
            Product tmp = AppController.productController.findById(AppController.getInstance().onairList, p.getId());
            if(tmp != null){
                if(tmp.getVideo_link().matches("")||tmp.getVideo_link().equals(null)) {
                    // stream link
                    b.putString("VIDEO_LINK", "-dK68fytu5w");
                } else {
                    // video link
                    b.putString("VIDEO_LINK", extractYoutubeVideoId(p.getVideo_link()));
                }
            } else {
                // video link
                b.putString("VIDEO_LINK", extractYoutubeVideoId(p.getVideo_link()));
            }
            videoFragment.setArguments(b);

            // Add fragment
            FragmentManager videoFragMan = getChildFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = videoFragMan.beginTransaction();
            transaction.add(R.id.frm_visual, videoFragment);
            transaction.commit();
        } else {
            // Create image fragment
            ProductImageFragment imageFragment = new ProductImageFragment();

            // Pass image link to fragment
            Bundle b = new Bundle();
            b.putString("IMAGE_LINK", p.getImage());
            imageFragment.setArguments(b);

            // Add fragment
            FragmentManager imageFragMan = getChildFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = imageFragMan.beginTransaction();
            transaction.add(R.id.frm_visual, imageFragment);
            transaction.commit();
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_info, container, false);

        // Set title
        TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setText(p.getTitle());

        // Set old price
        TextView txt_oldPrice = (TextView) view.findViewById(R.id.txt_oldPrice);
        if (p.getOld_price() <= p.getNew_price()) {
            txt_oldPrice.setVisibility(View.GONE);
        } else {
            txt_oldPrice.setText(p.getOld_price(AppController.currency)+AppController.currency);
            txt_oldPrice.setPaintFlags(txt_oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Set new price
        TextView txt_newPrice = (TextView) view.findViewById(R.id.txt_newPrice);
        txt_newPrice.setText(p.getNew_price(AppController.currency)+AppController.currency);

        // Show time area
        LinearLayout time_area = (LinearLayout) view.findViewById(R.id.layout_time);
        int current = (int)(System.currentTimeMillis()/1000);
        if(p.getStart_time() == 0 || p.getEnd_time() == 0 || current > p.getEnd_time()){
            time_area.setVisibility(View.GONE);
        } else {
            // Set show time
            TextView txt_showTime = (TextView) view.findViewById(R.id.txt_showTime);
            String showTime = p.getTime("dd/MM/yyyy HH:mm", "HH:mm");
            txt_showTime.setText(showTime);
        }

        // Get detail button
        Button btn_detail = (Button) view.findViewById(R.id.btn_detail);
        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getLink()));
                startActivity(browserIntent);
            }
        });

        // Buy button
        Button btn_buy = (Button) view.findViewById(R.id.btn_buy);
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getLink()));
                startActivity(browserIntent);
            }
        });

        // Description web view
        webView_description = (WebView) view.findViewById(R.id.webView_description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView_description.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            webView_description.getSettings().setLoadWithOverviewMode(true);
            webView_description.getSettings().setUseWideViewPort(true);
        } else {
            webView_description.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        // Favorite button
        tglBtn_fav = (ToggleButton) view.findViewById(R.id.tglBtn_favorite);
        if (AppController.extendProductController.findById(AppController.getInstance().favoriteList, p.getId()) == null) {
            tglBtn_fav.setChecked(false);
        } else {
            tglBtn_fav.setChecked(true);
        }

        ConnectionDetector cd = new ConnectionDetector(getContext());
        if (cd.isConnectingToInternet() == false) {
            tglBtn_fav.setEnabled(false);
        } else {
            tglBtn_fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) { // add product into favorite
                        if (AppController.getInstance().getUser().isLoggedIn() == true) {
                            requestAddToFav(AppController.getInstance().getUser().getId(), p.getId());
                        } else {
                            AppController.extendProductController.addTo(AppController.getInstance().favoriteList, p);
                            toast.makeText(getContext(), getResources().getString(R.string.fav_add), Toast.LENGTH_LONG).show();
                        }

                    } else { // remove product from favorite
                        if (AppController.getInstance().getUser().isLoggedIn() == true) {
                            requestDelFromFav(AppController.getInstance().getUser().getId(), p.getId());
                        } else {
                            Product tmp = AppController.productController.findById(AppController.getInstance().favoriteList, p.getId());
                            AppController.extendProductController.removeFrom(AppController.getInstance().favoriteList, tmp);
                            toast.makeText(getContext(), getResources().getString(R.string.fav_remove), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }

        // Compare price button
        Button btn_compare = (Button) view.findViewById(R.id.btn_compare);
        btn_compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ComparePriceActivity.class);
                i.putExtra("PRODUCT", p);
                startActivity(i);
            }
        });

        return view;
    }

    private String extractYoutubeVideoId(String ytUrl) {

        String vId = null;

        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);

        if(matcher.find()){
            vId= matcher.group();
        }
        return vId;
    }

    @Override
    public void onResponse(JSONObject response) {
        pDialog.hide();
        Log.d("Description", response.toString());
        try {
            int status = response.getInt("status");
            if (status == 400) {
                // product not found
                toast.makeText(getContext(), getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
            } else if (status == 200) {
                String desc = response.getString(AppController.productController.TAG_DESCRIPTION);
                webView_description.loadData(desc, "text/html; charset=utf-8", "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        pDialog.hide();
        toast.makeText(getContext(), getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
    }

    private void requestAddToFav(final int user_id, final int product_id) {
        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");
                    if (status == true) {
                        AppController.extendProductController.addTo(AppController.getInstance().favoriteList, p);
                        toast.makeText(getContext(), getResources().getString(R.string.fav_add), Toast.LENGTH_LONG).show();
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
                toast.makeText(getContext(), getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
                tglBtn_fav.setEnabled(false);
            }
        };

        CustomRequest addReq = new CustomRequest(Request.Method.POST, Constants.FAV_ADD_URL,
                null, rListener, eListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Integer.toString(user_id));
                params.put("product_id", Integer.toString(product_id));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(addReq);
    }

    private void requestDelFromFav(final int user_id, final int product_id) {
        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");
                    if (status == true) {
                        Product tmp = AppController.productController.findById(AppController.getInstance().favoriteList, p.getId());
                        AppController.extendProductController.removeFrom(AppController.getInstance().favoriteList, tmp);
                        toast.makeText(getContext(), getResources().getString(R.string.fav_remove), Toast.LENGTH_LONG).show();
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
                toast.makeText(getContext(), getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
                tglBtn_fav.setEnabled(false);
            }
        };

        CustomRequest delReq = new CustomRequest(Request.Method.POST, Constants.FAV_REMOVE_URL,
                null, rListener, eListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Integer.toString(user_id));
                params.put("product_id", Integer.toString(product_id));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(delReq);
    }

    private void requestAddToWR(final int user_id, final int product_id){
        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("WR", response.toString());
                try {
                    boolean status = response.getBoolean("status");
                    if (status == true) {
                        AppController.extendProductController.addTo(AppController.getInstance().watchRecentList, p);
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

        CustomRequest addReq = new CustomRequest(Request.Method.POST, Constants.WR_ADD_URL,
                null, rListener, eListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", Integer.toString(user_id));
                params.put("product_id", Integer.toString(product_id));

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(addReq);
    }
}
