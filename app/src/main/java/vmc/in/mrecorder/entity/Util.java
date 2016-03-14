package vmc.in.mrecorder.entity;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.datahandler.HelperCallRecordings;
import vmc.in.mrecorder.myapplication.CallApplication;

/**
 * Created by gousebabjan on 7/3/16.
 */
public class Util implements TAG {
//    private static ArrayList<Model> list;
//    private Cursor cursor;
//
//    private String phoneNumber, id;
//    private String time;
//    private String filePath;
//    private String callType;
//    private Model model;
//    private HelperCallRecordings hcr;
//    private Cursor c;
//
//
//    public ArrayList<Model> getCalls(Context context) {
//
//        hcr = new HelperCallRecordings(context);
//        c = hcr.display();
//        list = new ArrayList<Model>();
//        c.moveToFirst();
//        while (c.moveToNext()) {
//            model = new Model();
//            phoneNumber = c.getString(c.getColumnIndex("Number"));
//            time = c.getString(c.getColumnIndex("Time"));
//            callType = c.getString(c.getColumnIndex("CallType"));
//            filePath = c.getString(c.getColumnIndex("FilePath"));
//            id = c.getString(c.getColumnIndex("_id"));
//
//            Log.d("number", "" + phoneNumber);
//            if (id != null) {
//                model.setId(id);
//            }
//            if (phoneNumber != null) {
//                model.setPhoneNumber(phoneNumber);
//            }
//            if (time != null) {
//                model.setTime(time);
//            }
//            if (callType != null) {
//                model.setCallType(callType);
//            }
//            if (filePath != null) {
//                model.setFilePath(filePath);
//                model.setFile(new File(filePath));
//            }
//
//
//            list.add(model);
//        }
//
//        return list;
//    }

    public static void setRecording(Context context) {
        CallApplication.sp = context.getApplicationContext().getSharedPreferences("com.example.call", Context.MODE_PRIVATE);

        CallApplication.e = CallApplication.sp.edit();
        final Dialog dialog = new Dialog(context, R.style.myBackgroundStyle);
        dialog.setContentView(R.layout.layout_dialog);
        // dialog.setTitle("Set Your Record Preference");
        dialog.setTitle(Html.fromHtml("<font color='black'>Set Record Preference</font>"));
        RadioGroup group = (RadioGroup) dialog.findViewById(R.id.radioGroup1);
        //  final RelativeLayout rl = (RelativeLayout) dialog.findViewById(R.id.ask_layout);
        final TextView tv1 = (TextView) dialog.findViewById(R.id.r0);
        final TextView tv2 = (TextView) dialog.findViewById(R.id.r1);
        switch (CallApplication.sp.getInt("type", 0)) {
            case 0:
                group.check(R.id.radio0);
                break;

            case 1:
                group.check(R.id.radio1);
                break;


            default:
                break;
        }


        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.radio0:
                        CallApplication.e.putInt("type", 0);
                        // rl.setVisibility(View.GONE);
                        tv1.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.GONE);
                        break;
                    case R.id.radio1:
                        CallApplication.e.putInt("type", 1);
                        // rl.setVisibility(View.GONE);
                        tv1.setVisibility(View.GONE);
                        tv2.setVisibility(View.VISIBLE);
                        break;


                    default:
                        break;
                }
            }
        });
        Button save = (Button) dialog.findViewById(R.id.button1);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CallApplication.e.commit();
                CallApplication.getInstance().resetService();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    ;

}
