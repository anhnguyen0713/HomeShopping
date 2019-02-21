package com.anhnguyen.homeshopping.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.toolbox.NetworkImageView;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Channel;
import com.anhnguyen.homeshopping.model.Product;
import com.anhnguyen.homeshopping.util.Constants;
import com.anhnguyen.homeshopping.util.MyBroadcastReceiver;

import java.text.DateFormat;
import java.util.Date;

public class ViewNonVideoProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_non_video_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load fontawesome
        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        // Remove default app name in top app bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        LinearLayout btn_backHome = (LinearLayout)findViewById(R.id.btn_backHome);

        // Style top left button
        TextView back_icon = (TextView)btn_backHome.getChildAt(0);
        back_icon.setTypeface(fontawesome);
        back_icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        TextView text = (TextView)btn_backHome.getChildAt(1);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        // Set event back to main screen of top left button
        btn_backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get intent
        Intent i = getIntent();
        // Get selected product
        final Product p = (Product) i.getExtras().getSerializable("SELECTED_PRODUCT");

        // Set padding top frame layout info
        FrameLayout frm_info = (FrameLayout)findViewById(R.id.frm_info);
        LinearLayout top_upcoming = (LinearLayout) findViewById(R.id.top_upcoming);
        frm_info.setPadding(0, top_upcoming.getHeight(), 0, 0);

        // Create ProductInfo fragment
        ProductInfoFragment productInfoFragment = new ProductInfoFragment();

        // Pass product to fragment
        Bundle b = new Bundle();
        b.putSerializable("PRODUCT", p);
        productInfoFragment.setArguments(b);

        // Create fragment manager
        FragmentManager frmManager = getSupportFragmentManager();
        // Create fragment transaction
        android.support.v4.app.FragmentTransaction transaction = frmManager.beginTransaction();
        // Add ProductInfo fragment to activity
        transaction.add(R.id.frm_info, productInfoFragment);
        transaction.commit();

        // Channel logo
        NetworkImageView img_logo = (NetworkImageView)findViewById(R.id.img_logo);
        Channel c = AppController.channelController.findById(AppController.getInstance().channelList, p.getChannel_id());
        img_logo.setImageUrl(c.getLogo(), AppController.getInstance().getImageLoader());

        // Text remain
        TextView txt_remain = (TextView) findViewById(R.id.txt_remain);
        int current = (int)(System.currentTimeMillis()/1000);
        if(p.getStart_time() == 0 || p.getEnd_time() == 0 || current > p.getEnd_time()){
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            txt_remain.setText(currentDateTimeString);
        } else {
            String remain_time = p.getTimeRemained();
            if(AppController.currency.equals(Constants.CURRENCY_VIETNAM)){
                txt_remain.setText("Còn lại " + remain_time);
            } else if (AppController.currency.equals(Constants.CURRENCY_US)) {
                txt_remain.setText("Remaining " + remain_time);
            }
        }

        // Alarm button
        ToggleButton tglBtn_alarm = (ToggleButton) findViewById(R.id.tglBtn_alarm);
        if(p.getEnd_time() < (int)(System.currentTimeMillis()/1000)) {
            tglBtn_alarm.setVisibility(View.GONE);
        }

        final Product tmp = AppController.notificationController.findById(AppController.getInstance().notiList, p.getId());
        if (tmp != null) {
            tglBtn_alarm.setChecked(true);
        } else {
            tglBtn_alarm.setChecked(false);
        }

        tglBtn_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((ToggleButton) v).isChecked() == true) {
                    AppController.notificationController.addTo(AppController.getInstance().notiList, p);
                    startAlarm(p);
                } else {
                    AppController.notificationController.removeFrom(AppController.getInstance().notiList, tmp);
                    cancelAlarm(p);
                }
            }
        });
    }

    private void startAlarm(Product p){
        long currentSeconds = System.currentTimeMillis()/1000L;
        long startStream = (long)p.getStart_time();
        long wait = startStream - currentSeconds;

        Intent intent = new Intent(ViewNonVideoProductActivity.this, MyBroadcastReceiver.class);
        intent.putExtra("PRODUCT", p);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ViewNonVideoProductActivity.this,
                p.getId()+p.getStart_time(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + wait*1000, pendingIntent);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.alarm_set),
                Toast.LENGTH_LONG).show();
    }

    private void cancelAlarm(Product p){
        Intent intent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                p.getId()+p.getStart_time(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
