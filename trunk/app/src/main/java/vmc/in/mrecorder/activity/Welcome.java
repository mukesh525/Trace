package vmc.in.mrecorder.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.util.Utils;

public class Welcome extends AppCompatActivity implements TAG {
    private static int SPLASH_TIME_OUT = 3000;
    Boolean splashShown;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        if (Utils.tabletSize(Welcome.this) < 6.0) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        splashShown = Utils.getFromPrefsBoolean(Welcome.this, SHOWN, false);
        if (splashShown) {

            if (Utils.isLogin(Welcome.this)) {
                i = new Intent(Welcome.this, Home.class);
            } else {
                i = new Intent(Welcome.this, Login.class);
            }
            startActivity(i);

        } else {
            Utils.saveToPrefs(Welcome.this, SHOWN, true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Utils.isLogin(Welcome.this)) {
                        i = new Intent(Welcome.this, Home.class);
                    } else {
                        i = new Intent(Welcome.this, Login.class);
                    }
                    startActivity(i);

                }
            }, SPLASH_TIME_OUT);

        }

    }
}
