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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Settings extends AppCompatActivity implements TAG {

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomTheme.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MyPreferenceFragment())
                .commit();
        PreferenceManager.setDefaultValues(Settings.this, R.xml.settings, false);

    }

    public static class MyPreferenceFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {



        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            final SwitchPreference recordingPreference = (SwitchPreference) findPreference("prefRecording");
            final SwitchPreference callPreference = (SwitchPreference) findPreference("prefCallUpdate");
            final Preference themePrefrence = (Preference) findPreference("preftheme");
//

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);

            return v;
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


