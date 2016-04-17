package vmc.in.mrecorder.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.prefs.Preferences;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.syncadapter.SyncUtils;
import vmc.in.mrecorder.util.CustomTheme;
import vmc.in.mrecorder.util.Utils;

public class Settings extends AppCompatActivity implements View.OnClickListener, TAG {

    private Toolbar toolbar;
    private RadioButton red, green, blue, orange;
    private TextView themetext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomTheme.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        themetext = (TextView) findViewById(R.id.settheme);

        setTextTheme(themetext);

        red = (RadioButton) findViewById(R.id.red);
        red.setOnClickListener(this);
        green = (RadioButton) findViewById(R.id.green);
        green.setOnClickListener(this);
        blue = (RadioButton) findViewById(R.id.blue);
        blue.setOnClickListener(this);
        orange = (RadioButton) findViewById(R.id.orange);
        orange.setOnClickListener(this);

        setSelection();
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MyPreferenceFragment())
                .commit();
       PreferenceManager.setDefaultValues(Settings.this, R.xml.settings, false);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.red:
                Utils.saveToPrefs(Settings.this, THEME, "1");
                CustomTheme.changeToTheme(Settings.this, 1);
                red.setChecked(true);
                blue.setChecked(false);
                green.setChecked(false);
                orange.setChecked(false);
                break;
            case R.id.blue:
                Utils.saveToPrefs(Settings.this, THEME, "0");
                CustomTheme.changeToTheme(Settings.this, 0);
                red.setChecked(false);
                blue.setChecked(true);
                green.setChecked(false);
                orange.setChecked(false);
                break;
            case R.id.green:
                Utils.saveToPrefs(Settings.this, THEME, "2");
                CustomTheme.changeToTheme(Settings.this, 2);
                red.setChecked(false);
                blue.setChecked(false);
                green.setChecked(true);
                orange.setChecked(false);
                break;
            default:
                Utils.saveToPrefs(Settings.this, THEME, "5");
                CustomTheme.changeToTheme(Settings.this, 5);
                red.setChecked(false);
                blue.setChecked(false);
                green.setChecked(false);
                orange.setChecked(true);
                break;
        }

    }

    public void setSelection() {
        int id = Integer.parseInt(Utils.getFromPrefs(Settings.this, THEME, "5"));
        switch (id) {

            case 1:
                red.setChecked(true);
                blue.setChecked(false);
                green.setChecked(false);
                orange.setChecked(false);
                break;
            case 0:
                red.setChecked(false);
                blue.setChecked(true);
                green.setChecked(false);
                orange.setChecked(false);
                break;
            case 2:
                red.setChecked(false);
                blue.setChecked(false);
                green.setChecked(true);
                orange.setChecked(false);
                break;
            default:
                red.setChecked(false);
                blue.setChecked(false);
                green.setChecked(false);
                orange.setChecked(true);
                break;
        }
    }

    public void setTextTheme(TextView view) {
        int id = Integer.parseInt(Utils.getFromPrefs(Settings.this, THEME, "5"));
        ;
        switch (id) {
            case 0:
                view.setTextColor(Color.parseColor("#8BC34A"));
                break;
            case 1:
                view.setTextColor(Color.parseColor("#795548"));
                break;
            case 2:
                view.setTextColor(Color.parseColor("#536DFE"));
                break;
            default:
                view.setTextColor(Color.parseColor("#03A9F4"));
                break;
        }
    }


    public static class MyPreferenceFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {


        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            final SwitchPreference recordingPreference = (SwitchPreference) findPreference("prefRecording");
            final SwitchPreference callPreference = (SwitchPreference) findPreference("prefCallUpdate");

            callPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if (newValue instanceof Boolean) {
                        boolean selected = Boolean.parseBoolean(newValue.toString());
                        if (selected) {
                            recordingPreference.setChecked(false);
                        } else {
                            recordingPreference.setChecked(true);
                        }
                        return true;
                    }
                    return false;
                }
            });

            recordingPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue instanceof Boolean) {
                        boolean selected = Boolean.parseBoolean(newValue.toString());
                        if (selected) {
                            callPreference.setChecked(false);
                        } else {
                            callPreference.setChecked(true);
                        }
                        return true;
                    }
                    return false;
                }
            });


        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                    for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                        Preference singlePref = preferenceGroup.getPreference(j);
                        updatePreference(singlePref, singlePref.getKey());
                    }
                } else {
                    updatePreference(preference, preference.getKey());
                }
            }

        }

        private void updatePreference(Preference preference, String key) {
            if (preference == null) return;
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setSummary(listPreference.getEntry());
                return;
            }
            if (preference instanceof SwitchPreference) {
                SwitchPreference checkPreference = (SwitchPreference) preference;
                checkPreference.setSummary(checkPreference.getSummary());
                return;
            }
//            SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
//            preference.setSummary(sharedPrefs.getString(key, "Default"));
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            Preference pref = findPreference(key);
            boolean recording = sharedPreferences.getBoolean("prefRecording", true);
            CallApplication.getInstance().startRecording();
            if (recording) {
                //  CallApplication.getInstance().startRecording();
            } else {
                //  CallApplication.getInstance().stopRecording();
            }

            updatePreference(pref, key);
            SyncUtils.CreateSyncAccount(getActivity());
            SyncUtils.updateSync();


        }
    }


}


