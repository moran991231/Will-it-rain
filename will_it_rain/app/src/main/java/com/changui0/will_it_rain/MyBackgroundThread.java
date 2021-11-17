package com.changui0.will_it_rain;

import android.content.Context;

public class MyBackgroundThread extends Thread {
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        Weather wth = new  Weather();
         MyGps myGps = new  MyGps((MainActivity) context);
        if(! MyGps.isXyValid()){
            myGps.readXy();
        }

//        int pop = wth.isGoingToRain(24,  MyGps.x,  MyGps.y, now);
        int pop = wth.isGoingToRain(24,  60    ,  127, now);
        System.out.println("@@@@@@@ pop: " + pop);

        String str = wth.makeNotificatoinText(24, pop);
         MyNotification myNoti = new  MyNotification(context, (int) now);

        // notification generate
        myNoti.makeNotification("Will it rain?", str);
    }
}
