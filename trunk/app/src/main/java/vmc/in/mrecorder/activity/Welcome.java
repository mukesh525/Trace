package vmc.in.mrecorder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import vmc.in.mrecorder.R;

public class Welcome extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i = new Intent(Welcome.this, Login.class);
                startActivity(i);

            }
        }, SPLASH_TIME_OUT);

    }
}
