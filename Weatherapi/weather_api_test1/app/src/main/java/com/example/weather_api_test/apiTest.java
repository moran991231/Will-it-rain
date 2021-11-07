package com.example.weather_api_test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class apiTest extends Thread {

    private final String endPoint = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?";
    private final String serviceKey = "1Y66bkrYDIVc22RQE5oIxSR7KAViZKtP4JGd21BwS31M4cYJT%2BC%2B%2F69m0AHecwDar5bZrkYmMePhuiA3Qcay3A%3D%3D";
    private final int ERROR = -1, NOT_POP = -2, MISSING1 = -900, MISSING2 = 900;// POP: probability of precipitation (rainfall)
    private final int ROWS_PER_HOUR = 12;

    private URL makeURL(int pageNo, int numRow, String date, String time, int x, int y) {
        // time should be 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300
        StringBuilder sb = new StringBuilder();
        sb.append(endPoint);
        sb.append("serviceKey=").append(serviceKey);
        sb.append("&pageNo=").append(pageNo);
        sb.append("&numOfRows=").append(numRow);
        sb.append("&dataType=").append("JSON");
        sb.append("&base_date=").append(date);
        sb.append("&base_time=").append(time);
        sb.append("&nx=").append(x);
        sb.append("&ny=").append(y);
        URL url;
        try {
            url = new URL(sb.toString());
        } catch (MalformedURLException e) {
            url = null;
        }
        return url;
    }

    private int getPop(String baseDate, String baseTime, int page, int x, int y) {
        try {
            URL url = makeURL(page, 1, baseDate, baseTime, x, y);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader bufferedReader;
            if (conn.getResponseCode() == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else
                return ERROR;
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            conn.disconnect();

            JSONObject mainObject = new JSONObject(stringBuilder.toString());
            JSONArray itemArray = mainObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");
            JSONObject item = itemArray.getJSONObject(0);
            if (item.getString("category").compareTo("POP") != 0)
                return NOT_POP;
            System.out.printf("%s - %s %s: %s \n", item.getString("fcstDate"), item.getString("fcstTime"), item.getString("category"), item.getString("fcstValue"));
            return Integer.parseInt(item.getString("fcstValue"));

        } catch (Exception e) {
            return ERROR;
        }
    }

    private int ret; // used in getPop() and isGoingToRain()

    private void calculatePops(String baseDate, String baseTime, int duration, int x, int y) {

        ret = -1000;

        int[] pops = new int[duration];
        int offset = 8;
        try {
            for (int i = 0; i < duration; i++) {
                pops[i] = getPop(baseDate, baseTime, ROWS_PER_HOUR * i + offset, x, y);
                if (pops[i] == ERROR) ;
                else if (pops[i] == MISSING1 || pops[i] == MISSING2) ;
                else if (pops[i] == NOT_POP) {
                    offset++;
                    i--;
                }
                // else: do nothing
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Arrays.sort(pops);
        ret = pops[pops.length - 1];
    }

    // use this function to get the max of pop 0~100 (integer)
    public int isGoingToRain(String baseDate, String baseTime, int duration, int x, int y) {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        ex.execute(new Runnable() {
            @Override
            public void run() {
                calculatePops(baseDate, baseTime, duration, x, y);
            }
        });
        return ret;
    }

    public String makeNotificatoinText(int duration, int maxPop){
        String str = "There's an error";
        if(maxPop==ERROR)
            return str;
        str = String.format("향후 %d시간 내의 최대 강수확률은 %d%입니다.", duration, maxPop);
        return str;
    }

    // example code
    private void func() throws IOException, JSONException {
        URL url = makeURL(1, 10, "20211105", "0500", 55, 127);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader bufferedReader;
        if (conn.getResponseCode() == 200) {
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            //connection error :(
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        System.out.println("~~~~~~~~~~~~~~~~~~~");
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        String result = stringBuilder.toString();
        conn.disconnect();
        JSONObject mainObject = new JSONObject(result);
        JSONArray itemArray = mainObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");
        for (int i = 0; i < itemArray.length(); i++) {
            JSONObject item = itemArray.getJSONObject(i);
            String category = item.getString("category");
            String value = item.getString("fcstValue");
            System.out.printf("category: %s, value: %s \n", category, value);
        }
    }

}