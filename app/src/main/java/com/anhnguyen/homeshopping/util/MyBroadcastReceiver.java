package com.anhnguyen.homeshopping.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.support.v4.app.TaskStackBuilder;

import com.anhnguyen.homeshopping.R;
import com.anhnguyen.homeshopping.app.ViewVideoProductActivity;
import com.anhnguyen.homeshopping.controller.AppController;
import com.anhnguyen.homeshopping.model.Product;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private Product p;

    @Override
    public void onReceive(Context context, Intent intent) {
        p = (Product) intent.getSerializableExtra("PRODUCT");

        // Vibrate the mobile phone
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        // Show the notification
        showNotification(context);

        // Remove product from notification list
        Product tmp = AppController.notificationController.findById(AppController.getInstance().notiList, p.getId());
        AppController.notificationController.removeFrom(AppController.getInstance().notiList, tmp);
    }

    private void showNotification(Context context) {
        // prepare intent which is triggered if the
        // notification is selected
        Intent noti_intent = new Intent(context, ViewVideoProductActivity.class);
        noti_intent.putExtra("PRODUCT", p);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
        stackBuilder.addParentStack(ViewVideoProductActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(noti_intent);

        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n = new Notification.Builder(context)
                .setContentTitle(p.getTitle())
                .setContentText("Now LIVE! Watch now!")
                .setSmallIcon(R.mipmap.cart_logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.cart_logo))
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), n);
    }
}
