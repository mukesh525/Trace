package vmc.in.mrecorder.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.gcm.GCMClientManager;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.util.utils;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button btn_login;
    TextView link_signup, link_forgot_password;
    String PROJECT_NUMBER = "26701829862";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = (Button) findViewById(R.id.btn_login);
        link_signup = (TextView) findViewById(R.id.link_signup);
        link_forgot_password = (TextView) findViewById(R.id.link_forgot);
        btn_login.setOnClickListener(this);
        link_signup.setOnClickListener(this);
        link_forgot_password.setOnClickListener(this);

        GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                Log.d("Registration id", registrationId);
                if (isNewRegistration) {
                    onRegisterGcm(registrationId);
                }
            }


        });
    }

    public void onRegisterGcm(final String regid) {

        if (utils.onlineStatus2(Login.this)) {
            new RegisterGcm(regid).execute();
        } else {
            Toast.makeText(Login.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    class RegisterGcm extends AsyncTask<Void, Void, String> {
        private String regid;

        public RegisterGcm(String regid) {
            this.regid = regid;
        }

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                //startActivity(new Intent(getApplicationContext(), Home.class));
               // finish();
                Log.d("android_id", CallApplication.getDeviceId());
                break;
            case R.id.link_signup:
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                finish();
                break;
            case R.id.link_forgot:
                startActivity(new Intent(getApplicationContext(), ForgotPasword.class));
                finish();
                break;
        }
    }
}
