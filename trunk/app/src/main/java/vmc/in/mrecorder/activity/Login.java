package vmc.in.mrecorder.activity;


import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.fragment.OTPDialogFragment;
import vmc.in.mrecorder.gcm.GCMClientManager;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.util.JSONParser;
import vmc.in.mrecorder.util.Utils;

public class Login extends AppCompatActivity implements View.OnClickListener, OTPDialogFragment.OTPDialogListener, TAG {

    private static Login inst;
    Button btn_login, btn_getOtp;

    EditText et_email, et_password;
    CheckBox check_box;
    TextView tv_otp, link_forgot_password;
    String email, password;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private String OTP_Sms = "N/A", OTP_resp, gcmkey;
    private ProgressDialog progressDialog;
    public static final String DEAFULT = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordi_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        CallApplication.sp.edit().putInt(TYPE, 1).commit();
//        if ( CallApplication.sp.getInt(TYPE, 0) == 0) {
//            startService(CallApplication.all);
//        } else if ( CallApplication.sp.getInt(TYPE, 0) == 1) {
//            stopService(CallApplication.all);
//            //  stopService(opt);
//        }
       Utils.stopRecording(Login.this);



        tv_otp = (TextView) findViewById(R.id.input_OTP);
        btn_getOtp = (Button) findViewById(R.id.btn_get_otp);
        btn_login = (Button) findViewById(R.id.btn_login);
        check_box = (CheckBox) findViewById(R.id.checkBox_forgot);
        link_forgot_password = (TextView) findViewById(R.id.link_forgot);
        et_email = (EditText) findViewById(R.id.input_email);
        et_password = (EditText) findViewById(R.id.input_password);


        btn_login.setOnClickListener(this);
        btn_getOtp.setOnClickListener(this);
        link_forgot_password.setOnClickListener(this);
        et_email.addTextChangedListener(new MyTextWacher(et_email));
        et_password.addTextChangedListener(new MyTextWacher(et_password));



        load();
      //  cancelNotification(Login.this,NOTIFICATION_ID);

        GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                Log.d("Registration id", registrationId);
                gcmkey = registrationId;
                if (isNewRegistration) {
                    onRegisterGcm(registrationId);

                }
            }


        });

        // Log.d("android_id", CallApplication.getDeviceId());
    }


    public void onRegisterGcm(final String regid) {

        if (Utils.onlineStatus2(Login.this)) {
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


    public static Login instance() {
        return inst;
    }

    @Override
    protected void onStart() {
        super.onStart();
        inst = this;
    }

    public void updateList(final String smsMessage) {
        //update otp Your one time passwod for Mconnect is: 356958
        OTP_Sms = smsMessage.substring(42);
        //  String OTP1=smsMessage.split(": ")[0];

        // Log.d("SMS", OTP1+" "+OTP);
        tv_otp.setText(OTP_Sms);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                Login();
                // StartLogin();
                break;

            case R.id.btn_get_otp:

              //  btn_getOtp.setBackgroundColor(Color.parseColor("#FFDC4545"));
                if (validateOTP()) {
                    GetOtp();
                }
                break;

            case R.id.link_forgot:
                startActivity(new Intent(getApplicationContext(), ForgotPasword.class));
                finish();
                break;
        }
    }

    private void Login() {


        if (validate()) {
            if (OTP_resp.equals(OTP_Sms)) {
                //  startActivity(new Intent(getApplicationContext(), Home.class));
                //Toast.makeText(getApplicationContext(), "OTP Verfied", Toast.LENGTH_SHORT).show();
                StartLogin();
            } else {

                btn_login.setEnabled(false);
                onLoginFailed();
            }
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        btn_login.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String OTP = tv_otp.getText().toString();

        Drawable drawable = ContextCompat.getDrawable(Login.this, R.drawable.error);
        drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("enter a valid email address", drawable);
            //errormsg = "Enter a valid email address";
            valid = false;
        } else {
            et_email.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            et_password.setError("between 4 and 10 alphanumeric characters", drawable);
            // errormsg = "Password must be between 4 and 10 alphanumeric characters";
            valid = false;
        } else {
            et_password.setError(null);
        }
        if (OTP.isEmpty() || OTP.length() < 6) {
            tv_otp.setError("Wait for OTP Message", drawable);
            // errormsg = "Password must be between 4 and 10 alphanumeric characters";
            valid = false;
        } else {
            tv_otp.setError(null);
        }

//        if (!terms.isChecked()) {
//            Snackbar snack = Snackbar.make(mroot, "Accept terms and condition to proceed", Snackbar.LENGTH_SHORT)
//                    .setAction(null, new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                        }
//                    })
//                    .setActionTextColor(getResources().getColor(R.color.accent));
//            TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
//            tv.setTextColor(Color.WHITE);
//            snack.show();
//            valid = false;
//        } else {
//            _repassword.setError(null);
//        }

        return valid;
    }

    public boolean validateOTP() {
        boolean valid = true;

        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String OTP = tv_otp.getText().toString();

        Drawable drawable = ContextCompat.getDrawable(Login.this, R.drawable.error);
        drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("enter a valid email address", drawable);
            //errormsg = "Enter a valid email address";
            valid = false;
        } else {
            et_email.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            et_password.setError("between 4 and 10 alphanumeric characters", drawable);
            // errormsg = "Password must be between 4 and 10 alphanumeric characters";
            valid = false;
        } else {
            et_password.setError(null);
        }
        return valid;
    }

    @Override
    public void onFinishInputDialog(String inputText) {
        // Toast.makeText(getApplicationContext(), "key" + inputText, Toast.LENGTH_SHORT).show();
    }


    private class MyTextWacher implements TextWatcher {

        private View view;

        private MyTextWacher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            switch (view.getId()) {
                case R.id.input_email:
                    //   validateEmail();
                    break;
                case R.id.input_password:
                    ///  validatePassword();
                    break;
            }
        }
    }

    public void load() {
        SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
        String email = pref.getString("email", DEAFULT);
        String password = pref.getString("password", DEAFULT);

        if (!email.equals(DEAFULT) || !password.equals(DEAFULT)) {
            et_email.setText(email);
            et_password.setText(password);
        }
        if (password.equals("")) {
            et_password.requestFocus();
            //  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void save() {
        if (check_box.isChecked()) {
            String email = et_email.getText().toString();
            String password = et_password.getText().toString();
            SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("email", email);
            editor.putString("password", password);
            editor.commit();

        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Login.this);
            prefs.edit().clear().commit();
            String email = et_email.getText().toString();
            SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("email", email);
            editor.putString("password", "");
            editor.commit();
        }
    }


    public void GetOtp() {
        btn_getOtp.setText("RESEND");
        email = et_email.getText().toString().trim();
        password = et_password.getText().toString().trim();
        if (Utils.onlineStatus2(Login.this)) {
            new GetOtp(email, password).execute();
        } else {
            Snackbar snack = Snackbar.make(coordinatorLayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GetOtp();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(Login.this, R.color.primary));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }
    }


    class GetOtp extends AsyncTask<Void, Void, JSONObject> {
        String message = "No Response from server";
        String code = "N";
        String email = "n";
        String password = "n";

        String msg, Otp;
        JSONObject response = null;

        public GetOtp(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
//             showProgress("Login Please Wait.."); progressDialog.setIndeterminate(true);
//             progressDialog.setMessage("Generating OTP...");
//               progressDialog.show();
            progressDialog = new ProgressDialog(Login.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Generating OTP...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            super.onPreExecute();
        }


        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO Auto-generated method stub


            try {
                response = JSONParser.getOTP(GET_OTP, email, password);

                if (response != null) {
                    if (response.has(CODE)) {
                        code = response.getString(CODE);
                    }
                    if (response.has(MESSAGE)) {
                        msg = response.getString(MESSAGE);
                    }
                    if (response.has(OTP)) {
                        OTP_resp = response.getString(OTP);
                        Log.d("OTP", OTP_resp);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            if (data != null) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();}
                Log.d("OTP", data.toString());


                if (code.equals("202")) {
                    //  Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

                } else {
                    //showOTPDialog();

                }

            }
        }


    }


    public void StartLogin() {
        if (Utils.onlineStatus2(Login.this)) {
            new StartLogin().execute();
        } else {
            Snackbar snack = Snackbar.make(coordinatorLayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StartLogin();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(Login.this, R.color.primary));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }
    }

    class StartLogin extends AsyncTask<Void, Void, JSONObject> {
        String message = "n";
        String code = "n";
        String authcode = "n", name = "n";

        JSONObject response = null;
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        private String image;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Login.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.setCancelable(false);
            progressDialog.show();


        }


        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                response = JSONParser.login(LOGIN_URL, email, password, CallApplication.getDeviceId(), gcmkey);
                Log.d("GCMPRO",response.toString());
                if (response.has(CODE))
                    code = response.getString(CODE);
                if (response.has(MESSAGE))
                    message = response.getString(MESSAGE);
                if (response.has(AUTHKEY))
                    authcode = response.getString(AUTHKEY);

            } catch (Exception e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(JSONObject data) {

            if (data != null) {
                Log.d("LOG", data.toString());
            }
            btn_login.setEnabled(true);

            if (code.equals("202")) {

                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                StartLogin();

                            }
                        }).
                        setActionTextColor(ContextCompat.getColor(Login.this, R.color.accent)).show();
            }
            if (code.equals("400")) {
                save();
                Utils.saveToPrefs(Login.this, AUTHKEY, authcode);
                Utils.saveToPrefs(Login.this, EMAIL, email);
                Intent intent = new Intent(Login.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                overridePendingTransition(0, 0);
                Login.this.startActivity(intent);
            }


            if (code.equals("n")) {
                Snackbar.make(coordinatorLayout, "No Response From Server", Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                StartLogin();

                            }
                        }).
                        setActionTextColor(ContextCompat.getColor(Login.this, R.color.primary_dark)).show();
            }
        }

    }
    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

}