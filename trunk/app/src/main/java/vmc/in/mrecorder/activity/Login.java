package vmc.in.mrecorder.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        CallApplication.getInstance().stopRecording();
        tv_otp = (TextView) findViewById(R.id.input_OTP);
        btn_getOtp = (Button) findViewById(R.id.btn_get_otp);
        btn_login = (Button) findViewById(R.id.btn_login);
        check_box = (CheckBox) findViewById(R.id.checkBox_forgot);
        link_forgot_password = (TextView) findViewById(R.id.link_forgot);
        et_email = (EditText) findViewById(R.id.input_email);
        et_password = (EditText) findViewById(R.id.input_password);

        getAllPermision();
        btn_login.setOnClickListener(this);
        btn_getOtp.setOnClickListener(this);
        link_forgot_password.setOnClickListener(this);
        load();
        cancelNotification(Login.this, NOTIFICATION_ID);

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
        OTP_Sms = smsMessage.substring(33);
        //  String OTP1=smsMessage.split(": ")[0];

        // Log.d("SMS", OTP1+" "+OTP);
        tv_otp.setText(OTP_Sms);
        btn_login.setText("Login");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }


    }

    @Override
    public void onClick(View v) {
        hideKeyboard();
        switch (v.getId()) {
            case R.id.btn_login:
                if (btn_login.getText().toString().equals("Login")) {

                    Login();
                } else if (validateOTP()) {
                    GetOtp();
                    if (tv_otp.getText().toString().length() == 0 || tv_otp.getText().toString().equals("")) {
                        btn_login.setText("Resend OTP");
                    }
                }
                break;

            case R.id.btn_get_otp:
//                if (validateOTP()) {
//                    GetOtp();
//                }
                break;

            case R.id.link_forgot:
                startActivity(new Intent(getApplicationContext(), ForgotPasword.class));
                finish();
                break;
        }
    }

    private void Login() {


        if (validate()) {

            if (OTP_resp != null && OTP_resp.equals(OTP_Sms)) {
                //  startActivity(new Intent(getApplicationContext(), Home.class));
                //Toast.makeText(getApplicationContext(), "OTP Verfied", Toast.LENGTH_SHORT).show();
                StartLogin();
            } else {

                btn_login.setEnabled(false);
                onLoginFailed();
            }
        } else {
            // Toast.makeText(getBaseContext(), "validate failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Invalid OTP try again", Toast.LENGTH_SHORT).show();
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
            // tv_otp.setError("Wait for OTP Message", drawable);
            // errormsg = "Password must be between 4 and 10 alphanumeric characters";
            valid = false;
        } else {
            // tv_otp.setError(null);
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


    public synchronized void GetOtp() {
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
            super.onPreExecute();
//             showProgress("Login Please Wait.."); progressDialog.setIndeterminate(true);
//             progressDialog.setMessage("Generating OTP...");
//               progressDialog.show();
            progressDialog = new ProgressDialog(Login.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Generating OTP...");
            progressDialog.setCancelable(false);
            progressDialog.show();


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
                    progressDialog.dismiss();
                }
                Log.d("OTP", data.toString());

                tv_otp.setHint("Waiting for OTP");
                if (code.equals("202")) {
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
//                    if (progressDialog != null && progressDialog.isShowing()) {
//                        progressDialog.dismiss();
//                    }

                } else {

                    //showOTPDialog();

                }

            } else {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
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
        String username = "n";
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
                response = JSONParser.login(LOGIN_URL, email, password, CallApplication.getInstance().getDeviceId(), gcmkey);
                Log.d("GCMPRO", response.toString());
                if (response.has(CODE))
                    code = response.getString(CODE);
                if (response.has(MESSAGE))
                    message = response.getString(MESSAGE);
                if (response.has(AUTHKEY))
                    authcode = response.getString(AUTHKEY);
                if (response.has(NAME)) {
                    username = response.getString(NAME);
                }
                Log.d("LOG", authcode);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
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
                Utils.saveToPrefs(Login.this, NAME, username);
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

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            View view = this.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else {
            // writeToLog("Software Keyboard was not shown");
        }
    }

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @TargetApi(Build.VERSION_CODES.M)
    private void getAllPermision() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("CALL PHONE");
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add("RECORD AUDIO");
        if (!addPermission(permissionsList, Manifest.permission.PROCESS_OUTGOING_CALLS))
            permissionsNeeded.add("PROCESS OUTGOING CALLS ");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_CALL_LOG))
            permissionsNeeded.add("WRITE CALL LOG");
        if (!addPermission(permissionsList, Manifest.permission.READ_SMS))
            permissionsNeeded.add("READ SMS");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("READ CONTACTS");
        if (!addPermission(permissionsList, Manifest.permission.READ_CALL_LOG))
            permissionsNeeded.add("READ CALL LOG");
        if (!addPermission(permissionsList, Manifest.permission.INTERNET))
            permissionsNeeded.add("INTERNET");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
            permissionsNeeded.add("ACCESS NETWORK STATE");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_WIFI_STATE))
            permissionsNeeded.add("ACCESS WIFI STATE");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("READ PHONE STATE");
        if (!addPermission(permissionsList, Manifest.permission.MODIFY_AUDIO_SETTINGS))
            permissionsNeeded.add("MODIFY AUDIO SETTINGS");
        if (!addPermission(permissionsList, Manifest.permission.RECEIVE_BOOT_COMPLETED))
            permissionsNeeded.add("RECEIVE BOOT COMPLETED");
        if (!addPermission(permissionsList, Manifest.permission.VIBRATE))
            permissionsNeeded.add("VIBRATE");
        if (!addPermission(permissionsList, Manifest.permission.WAKE_LOCK))
            permissionsNeeded.add("WAKE LOCK");
        if (!addPermission(permissionsList, Manifest.permission.GET_ACCOUNTS))
            permissionsNeeded.add("GET ACCOUNTS");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("READ EXTERNAL STORAGE");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("WRITE EXTERNAL STORAGE");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS))
            permissionsNeeded.add("ACCESS LOCATION EXTRA COMMANDS");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("ACCESS COARSE LOCATION");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("ACCESS FINE LOCATION");
        if (!addPermission(permissionsList, Manifest.permission.RECEIVE_SMS))
            permissionsNeeded.add("RECEIVE SMS");
        if (!addPermission(permissionsList, Manifest.permission.READ_SMS))
            permissionsNeeded.add("READ SMS");
        if (!addPermission(permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("SEND SMS");
        if (!addPermission(permissionsList, Manifest.permission.READ_SYNC_STATS))
            permissionsNeeded.add("READ SYNC STATS");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_SYNC_SETTINGS))
            permissionsNeeded.add("WRITE SYNC SETTINGS");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
            permissionsNeeded.add("ACCESS NETWORK STATE");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }


    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Login.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }
}