package vmc.in.mrecorder.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.adapter.CustomSpinnerAdapter;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.BarModel;
import vmc.in.mrecorder.entity.PieModel;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.util.CustomTheme;
import vmc.in.mrecorder.util.JSONParser;
import vmc.in.mrecorder.util.Utils;

public class AnalyticsByType extends AppCompatActivity implements vmc.in.mrecorder.callbacks.TAG {

    private Toolbar mToolbar;
    private PieChart pieChart;
    private BarChart chart;
    private LinearLayout mainLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout offline;
    private TextView tv_noResponse;
    private RelativeLayout coordinatorLayout;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    private Spinner spinner_nav;
    private ArrayList<PieModel> pieModels;
    private String reportype;
    private JSONObject response;
    private JSONArray records;
    private String count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomTheme.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_by_emp);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        spinner_nav = (Spinner) findViewById(R.id.spinner_nav);
        offline = (RelativeLayout) findViewById(R.id.rl_dummy);
        tv_noResponse = (TextView) findViewById(R.id.tv_noresponse);
        coordinatorLayout = (RelativeLayout) findViewById(R.id.coordi_layout);
        addItemsToSpinner();
        getData();
        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (pieChart != null) {
            mainLayout.removeView(pieChart);
        }
        getData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pieChart != null) {
            mainLayout.removeView(pieChart);
        }
    }

    private void getData() {
        if (Utils.onlineStatus2(AnalyticsByType.this)) {
            new GetPieChartData().execute();
            if (offline.getVisibility() == View.VISIBLE) {
                offline.setVisibility(View.GONE);
            }

        } else {
            if (offline.getVisibility() == View.GONE) {
                offline.setVisibility(View.VISIBLE);
            }
            Snackbar snack = Snackbar.make(coordinatorLayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            getData();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(AnalyticsByType.this, R.color.primary));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }

    }


    public void addItemsToSpinner() {

        ArrayList<String> list = new ArrayList<String>();
        list.add("Daily");
        list.add("Weekly");
        list.add("Monthly");


        // Custom ArrayAdapter with spinner item layout to set popup background

        CustomSpinnerAdapter spinAdapter = new CustomSpinnerAdapter(
                getApplicationContext(), list);


        // Default ArrayAdapter with default spinner item layout, getting some
        // view rendering problem in lollypop device, need to test in other
        // devices

		/*
         * ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_spinner_item, list);
		 * spinAdapter.setDropDownViewResource
		 * (android.R.layout.simple_spinner_dropdown_item);
		 */

        spinner_nav.setAdapter(spinAdapter);

        spinner_nav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                String item = adapter.getItemAtPosition(position).toString();
                reportype=position+"";
                getData();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }



    private void setPieChart(ArrayList<PieModel> pieModel) {
        if (pieChart != null) {
            mainLayout.removeView(pieChart);
        }

        pieChart = new PieChart(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pieChart.setLayoutParams(layoutParams);
        // add pie chart to main layout
        mainLayout.addView(pieChart);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(15);
        pieChart.setTransparentCircleRadius(20);
        pieChart.setUsePercentValues(true);
        // customize legends
        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(9);
        l.setYEntrySpace(7);

       final ArrayList<String> labels = new ArrayList<String>();
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < pieModel.size(); i++) {
            labels.add(pieModel.get(i).getCalltype());
            entries.add(new Entry(Float.valueOf(pieModel.get(i).getCount()), i));
        }

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");
        dataset.setSliceSpace(3);
        dataset.setSelectionShift(7);

        PieData data = new PieData(labels, dataset);
        dataset.setColors(ColorTemplate.JOYFUL_COLORS);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setDescription("Analytics By Calls");

        pieChart.setData(data);

        pieChart.animateY(5000);

        pieChart.saveToGallery("/sd/mychart.jpg", 85); // 85 is the quality of the image

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                Toast.makeText(getApplicationContext(), " " +(int)Math.round(e.getVal())+" "+labels.get(e.getXIndex()) +" calls", Toast.LENGTH_SHORT).show();

                if (e == null)
                    return;
                Log.d("VAL SELECTED",
                        "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                                + ", DataSet index: " + dataSetIndex);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }


    class GetPieChartData extends AsyncTask<Void, Void, ArrayList<PieModel>> {
        private String message, code;


        @Override
        protected ArrayList<PieModel> doInBackground(Void... params) {


            try {
                Log.d("TAG", reportype);
                Log.d("TAG", CallApplication.getInstance().getDeviceId());
                Log.d("TAG", AUTHKEY);

                response = JSONParser.getEmpdata(TYPEREPORT_URL, reportype, CallApplication.getInstance().getDeviceId(), Utils.getFromPrefs(AnalyticsByType.this, AUTHKEY, "n"));
                Log.d("TAG", response.toString());
                Log.d("TAG", Utils.getFromPrefs(AnalyticsByType.this, AUTHKEY, "n"));

                if(response!=null)
                    pieModels = new ArrayList<PieModel>();
                if (response.has(CODE))
                    code = response.getString(CODE);

                if (response.has(RECORDS)) {
                    records = response.getJSONArray(RECORDS);
                    if (records.length() > 0) {

                        for (int i = 0; i < records.length(); i++) {
                            JSONObject jsonobj = records.getJSONObject(i);

                            PieModel pieModel = new PieModel();
                            if (jsonobj.has(CALLTYPEE)) {

                                pieModel.setCalltype(jsonobj.getString(CALLTYPEE).equals("0") ? MISSED :
                                        jsonobj.getString(CALLTYPEE).equals("1") ? INCOMING : OUTGOING);
                            }
                            if (jsonobj.has(COUNT)) {
                                pieModel.setCount(jsonobj.getString(COUNT));
                            }


                            pieModels.add(pieModel);
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return pieModels;
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<PieModel> models) {
            if (models != null &&code!=null) {
                if (code.equals("400")) {
                    if (models.size() > 0) {
                        setPieChart(models);
                    } else {
                        setPieChart(models);
                        Toast.makeText(getApplicationContext(), "No Report Found",
                                Toast.LENGTH_LONG).show();

                    }


                } else if (code.equals("202") || code.equals("401")) {
                    Utils.isLogoutBackground(AnalyticsByType.this);
                }
                else if(code.equals("404")){
                    if (pieChart != null) {
                        mainLayout.removeView(pieChart);
                    }
                    Toast.makeText(getApplicationContext(), "No Records Found",
                            Toast.LENGTH_LONG).show();
                }


            }
        }
    }
}