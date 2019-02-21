package com.anhnguyen.homeshopping.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.util.ListViewNoRemoveAdapter;

public class WatchRecentActivity extends AppCompatActivity {
    private LinearLayout btn_watchrecent;
    private ListView listView;
    private ListViewNoRemoveAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_recent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load fontawesome
        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        // Remove default app name in top app bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_watchrecent = (LinearLayout) findViewById(R.id.btn_watchrecent);

        // Style top left button
        TextView back_icon = (TextView) btn_watchrecent.getChildAt(0);
        back_icon.setTypeface(fontawesome);
        back_icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        TextView text = (TextView) btn_watchrecent.getChildAt(1);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        // Set event of back to main screen of top left button
        btn_watchrecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // List view
        listView = (ListView) findViewById(R.id.listView);
        // Initialize adapter
        adapter = new ListViewNoRemoveAdapter(this, AppController.getInstance().watchRecentList);
        // Setup adapter
        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        // Set event when click item
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

}
