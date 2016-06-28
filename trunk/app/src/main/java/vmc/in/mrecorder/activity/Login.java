package vmc.in.mrecorder.activity;


import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.fragment.OTPDialogFragment;
import vmc.in.mrecorder.gcm.GCMClientManager;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.JSONParser;
import vmc.in.mrecorder.util.Utils;

public class Login extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,View.OnClickListener, OTPDialogFragment.OTPDialogListener, TAG {

    private static Login inst;
    Button btn_login, btn_getOtp;

    EditText et_email, et_password;
    CheckBox check_box, check_box_show_password;
    TextView tv_otp;
    String email, password;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private String OTP_Sms = "N/A", OTP_resp, gcmkey;
    private ProgressDialog progressDialog;
    public static final String DEAFULT = "";
    private boolean first = true;


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
        et_email = (EditText) findViewById(R.id.input_email);
        et_password = (EditText) findViewById(R.id.input_password);

//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
//            getAllPermision();
//
//        }


        if (android.os.Build.VERSION.SDK_INT > 19) {
            btn_login.setBackgroundResource(R.drawable.button_background);

        }
        btn_login.setOnClickListener(this);
        btn_getOtp.setOnClickListener(this);
        //  link_forgot_password.setOnClickListener(this);
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


        et_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (et_password.getRight() - et_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (et_password.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                            // show password
                            et_password.setInputType(InputType.TYPE_CLASS_TEXT);
                            et_password.clearFocus();
                            et_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.show, 0);
                        } else {
                            // hide password
                            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            et_password.clearFocus();
                            et_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pass, 0, R.drawable.invisible, 0);

                        }


                        return false;
                    }
                }
                return false;
            }
        });


        // Log.d("android_id", CallApplication.getDeviceId());
    }


    public void onRegisterGcm(final String regid) {

        if (ConnectivityReceiver.isConnected()) {
            new RegisterGcm(regid).execute();
        } else {
            Toast.makeText(Login.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Toast.makeText(Login.this, "Exit Login", Toast.LENGTH_SHORT).show();
        System.exit(0);

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
        OTP_Sms = smsMessage.substring(9, 15);
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

        permissions();

        et_password.clearFocus();
        et_email.clearFocus();

        switch (v.getId()) {
            case R.id.btn_login:
                if (first && validateOTP()) {

                    showTermsAlert();
                    first = false;
                } else {
                    startLogin();
                }
                break;

            case R.id.btn_get_otp:
//                if (validateOTP()) {
//                    GetOtp();
//                }
                break;

//            case R.id.link_forgot:
//                startActivity(new Intent(getApplicationContext(), ForgotPasword.class));
//                finish();
//                break;
        }
    }

    private void startLogin() {
        if (btn_login.getText().toString().equals("Login")) {

            Login();
        } else if (validateOTP()) {

            GetOtp();

        }
    }

    public void showTermsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Login.this);
        alertDialog.setTitle("MTracker");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        // Setting Dialog Message
        alertDialog.setMessage("You agree that MTracker will record all calls made through this device. ");

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // dialog.cancel();
                startLogin();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                first = true;
                dialog.cancel();
                // Utils.isLogout(Login.this);
            }
        });

        // Showing Alert Message
        alertDialog.show();
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

        if (password.isEmpty()) {
            et_password.setError("Password must not be empty.", drawable);
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

        if (password.isEmpty()) {
            et_password.setError("Password must not be empty.", drawable);
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
            et_email.clearFocus();
            et_password.clearFocus();
        }
        if (password.equals("")) {
            //  et_password.requestFocus();
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
        email = et_email.getText().toString().trim();
        password = et_password.getText().toString().trim();
        if (ConnectivityReceiver.isConnected()) {
            if (tv_otp.getText().toString().length() == 0 || tv_otp.getText().toString().equals("")) {
                btn_login.setText("Resend OTP");
            }
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
                Log.d("OTPRes", response.toString());
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

                if (tv_otp.getVisibility() == View.GONE) {
                    tv_otp.setVisibility(View.VISIBLE);
                }
                tv_otp.setHint("Waiting for OTP");

                if (code.equals("202")) {
                    btn_login.setText("Get OTP");
                    Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).show();
                    tv_otp.setHint("");

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

        if(ConnectivityReceiver.isConnected()){
            new StartLogin().execute();
        }
//        if (Utils.onlineStatus2(Login.this)) {
//            new StartLogin().execute();
//
//        }
        else {

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
        String username = "n", recording = "n", mcubeRecording = "n", workhour = "n";
        String authcode = "n", name = "n";

        JSONObject response = null;
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        private String image;
        private String usertype;

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
                Log.d("GCMPRO D", CallApplication.getInstance().getDeviceId());

                response = JSONParser.login(LOGIN_URL, email, password, CallApplication.getInstance().getDeviceId(), gcmkey);
                Log.d("GCMPRO", response.toString());
                Log.d("GCMPRO D", CallApplication.getInstance().getDeviceId());
                if (response.has(CODE))
                    code = response.getString(CODE);
                if (response.has(MESSAGE))
                    message = response.getString(MESSAGE);
                if (response.has(AUTHKEY))
                    authcode = response.getString(AUTHKEY);
                if (response.has(NAME)) {
                    username = response.getString(NAME);
                }
                if (response.has(USERTYPE)) {
                    usertype = response.getString(USERTYPE);
                }
                if (response.has(RECORDING)) {
                    recording = response.getString(RECORDING);
                }
                if (response.has(MCUBECALLS)) {
                    mcubeRecording = response.getString(MCUBECALLS);
                }
                if (response.has(WORKHOUR)) {
                    workhour = response.getString(WORKHOUR);
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
                btn_login.setText("Get OTP");

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
                Utils.saveToPrefs(Login.this, USERTYPE, usertype);
                SharedPreferences sharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor ed = sharedPrefs.edit();
                if (recording.equals("1")) {
                    ed.putBoolean("prefRecording", true);
                } else {
                    ed.putBoolean("prefRecording", false);
                }
                if (workhour.equals("1")) {
                    ed.putBoolean("prefOfficeTimeRecording", true);
                } else {
                    ed.putBoolean("prefOfficeTimeRecording", false);
                }
                if (mcubeRecording.equals("1")) {
                    ed.putBoolean("prefMcubeRecording", true);
                } else {
                    ed.putBoolean("prefMcubeRecording", false);
                }

                ed.commit();
                CallApplication.getInstance().startRecording();


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


    @Override
    protected void onResume() {
        super.onResume();
        CallApplication.getInstance().setConnectivityListener(this);
    }

    public void permissions() {
        Dexter.checkPermissions(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                List<String> grantedPermissions = new ArrayList<String>();
                for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
                    if (!grantedPermissions.contains(response.getPermissionName())) {
                        grantedPermissions.add(response.getPermissionName());
                    }
                }
               // Toast.makeText(getApplicationContext(), "Granted permissions:" + grantedPermissions.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION);
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
                    .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }
}