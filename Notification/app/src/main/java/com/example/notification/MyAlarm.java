package com.example.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.media.RingtoneManager;
import android.os.Build;


import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class MyAlarm extends BroadcastReceiver {
    public static final String fileName = "alarmTime.txt";
    public static int hour = -1, min = -1;
    private MainActivity mainActivity;
    private String channelId = "alarm_channel";
    private static int times;
    AlarmManager alarmManager;

    public MyAlarm() {
    }

    public MyAlarm(MainActivity m) {
        mainActivity = m;
        alarmManager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent busRouteIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(busRouteIntent);
        PendingIntent busRoutePendingIntent =
                stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long now = System.currentTimeMillis();
        String str = new SimpleDateFormat("yyyy MM dd HH:mm| ").format(new Date(now));
        // notification generate
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher).setDefaults(Notification.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setContentTitle("메롱")
                .setContentText(str + times + "번째")
                .setContentIntent(busRoutePendingIntent);

        times++;
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Rainfall", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        int id = (int) System.currentTimeMillis();

        notificationManager.notify(id, notificationBuilder.build());
    }

    public void setAlarm(int hour, int minute) {
        this.hour = hour;
        this.min = minute;
        if (alarmManager == null) return;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        if (calendar.before(Calendar.getInstance()))
            calendar.add(Calendar.DATE, 1);

        PendingIntent alarmIntent = getAlamPIntent(1);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
        String str = String.format("알림이 %d시 %d분에 저장되었습니다.", hour, minute);
        Toast.makeText(mainActivity, str, Toast.LENGTH_LONG).show();

    }

    public void cancelAlarm() {
        if (alarmManager == null) return;
        alarmManager.cancel(getAlamPIntent(1));
    }

    public PendingIntent getAlamPIntent(int reqCode) {
        Intent intent = new Intent(mainActivity, MyAlarm.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent ret = PendingIntent.getBroadcast(mainActivity, reqCode, intent, flags);
        return ret;
    }

    public static boolean isTimeValid() {
        if (0 <= hour && hour <= 23 && 0 <= min && min <= 59) return true;
        return false;
    }

    public void setTimeInvalid() {
        hour = min = -1;
    }

    public void writeTime() {
        try {
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(mainActivity.openFileOutput(fileName, Context.MODE_PRIVATE));
            oStreamWriter.write(String.format("%d %d", hour, min));
            oStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readTime() {
        try {
            InputStream iStream = mainActivity.openFileInput(fileName);
            if (iStream != null) {
                InputStreamReader iStreamReader = new InputStreamReader(iStream);
                BufferedReader bufferedReader = new BufferedReader(iStreamReader);
                StringTokenizer st = new StringTokenizer(bufferedReader.readLine());
                hour = Integer.parseInt(st.nextToken());
                min = Integer.parseInt(st.nextToken());
                iStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
