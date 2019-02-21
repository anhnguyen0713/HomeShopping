package com.anhnguyen.homeshopping.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.controller.TypefaceController;
import com.anhnguyen.homeshopping.util.Constants;

public class MyProfileActivity extends AppCompatActivity {
    private LinearLayout btn_myProfile;
    private TextView txt_username;
    private TextView txt_email;
    private Button btn_signup;
    private Button btn_login;
    private Button btn_logout;
    private NetworkImageView img_avatar;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load fontawesome
        Typeface fontawesome = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

        // Remove default app name in top app bar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btn_myProfile = (LinearLayout) findViewById(R.id.btn_myProfile);

        // Style top left button
        TextView back_icon = (TextView) btn_myProfile.getChildAt(0);
        back_icon.setTypeface(fontawesome);
        back_icon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        TextView text = (TextView) btn_myProfile.getChildAt(1);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        // Set event of back to main screen of top left button
        btn_myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get signup button
        btn_signup = (Button) findViewById(R.id.btn_signup);
        // Set listener for signup button
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup_intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(signup_intent);
            }
        });

        // Get username text view
        txt_username = (TextView) findViewById(R.id.txt_username);
        // Get email text view
        txt_email = (TextView) findViewById(R.id.txt_email);
        // Avatar image
        img_avatar = (NetworkImageView) findViewById(R.id.img_avatar);

        // Get login button
        btn_login = (Button) findViewById(R.id.btn_login);
        // Set listener for login button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login_intent);
            }
        });

        // Get logout button
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();

                toast.makeText(MyProfileActivity.this, getResources().getString(R.string.logout_succees), Toast.LENGTH_LONG).show();

                // Start main activity
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        if (AppController.getInstance().getUser().isLoggedIn() == false) {
            setUpNotLoggedIn();
        } else {
            setUpLoggedIn();
        }

        // Button list watch recently
        RelativeLayout btn_wr = (RelativeLayout) findViewById(R.id.btn_wr);
        setValue(btn_wr, AppController.getInstance().watchRecentList.size());
        setTypeface(btn_wr);
        btn_wr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent wr_intent = new Intent(getApplicationContext(), WatchRecentActivity.class);
                startActivity(wr_intent);
            }
        });

        // Button list favorite
        RelativeLayout btn_fav = (RelativeLayout) findViewById(R.id.btn_fav);
        setValue(btn_fav, AppController.getInstance().favoriteList.size());
        setTypeface(btn_fav);
        btn_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fav_intent = new Intent(getApplicationContext(), FavoriteActivity.class);
                startActivity(fav_intent);
            }
        });

        // Button list notification
        RelativeLayout btn_noti = (RelativeLayout) findViewById(R.id.btn_noti);
        setValue(btn_noti, AppController.getInstance().notiList.size());
        setTypeface(btn_noti);
        btn_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent noti_intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(noti_intent);
            }
        });
    }

    private void logout() {
        AppController.getInstance().getUser().setLoggedIn(false);
        AppController.getInstance().getUser().setId(-1);
        AppController.getInstance().getUser().setEmail(null);
        AppController.getInstance().getUser().setName(null);
        AppController.getInstance().getUser().setAvatar(null);

        // Clear favorite list
        AppController.extendProductController.clear(AppController.getInstance().favoriteList);

        // Clear watch recent list
        AppController.extendProductController.clear(AppController.getInstance().watchRecentList);

        // Clear noti list
        AppController.extendProductController.clear(AppController.getInstance().notiList);
    }

    private void setUpNotLoggedIn() {
        // Set username
        txt_username.setText(R.string.txt_username);

        // Set email
        txt_email.setText(R.string.txt_email);

        // Set default avatar
        img_avatar.setImageUrl(Constants.DEFAULT_AVATAR, AppController.getInstance().getImageLoader());

        // Show login button
        btn_login.setVisibility(View.VISIBLE);

        // Show signup button
        btn_signup.setVisibility(View.VISIBLE);

        // Hide logout button
        btn_logout.setVisibility(View.INVISIBLE);
    }

    private void setUpLoggedIn() {
        // Set username
        txt_username.setText(AppController.getInstance().getUser().getName());

        // Set email
        txt_email.setText(AppController.getInstance().getUser().getEmail());

        // Set avatar
        img_avatar.setImageUrl(Constants.DEFAULT_AVATAR_2, AppController.getInstance().getImageLoader());

        // Hide login button
        btn_login.setVisibility(View.INVISIBLE);

        // Hide signup button
        btn_signup.setVisibility(View.INVISIBLE);

        // Show logout button
        btn_logout.setVisibility(View.VISIBLE);
    }

    private void setValue(RelativeLayout button, int value) {
        TextView t = (TextView) button.getChildAt(1);
        t.setText(Integer.toString(value));
    }

    private void setTypeface(RelativeLayout button) {
        TextView t = (TextView) button.getChildAt(2);
        t.setTypeface(TypefaceController.createTypeface(this, Constants.FONTAWESOME));
    }
}
