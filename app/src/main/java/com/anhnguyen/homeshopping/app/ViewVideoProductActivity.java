package com.anhnguyen.homeshopping.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.model.Product;

public class ViewVideoProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get position of selected item
        Intent intent = getIntent();
        Product p = (Product) intent.getSerializableExtra("PRODUCT");

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

        // Create ProductInfo fragment
        ProductInfoFragment productInfoFragment = new ProductInfoFragment();

        // Pass product to fragment
        Bundle b = new Bundle();
        b.putSerializable("PRODUCT", p);
        b.putBoolean("HAS_VIDEO", true);
        productInfoFragment.setArguments(b);

        // Create fragment manager
        FragmentManager frmManager = getSupportFragmentManager();
        // Create fragment transaction
        android.support.v4.app.FragmentTransaction transaction = frmManager.beginTransaction();
        // Add ProductInfo fragment to activity
        transaction.add(R.id.frm_info, productInfoFragment);
        transaction.commit();
    }

}
