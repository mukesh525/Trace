package vmc.in.mrecorder.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import vmc.in.mrecorder.R;
import vmc.in.mrecorder.util.Utils;

public class Feedback extends AppCompatActivity {
    @InjectView(R.id.etfeedback)
    EditText etFeedback;
    @InjectView(R.id.button)
    Button button;
    @InjectView(R.id.root)
    RelativeLayout mroot;
    String feedbackmsg;
    private Toolbar toolbar;
    private String authkey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.inject(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        hideKeyboard();
       // authkey = getIntent().getExtras().getString(AUTHKEY);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackmsg = etFeedback.getText().toString();
               // UpdateFeedBack();

            }
        });


    }


    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            View view = this.getCurrentFocus();
            if (view != null) {
                // imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                imm.showSoftInputFromInputMethod(view.getWindowToken(), 0);
            }
        } else {
            // writeToLog("Software Keyboard was not shown");
        }
    }
//
//    public void UpdateFeedBack() {
//
//        if (Utils.onlineStatus2(Feedback.this)) {
//            new SubmitUpdateFeedBack().execute();
//        } else {
//            Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
//                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            UpdateFeedBack();
//
//                        }
//                    })
//                    .setActionTextColor(ContextCompat.getColor(Feedback.this, R.color.accent));
//            View view = snack.getView();
//            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
//            tv.setTextColor(Color.WHITE);
//            snack.show();
//        }
//
//    }
//
//
//    class SubmitUpdateFeedBack extends AsyncTask<Void, Void, String> {
//        String message = "n";
//        String code = "n";
//        JSONObject response = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//
//        @Override
//        protected String doInBackground(Void... params) {
//            // TODO Auto-generated method stub
//
//            try {
//                response = JSONParser.SubmitFeedBack(SEND_FEEDBACK, authkey, feedbackmsg);
//                Log.d("RESPONSE", response.toString());
//
//
//                if (response.has(CODE)) {
//                    code = response.getString(CODE);
//
//                }
//                if (response.has(MESSAGE)) {
//                    message = response.getString(MESSAGE);
//                }
//
//
//            } catch (Exception e) {
//                Log.d("RESPONSE", e.getMessage());
//            }
//            return code;
//        }
//
//        @Override
//        protected void onPostExecute(String data) {
//
//            if (data.equals("400")) {
//                Toast.makeText(FeedBack.this, "Fedback Submitted Sucessfully", Toast.LENGTH_SHORT).show();
//
//            } else {
//                Toast.makeText(FeedBack.this, "Server busy! Please Try again Later", Toast.LENGTH_SHORT).show();
//            }
//
//
//        }
//
//
//    }
}
