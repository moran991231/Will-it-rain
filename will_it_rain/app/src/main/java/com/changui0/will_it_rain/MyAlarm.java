package com.changui0.will_it_rain;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.StringTokenizer;

public class MyAlarm extends BroadcastReceiver {
    public static final String fileName = "alarmTime.txt";
    public static int hour = -1, min = -1;
    private Context context;
    private String channelId = "alarm_channel";
    private final String channelName = "Probability of Rainfall"; // don't change
    private static int times;
    private AlarmManager alarmManager;

    public MyAlarm() {
    }

    public MyAlarm(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm", "@@@@@@ Alram begin @@@@@");
        MyBackgroundThread th = new MyBackgroundThread();
        th.setContext(context);
        th.start();
        Log.d("Alarm", "@@@@@@ Alram end @@@@@");
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
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();

    }

    public void cancelAlarm() {
        if (alarmManager == null) return;
        alarmManager.cancel(getAlamPIntent(1));
        Toast.makeText(context, "Alarm is canceled", Toast.LENGTH_LONG).show();
    }

    public PendingIntent getAlamPIntent(int reqCode) {
        Intent intent = new Intent(context, MyAlarm.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent ret = PendingIntent.getBroadcast(context, reqCode, intent, flags);
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
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            oStreamWriter.write(String.format("%d %d", hour, min));
            oStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readTime() {
        try {
            InputStream iStream = context.openFileInput(fileName);
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
