package com.holymoly.coronahelper.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.holymoly.coronahelper.LoadingActivity;
import com.holymoly.coronahelper.MainActivity;
import com.holymoly.coronahelper.district.MapTotalActivity;
import com.holymoly.coronahelper.R;


public class NotificationReceiver extends BroadcastReceiver {
    Context context;

    //알림관련 필드
    NotificationCompat.Builder notiBuilder;
    NotificationManager notiManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        notiManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        setNotification(context);
    }


    private void setNotification(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = "코로나서포터";
            String channelNAME = "코로나서포터";
            String channelDESTRICTION = "자가격리구역 이탈 감지 서비스";

            NotificationChannel channel = new NotificationChannel(
                    channelID,
                    channelNAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(channelDESTRICTION);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0,200,200,200});

            notiManager.createNotificationChannel(channel);
            notiBuilder = new NotificationCompat.Builder(context, channelID);

            notiBuilder.setSmallIcon(R.drawable.notification_icon);
            notiBuilder.setWhen(System.currentTimeMillis());
            notiBuilder.setContentTitle("코로나서포터");
            notiBuilder.setContentText("자가격리구역을 이탈했습니다!");
            notiBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
            notiBuilder.setAutoCancel(true);


            Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);
            notiBuilder.setLargeIcon(largeIcon);


            Intent intent = new Intent(context, LoadingActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notiBuilder.setContentIntent(pIntent);

            notiManager.notify(222, notiBuilder.build());
        }
    }
}
