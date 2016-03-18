package vmc.in.mrecorder.util;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import vmc.in.mrecorder.callbacks.TAG;

/**
 * Created by gousebabjan on 17/3/16.
 */
public class JSONParser implements TAG{
    static JSONObject jObj = null;

    public static JSONObject login(String url1, String email, String password,String deviceid,String gcmkey) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(EMAIL, email);
        params.put(PASSWORD, password);
        params.put(DEVICE_ID, deviceid);
        params.put(GCM_KEY, gcmkey);

       Log.d("LOGIN",email+" "+password+" "+deviceid+" "+gcmkey);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.d("LOG", result.toString());
        jObj = new JSONObject(result.toString());
        Log.d("RESPONSE",result.toString());
        return jObj;

    }

    public static JSONObject getOTP(String url1, String email,String password) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(EMAIL, email);
        params.put(PASSWORD, password);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        jObj = new JSONObject(result.toString());
        Log.d("RESPONSEOTP",result.toString());
        return jObj;

    }


}