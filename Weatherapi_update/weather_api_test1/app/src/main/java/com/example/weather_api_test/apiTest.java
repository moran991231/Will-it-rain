package com.example.weather_api_test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;



public class apiTest extends Thread{
    public void func() throws IOException, JSONException {
        String endPoint =  "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";
        String serviceKey = "djeHjpQmJi0AcVGpgRiJTye33OLW9G6EemWpzGl0tGfTGm3XqYszCpgFO1HlNxodPByX7N6NZLmug4E4HvNv8g%3D%3D";
        String pageNo = "1";
        String numOfRows = "10";
        String baseDate = getTime(); //초단기 예보로 당일 것만 해당
        String baseTime = getTime1(); // 0500 기준으로 3시간 단위로 검색
        String nx = "61"; //위경도임.
        String ny = "127"; //위경도 정보는 api문서 볼 것.

        String s = endPoint+"getVilageFcst?"+
                "serviceKey="+serviceKey
                +"&pageNo=" + pageNo
                +"&numOfRows=" + numOfRows
                +"+&dataType=JSON"
                + "&base_date=" + baseDate
                +"&base_time="+baseTime
                +"&nx="+nx
                +"&ny="+ny;

        URL url = new URL(s);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader bufferedReader;
        if(conn.getResponseCode() == 200) {
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }else{
            //connection error :(
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            System.out.println(line);
        }
        bufferedReader.close();
        String result= stringBuilder.toString();
        conn.disconnect();
        JSONObject mainObject = new JSONObject(result);
        JSONArray itemArray = mainObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");
        for(int i=0; i<itemArray.length(); i++){
            JSONObject item = itemArray.getJSONObject(i);
            String category = item.getString("category");
            String value = item.getString("fcstValue");
            if(category.equals("POP")){
                System.out.println(category + "  " + value);
            }
        }
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
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH00");
        String getTime1 = timeFormat.format(date);

        switch(getTime1){
            case "0600": case "0700":
                return "0500";
            case "0900": case "1000":
                return "0800";
            case "1200": case "1300":
                return "1100";
            case "1500": case "1600":
                return "1400";
            case "1800": case "1900":
                return "1700";
            case "2100": case "2200":
                return "2000";
            case "2400": case "0100":
                return "2300";
            case "0300": case "0400":
                return "0200";
            default:
                return getTime1;
        }
    }
}