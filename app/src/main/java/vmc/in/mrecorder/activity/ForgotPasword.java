package vmc.in.mrecorder.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.fragment.OTPDialogFragment;
import vmc.in.mrecorder.util.JSONParser;
import vmc.in.mrecorder.util.Utils;

public class ForgotPasword extends AppCompatActivity implements TAG, OTPDialogFragment.OTPDialogListener, TextWatcher {
    private static ForgotPasword inst;

    @InjectView(R.id.fginput_phone)
    EditText _phone;
    @InjectView(R.id.sendOtp)
    Button _sendOTp;
    @InjectView(R.id.fginput_repassword)
    EditText _repassword;
    @InjectView(R.id.fginput_password)
    EditText _passwordText;
    @InjectView(R.id.btn_changepassword)
    Button _changePassButton;
    @InjectView(R.id.link_login)
    TextView _loginLink;
    @InjectView(R.id.fg_OTP)
    EditText _fgOTP;
    @InjectView(R.id.mroot)
    RelativeLayout mroot;
    OTPDialogFragment otpDialogFragment = new OTPDialogFragment();
    AlertDialog.Builder alertDialog;
    private String OTP1;
    private ProgressDialog progressDialog;
    private String password, repassword, phone;


    public static ForgotPasword instance() {
        return inst;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pasword);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _phone.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog = new AlertDialog.Builder(this);

        _changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        _sendOTp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTP1 = _phone.getText().toString();
                GetOtp();
                _sendOTp.setText("Resend");
            }
        });


        _phone.addTextChangedListener(this);

    }

    public boolean validate() {
        boolean valid = true;


        String password = _passwordText.getText().toString();
        String repassword = _repassword.getText().toString();
        String phone = _phone.getText().toString();
        Drawable drawable = ContextCompat.getDrawable(ForgotPasword.this, R.drawable.error);
        drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));

        if (phone.isEmpty() || phone.length() < 10) {
            _phone.setError("at least 10 digit", drawable);

            valid = false;
        } else {
            _phone.setError(null);
        }


        if (repassword.isEmpty() || repassword.length() < 4 || repassword.length() > 10) {
            _repassword.setError("between 4 and 10 alphanumeric characters", drawable);

            valid = false;
        } else {
            _repassword.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters", drawable);

            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (!repassword.equals(password)) {
            _repassword.setError("password mismatch", drawable);

            valid = false;
        } else {
            _repassword.setError(null);
        }


        return valid;
    }

    private void showOTPDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        otpDialogFragment = new OTPDialogFragment();
        otpDialogFragment.setCancelable(false);
        otpDialogFragment.setDialogTitle("Enter OTP");
        otpDialogFragment.show(fragmentManager, "Input Dialog");
    }

    public void changePass() {

        Log.d("TAG", "forgotPass");
        hideKeyboard();
        if (!validate()) {
            return;
        } else {

            phone = _phone.getText().toString();
            password = _passwordText.getText().toString();
            ChangePass();
        }

    }

    public void GetOtp() {
        if (Utils.onlineStatus2(ForgotPasword.this)) {
            new GetOtp(_phone.getText().toString()).execute();
        } else {
            Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GetOtp();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(ForgotPasword.this, R.color.primary));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    public void onFinishInputDialog(String inputText) {
        hideKeyboard();
        OTP1 = inputText;
        ChangePass();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
       /* if (s.length() == 10) {
            OTP1 = _phone.getText().toString();
            GetOtp();
        }*/
    }

    @Override
    public void afterTextChanged(Editable s) {

    }



    public void updateList(final String smsMessage) {
        //Your OTP for Mconnect password change is: 090502
        OTP1 = smsMessage.substring(42);
        //  String OTP1=smsMessage.split(": ")[0];
        _fgOTP.setText(OTP1);
        _passwordText.requestFocus();


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

    public void ChangePass() {
        if (Utils.onlineStatus2(ForgotPasword.this)) {
            new ChangePass().execute();

        } else {
            Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ChangePass();
                        }
                    })
                    .setActionTextColor(getResources().getColor(R.color.accent));
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        }
    }

    class GetOtp extends AsyncTask<Void, Void, JSONObject> {
        String message = "No Response from server";
        String code = "N";
        String phone = "n";

        String msg;
        JSONObject response = null;

        public GetOtp(String phone) {
            this.phone = phone;
        }

        @Override
        protected void onPreExecute() {
            // showProgress("Login Please Wait.."); progressDialog.setIndeterminate(true);
            // progressDialog.setMessage("Generating OTP...");
            //   progressDialog.show();
            _sendOTp.setEnabled(false);
            Log.d("OTP", OTP1);
            super.onPreExecute();
        }


        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO Auto-generated method stub


            try {
                response = JSONParser.getOTP(FORGOT_OTP_URL, phone,null);
                if (response != null) {
                    if (response.has(CODE)) {
                        code = response.getString(CODE);
                    }
                    if (response.has(MESSAGE)) {
                        msg = response.getString(MESSAGE);
                    }
                }

                //  if(response.c)

            } catch (Exception e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            Drawable drawable = ContextCompat.getDrawable(ForgotPasword.this, R.drawable.error);
            drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));

            if (data != null) {

                Log.d("OTP", data.toString());

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (code.equals("202")) {
                    //  Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                    _phone.setError(msg, drawable);
                    _changePassButton.setEnabled(true);
                    _sendOTp.setEnabled(true);
                } else {
                    //showOTPDialog();
                    _phone.setError(null);
                    _sendOTp.setEnabled(true);
                }

            }
        }

    }

    class ChangePass extends AsyncTask<Void, Void, JSONObject> {
        String code = "N";
        JSONObject response = null;
        private String msg;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ForgotPasword.this,
                    R.style.MyMaterialTheme);
            progressDialog.setMessage("Verfying OTP");
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                response = JSONParser.ChangePass(CHANGED_PASS, phone, OTP1, password);
                code = response.getString(CODE);
                msg = response.getString(MESSAGE);

            } catch (Exception e) {
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            if (data != null) {
                Log.d("TEST", data.toString());
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (code.equals("n")) {
                Snackbar.make(mroot, "No Response From Server", Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChangePass();
                            }
                        }).
                        setActionTextColor(ContextCompat.getColor(ForgotPasword.this, R.color.primary_dark)).show();
            }
            if (code.equals("202")) {
                Snackbar.make(mroot, msg, Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChangePass();

                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(ForgotPasword.this, R.color.accent)).show();
            } else {
                _changePassButton.setEnabled(true);
                Intent intent = new Intent();
                intent.putExtra("msg", msg);
                setResult(RESULT_OK, intent);
                finish();

            }
        }

    }

}


