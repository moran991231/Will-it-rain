package com.example.weather_api_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // the usage of api
        apiTest at = new apiTest();
        int ret = at.isGoingToRain("20211105", "2000", 10, 61, 127);
        System.out.println("@@@@@@@ ret: " + ret);
    }
}