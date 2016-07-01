package vmc.in.mrecorder.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.parser.Requestor;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.CustomTheme;
import vmc.in.mrecorder.util.JSONParser;
import vmc.in.mrecorder.util.SingleTon;
import vmc.in.mrecorder.util.Utils;

public class Feedback extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, TAG {
    @InjectView(R.id.etfeedback)
    EditText etFeedback;
    @InjectView(R.id.button)
    Button button;
    @InjectView(R.id.root)
    RelativeLayout mroot;
    String feedbackmsg;
    private Toolbar toolbar;
    private String authkey;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomTheme.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.inject(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (android.os.Build.VERSION.SDK_INT > 19) {
            button.setBackgroundResource(R.drawable.button_background);

        }
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        //authkey = getIntent().getExtras().getString(AUTHKEY);
        authkey = Utils.getFromPrefs(this, AUTHKEY, "N/A");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();

                feedbackmsg = etFeedback.getText().toString();
                if (!(feedbackmsg.length() == 0 || feedbackmsg.isEmpty() || feedbackmsg.equals(""))) {

                    UpdateFeedBack();

                } else {
                    Toast.makeText(getApplication(), "Enter FeedBack Message", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }


    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            View view = this.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
               // imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);

            }

        } else {
            // writeToLog("Software Keyboard was not shown");
        }
    }

    //
    public void UpdateFeedBack() {

        if (ConnectivityReceiver.isConnected()) {
            new SubmitUpdateFeedBack().execute();
        } else {
            Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UpdateFeedBack();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(Feedback.this, R.color.accent));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        CallApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }



    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

            Snackbar snackbar = Snackbar
                    .make(mroot, message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    class SubmitUpdateFeedBack extends AsyncTask<Void, Void, String> {
        String message = "n";
        String code = "n";
        JSONObject response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hideKeyboard();

        }


        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                //response = JSONParser.SubmitFeedBack(GET_FEED_BACK_URL, authkey, feedbackmsg);
               response = Requestor.requestFeedback(requestQueue,GET_FEED_BACK_URL, authkey, feedbackmsg);
                Log.d(TAG, response.toString());


                if (response.has(CODE)) {
                    code = response.getString(CODE);

                }
                if (response.has(MESSAGE)) {
                    message = response.getString(MESSAGE);
                }


            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            return code;
        }

        @Override
        protected void onPostExecute(String data) {

            if (data.equals("400")) {
                Toast.makeText(Feedback.this, "Feedback Submitted Sucessfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(Feedback.this, "Server busy! Please Try again Later", Toast.LENGTH_SHORT).show();
            }


        }


    }

}
