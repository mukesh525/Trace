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
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.datahandler.HelperCallRecordings;
import vmc.in.mrecorder.entity.Model;
import vmc.in.mrecorder.entity.Util;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.service.CallRecorderServiceAll;


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

    private synchronized void doFileUpload(Model model) {


        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String responseFromServer = "";
        //  String urlString = "http://mywebsite.com/directory/upload.php";

        try {

            //------------------ CLIENT REQUEST
            FileInputStream fileInputStream = new FileInputStream(new File(model.getFilePath()));
            // open a URL connection to the Servlet
            URL url = new URL(UPLOAD_URL);
            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) url.openConnection();
            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + model.getFilePath() + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // close streams
            Log.e("Debug", "File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            Log.e("Debug", "error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }

        //------------------ read the SERVER RESPONSE
        try {
            dis = new DataInputStream(conn.getInputStream());
            StringBuilder response = new StringBuilder();

            String line;
            while ((line = dis.readLine()) != null) {
                response.append(line).append('\n');
            }

            responseFromServer = response.toString();
            if (responseFromServer.contains("has been uploaded")) {

                CallApplication.getWritableDatabase().delete(model.getId());
                if (new File(model.getFilePath()).exists()) {
                    new File(model.getFilePath()).delete();
                }
            }
            Log.d("Debug", responseFromServer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (dis != null) try {
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Beginning network synchronization");
        callList = CallApplication.getWritableDatabase().GetAllCalls();
        for (int i = 0; i < callList.size(); i++) {
            if (!CallRecorderServiceAll.recording) {
                if (new File(callList.get(i).getFilePath()).exists()) {
                    doFileUpload(callList.get(i));
                } else {
                    //CallApplication.getWritableDatabase().delete(callList.get(i).getId());

                }


            }


        }

    }


}
