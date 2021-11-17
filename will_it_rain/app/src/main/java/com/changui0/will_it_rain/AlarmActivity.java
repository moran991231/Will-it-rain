package com.changui0.will_it_rain;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {
    MainActivity mainActivity;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mainActivity = MainActivity.mainActivity;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        MyAlarm myAlarm = new MyAlarm(mainActivity);
        myAlarm.readTime();

        Button btn = (Button) findViewById(R.id.timeSave);
        TimePicker timePicker = (TimePicker) findViewById(R.id.time_picker);


        btn.setOnClickListener(v -> {
            int hour = timePicker.getHour(), min = timePicker.getMinute();
            myAlarm.setAlarm(hour, min);
            myAlarm.writeTime();
        });
        btn = (Button) findViewById(R.id.timeCancel);
        btn.setOnClickListener(v -> {
            myAlarm.cancelAlarm();
            myAlarm.setTimeInvalid();
            myAlarm.writeTime();
        });
    }
}
