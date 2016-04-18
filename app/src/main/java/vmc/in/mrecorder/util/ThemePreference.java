package vmc.in.mrecorder.util;

import android.content.Context;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.util.AttributeSet;
import android.widget.TextView;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;


public class ThemePreference extends Preference implements View.OnClickListener, TAG {
    private ImageButton red, green, blue, orange, deeppurple, indigo;
    private TextView summary;
    private Context mContext;

    public ThemePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        red = (ImageButton) view.findViewById(R.id.red);
        summary = (TextView) view.findViewById(R.id.summary);
        red.setOnClickListener(this);
        green = (ImageButton) view.findViewById(R.id.green);
        green.setOnClickListener(this);
        blue = (ImageButton) view.findViewById(R.id.blue);
        blue.setOnClickListener(this);
        orange = (ImageButton) view.findViewById(R.id.orange);
        orange.setOnClickListener(this);
        deeppurple = (ImageButton) view.findViewById(R.id.deeppurple);
        deeppurple.setOnClickListener(this);
        indigo = (ImageButton) view.findViewById(R.id.indigo);
        indigo.setOnClickListener(this);
        setSelection();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.red:
                Utils.saveToPrefs(mContext, THEME, "1");
                CustomTheme.changeToTheme((AppCompatActivity) mContext, 1);

                break;
            case R.id.blue:
                Utils.saveToPrefs(mContext, THEME, "0");
                CustomTheme.changeToTheme((AppCompatActivity) mContext, 0);

                break;
            case R.id.green:
                Utils.saveToPrefs(mContext, THEME, "2");
                CustomTheme.changeToTheme((AppCompatActivity) mContext, 2);

                break;
            case R.id.deeppurple:
                Utils.saveToPrefs(mContext, THEME, "3");
                CustomTheme.changeToTheme((AppCompatActivity) mContext, 3);

                break;
            case R.id.indigo:
                Utils.saveToPrefs(mContext, THEME, "4");
                CustomTheme.changeToTheme((AppCompatActivity) mContext, 4);

                break;
            default:
                Utils.saveToPrefs(mContext, THEME, "5");
                CustomTheme.changeToTheme((AppCompatActivity) mContext, 5);

                break;
        }

    }

    public void setSelection() {
        int id = Integer.parseInt(Utils.getFromPrefs((AppCompatActivity) mContext, THEME, "5"));
        switch (id) {

            case 1:
                summary.setText("Red");
                break;
            case 0:
                summary.setText("Blue");

                break;
            case 2:
                summary.setText("Green");
                break;
            case 3:
                summary.setText("Deep Purple");
                break;
            case 4:
                summary.setText("Indigo");
                break;
            default:
                summary.setText("Default");
                break;
        }
    }
}
