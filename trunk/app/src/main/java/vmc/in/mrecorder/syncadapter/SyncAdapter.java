/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vmc.in.mrecorder.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.Model;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.service.CallRecorderServiceAll;
import vmc.in.mrecorder.util.Utils;


public class SyncAdapter extends AbstractThreadedSyncAdapter implements TAG {
    public static final String TAG = "SyncAdapter";
    private final ContentResolver mContentResolver;
    private ArrayList<Model> callList;


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }


    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Beginning network synchronization");
        StartOrStopRecording();

        try {
            callList = CallApplication.getWritableDatabase().GetAllCalls();
            for (int i = 0; i < callList.size(); i++) {
                if (!CallRecorderServiceAll.recording && Utils.isLogin(getContext())) {

                    if (new File(callList.get(i).getFilePath()).exists()) {
                        uploadMultipartData(callList.get(i), true);
                    } else {
                        uploadMultipartData(callList.get(i), false);

                    }


                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage().toString());
        }


    }

    private void StartOrStopRecording() {
        if (!Utils.isLogin(getContext())) {
            if (!Utils.isMyServiceRunning(CallRecorderServiceAll.class, getContext())) {
                CallApplication.getInstance().startRecording();
                Log.d("SyncAdapter", "service started");
            } else {
                Log.d("SyncAdapter", "service already started");
            }
        } else {
            if (Utils.isMyServiceRunning(CallRecorderServiceAll.class, getContext())) {
                CallApplication.getInstance().stopRecording();
                Log.d("SyncAdapter", "service stopped");
            } else {
                Log.d("SyncAdapter", "service already stopped");
            }
        }
    }

    private synchronized void uploadMultipartData(Model model, boolean fileExist) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SS");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (fileExist) {
            MediaPlayer mp = MediaPlayer.create(getContext(), Uri.fromFile(model.getFile()));
            int duration = mp.getDuration();
            builder.addPart(UPLOADEDFILE, new FileBody(model.getFile()));
            Log.d(TAG, UPLOADEDFILE + ":" + model.getFile().getName());
            Log.d(TAG, DURATION + ":" + duration + "");
            builder.addPart(DURATION, new StringBody(duration + "", ContentType.TEXT_PLAIN));
            Long time = Long.valueOf(model.getTime()).longValue();
            long endtime = time + duration;
            builder.addPart(ENDTIME, new StringBody(sdf.format(new Date(endtime)), ContentType.TEXT_PLAIN));
            Log.d(TAG, ENDTIME + ":" + sdf.format(new Date(endtime)));
        }
        builder.addPart(AUTHKEY, new StringBody(Utils.getFromPrefs(getContext(), AUTHKEY, "n"), ContentType.TEXT_PLAIN));
        Log.d(TAG, AUTHKEY + ":" + Utils.getFromPrefs(getContext(), AUTHKEY, "n"));
        builder.addPart(DEVICE_ID, new StringBody(CallApplication.getDeviceId(), ContentType.TEXT_PLAIN));
        Log.d(TAG, DEVICE_ID + ":" + CallApplication.getDeviceId());
        builder.addPart(CALLTO, new StringBody(model.getPhoneNumber(), ContentType.TEXT_PLAIN));
        Log.d(TAG, CALLTO + ":" + model.getPhoneNumber());
        builder.addPart(STARTTIME, new StringBody(sdf.format(new Date(Long.parseLong(model.getTime()))), ContentType.TEXT_PLAIN));
        Log.d(TAG, STARTTIME + ":" + sdf.format(new Date(Long.parseLong(model.getTime()))));
        builder.addPart(CALLTYPEE, new StringBody(model.getCallType(), ContentType.TEXT_PLAIN));
        Log.d(TAG, CALLTYPEE + ":" + model.getCallType());
        if (!fileExist) {
            builder.addPart(ENDTIME, new StringBody("0000000", ContentType.TEXT_PLAIN));
            Log.d(TAG, ENDTIME + ":" + "0000000");
        }
        HttpEntity entity = builder.build();

        URL url = null;

        try {
            url = new URL(UPLOAD_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-length", entity.getContentLength() + "");
            urlConnection.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
            OutputStream os = urlConnection.getOutputStream();
            entity.writeTo(urlConnection.getOutputStream());
            os.close();
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String s = "";
            StringBuilder stringBuilder = new StringBuilder("");
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s);
            }
            String serverResponseMessage = stringBuilder.toString();
            String code;
            JSONObject obj = new JSONObject(serverResponseMessage);
            if (obj.has(CODE)) {
                code = obj.getString(CODE);

                if (code.equals("400")) {
                    CallApplication.getWritableDatabase().delete(model.getId());//from db
                    if (new File(model.getFilePath()).exists()) {
                        new File(model.getFilePath()).delete();//from in
                        Log.d(TAG, "FILE DELETED" + ":" + model.getFile().getName());
                    }
                    Log.d(TAG, "RECODRD DELETED" + ":" + model.getFile().getName());
                }

            }


            Log.d(TAG, "Response :" + serverResponseMessage);


        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

    }
}
