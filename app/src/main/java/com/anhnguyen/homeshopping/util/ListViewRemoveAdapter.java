package com.anhnguyen.homeshopping.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.app.FavoriteActivity;
import com.anhnguyen.homeshopping.app.NotificationActivity;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.controller.ChannelController;
import com.anhnguyen.homeshopping.model.Channel;
import com.anhnguyen.homeshopping.model.Product;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewRemoveAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Product> productList;
    private Product p;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ListViewRemoveAdapter(Activity activity, List<Product> productList) {
        this.activity = activity;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int location) {
        return productList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.remove_row, null);
        }

        if (imageLoader == null) {
            imageLoader = AppController.getInstance().getImageLoader();
        }

        NetworkImageView img_thumbnail = (NetworkImageView) convertView.findViewById(R.id.img_thumbnail);
        NetworkImageView img_logo = (NetworkImageView) convertView.findViewById(R.id.img_logo);
        TextView txt_title = (TextView) convertView.findViewById(R.id.txt_title);
        TextView txt_oldPrice = (TextView) convertView.findViewById(R.id.txt_oldPrice);
        TextView txt_newPrice = (TextView) convertView.findViewById(R.id.txt_newPrice);
        ImageView bestseller = (ImageView) convertView.findViewById(R.id.bestseller);

        // getting product data for the row
        p = productList.get(position);

        // best seller
        if(p.getIsHot()) {
            bestseller.setVisibility(View.VISIBLE);
        }

        // thumbnail image
        img_thumbnail.setImageUrl(p.getImage(), imageLoader);

        // logo channel
        Channel c = AppController.channelController.findById(AppController.getInstance().channelList, p.getChannel_id());
        if (c != null) {
            img_logo.setImageUrl(c.getLogo(), imageLoader);
        } else {
            img_logo.setImageUrl("", imageLoader);
        }

        // title
        txt_title.setText(p.getTitle());

        // old price
        if (p.getOld_price() <= p.getNew_price()) {
            txt_oldPrice.setVisibility(View.INVISIBLE);
        } else {
            txt_oldPrice.setText(p.getOld_price(AppController.currency) + AppController.currency);
            txt_oldPrice.setPaintFlags(txt_oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // new price
        if (p.getNew_price() == 0) {
            txt_newPrice.setVisibility(View.INVISIBLE);
        } else {
            txt_newPrice.setText(p.getNew_price(AppController.currency) + AppController.currency);
        }

        // Overlay play button
        ImageView overlay_play = (ImageView) convertView.findViewById(R.id.overlay_play);
        if (p.canWatch()){
            overlay_play.setVisibility(View.VISIBLE);
        } else {
            overlay_play.setVisibility(View.INVISIBLE);
        }

        // remove button
        ImageButton btn_remove = (ImageButton) convertView.findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog aDialpg = new AlertDialog.Builder(activity).create();
                aDialpg.setMessage(activity.getResources().getString(R.string.confirm_remove));
                aDialpg.setButton(DialogInterface.BUTTON_NEUTRAL, activity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                aDialpg.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        productList.remove(p);
                        if (activity instanceof FavoriteActivity) {
                            if (AppController.getInstance().getUser().isLoggedIn() == true) {
                                requestDelFromFav(AppController.getInstance().getUser().getId(), p.getId());
                            } else {
                                AppController.extendProductController.removeFrom(AppController.getInstance().favoriteList, p);
                            }
                        }
                        if (activity instanceof NotificationActivity){

                        }
                        notifyDataSetChanged();
                    }
                });
                aDialpg.show();
            }
        });

        return convertView;
    }

    private void requestDelFromFav(final int user_id, final int product_id) {
        Response.Listener<JSONObject> rListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean status = response.getBoolean("status");
                    if (status == true) {
                        AppController.extendProductController.removeFrom(AppController.getInstance().favoriteList, p);
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

}
