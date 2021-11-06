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

    private String getTime1() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
        String getTime1 = timeFormat.format(date);

        long getTime2 = Long.parseLong(getTime1);

        if ((getTime2 <= 210) || (getTime2 >= 2311)) {
            return "2300";
        } else if (getTime2 <= 510) {
            return "0200";
        } else if (getTime2 <= 810) {
            return "0500";
        } else if (getTime2 <= 1110) {
            return "0800";
        } else if (getTime2 <= 1410) {
            return "1100";
        } else if (getTime2 <= 1710) {
            return "1400";
        } else if (getTime2 <= 2010) {
            return "1700";
        } else {
            return "2000";
        }
    }

    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String getTime = dateFormat.format(date);

        long right = System.currentTimeMillis();
        Date date1 = new Date(right);
        SimpleDateFormat time1Format = new SimpleDateFormat("HHmm");
        String getTime3 = time1Format.format(date1);

        long getTime4 = Long.parseLong(getTime);
        long getTime5 = Long.parseLong(getTime3);

        if (getTime5 <= 210) {
            long a = (getTime4 - 1);
            String getTime6 = Long.toString(a);
            return getTime6;
        }
        else {
            return getTime;
        }
    }
}