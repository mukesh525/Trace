package vmc.in.mrecorder.parser;

import android.content.pm.PackageInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;

/**
 * Created by gousebabjan on 29/6/16.
 */
public class Requestor implements vmc.in.mrecorder.callbacks.TAG {

    public static JSONObject requestOTP(RequestQueue requestQueue, String url, final String email, final String password) {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(EMAIL, email);
                params.put(PASSWORD, password);
                return params;
            }
        };

        requestQueue.add(request);
        try {
            resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            response = new JSONObject(resp);
        } catch (InterruptedException e) {
            //L.m(e + "");
        } catch (ExecutionException e) {
            // L.m(e + "");
        } catch (TimeoutException e) {
            //L.m(e + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response != null)
            Log.d(TAG, response.toString());
        return response;
    }

    public static JSONObject requestRating(RequestQueue requestQueue, final String authey, String url, final String rateValue, final String title, final String desc, final String callid) {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AUTHKEY, authey);
                params.put(RATING, rateValue);
                params.put(RATING_TITLE, title);
                params.put(COMMENT, desc);
                params.put(CALLID, callid);
                return params;
            }
        };

        requestQueue.add(request);
        try {
            resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            response = new JSONObject(resp);
        } catch (InterruptedException e) {
            //L.m(e + "");
        } catch (ExecutionException e) {
            // L.m(e + "");
        } catch (TimeoutException e) {
            //L.m(e + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response != null)
            Log.d(TAG, response.toString());
        return response;
    }

    public static JSONObject requestSeen(RequestQueue requestQueue, String url, final String authkey, final String callid) {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AUTHKEY, authkey);
                params.put(CALLID, callid);
                return params;
            }
        };

        requestQueue.add(request);
        try {
            resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            response = new JSONObject(resp);
        } catch (InterruptedException e) {
            //L.m(e + "");
        } catch (ExecutionException e) {
            // L.m(e + "");
        } catch (TimeoutException e) {
            //L.m(e + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response != null)
            Log.d(TAG, response.toString());
        return response;
    }


    public static JSONObject requestLogin(RequestQueue requestQueue, String url, final String email, final String password, final String deviceid, final String gcmkey, final String model) {

        JSONObject response = null;
        String resp = null;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(EMAIL, email);
                params.put(PASSWORD, password);
                params.put(DEVICE_ID, deviceid);
                params.put(GCM_KEY, gcmkey);
                params.put(DEVICE, model);
               // params.put(APP_VERSION,  CallApplication.getInstance().appVersion());
                return params;
            }
        };

        requestQueue.add(request);
        try {
            resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            response = new JSONObject(resp);
        } catch (InterruptedException e) {
            //L.m(e + "");
        } catch (ExecutionException e) {
            // L.m(e + "");
        } catch (TimeoutException e) {
            //L.m(e + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response != null)
            Log.d(TAG, response.toString());
        return response;
    }


    public static JSONObject requestByEMP(RequestQueue requestQueue, String url, final String reporttype, final String deviceid, final String authkey) {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(REPORTTYPE, reporttype);
                params.put(DEVICE_ID, deviceid);
                params.put(AUTHKEY, authkey);
                return params;
            }
        };

        requestQueue.add(request);
        try {
            resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            response = new JSONObject(resp);
        } catch (InterruptedException e) {
            //L.m(e + "");
        } catch (ExecutionException e) {
            // L.m(e + "");
        } catch (TimeoutException e) {
            //L.m(e + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response != null)
            Log.d(TAG, response.toString());
        return response;
    }

    public static JSONObject requestByType(RequestQueue requestQueue, String url, final String reporttype, final String deviceid, final String authkey) {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(REPORTTYPE, reporttype);
                params.put(DEVICE_ID, deviceid);
                params.put(AUTHKEY, authkey);
                return params;
            }
        };

        requestQueue.add(request);
        try {
            resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            response = new JSONObject(resp);
        } catch (InterruptedException e) {
            //L.m(e + "");
        } catch (ExecutionException e) {
            // L.m(e + "");
        } catch (TimeoutException e) {
            //L.m(e + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response != null)
            Log.d(TAG, response.toString());
        return response;
    }


    public static JSONObject requestGetCalls(RequestQueue requestQueue, String url, final String authKey, final String limit, final String offset, final String deviceid, final String type) {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AUTHKEY, authKey);
                params.put(TYPE, type);
                params.put(OFFSET, offset);
                params.put(LIMIT, limit);
                params.put(DEVICE_ID, deviceid);
                String ver=CallApplication.getInstance().appVersion();
                params.put(APP_VERSION, ver);
                Log.d("VER",ver);
                return params;
            }
        };

        requestQueue.add(request);
        try {
            resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            response = new JSONObject(resp);
        } catch (InterruptedException e) {
            Log.d("EXCEPTION", e.getMessage().toString());
        } catch (ExecutionException e) {
            Log.d("EXCEPTION", e.getMessage().toString());
        } catch (TimeoutException e) {
            Log.d("EXCEPTION", e.getMessage().toString());
        } catch (JSONException e) {
            Log.d("EXCEPTION", e.getMessage().toString());
        }
        if (response != null)
            Log.d(TAG, response.toString());
        return response;
    }

    public static JSONObject requestGetRating(RequestQueue requestQueue, String url, final String authKey, final String callid) {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AUTHKEY, authKey);
                params.put(CALLID, callid);

                return params;
            }
        };

        requestQueue.add(request);
        try {
            resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            response = new JSONObject(resp);
        } catch (InterruptedException e) {
            //L.m(e + "");
        } catch (ExecutionException e) {
            // L.m(e + "");
        } catch (TimeoutException e) {
            //L.m(e + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response != null)
            Log.d(TAG, response.toString());
        return response;
    }


    public static JSONObject requestFeedback(RequestQueue requestQueue, String url, final String authkey, final String message) {
        JSONObject response = null;
        String resp;
        RequestFuture<String> requestFuture = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.POST, url, requestFuture, requestFuture) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AUTHKEY, authkey);
                params.put(FEEDBACK, message);
                return params;
            }
        };

        requestQueue.add(request);
        try {
            resp = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            response = new JSONObject(resp);
        } catch (InterruptedException e) {
            //L.m(e + "");
        } catch (ExecutionException e) {
            // L.m(e + "");
        } catch (TimeoutException e) {
            //L.m(e + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (response != null)
            Log.d(TAG, response.toString());
        return response;
    }


}
