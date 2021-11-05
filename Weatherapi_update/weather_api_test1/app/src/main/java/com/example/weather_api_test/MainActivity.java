package com.example.weather_api_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Executors;

import java.util.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiTest at = new apiTest();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    at.func();
                }catch(IOException | JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String getTime = dateFormat.format(date);

        return getTime;
    }
    private String getTime1() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd");
        String getTime1 = timeFormat.format(date);

        return getTime1;
    }
}