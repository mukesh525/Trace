package vmc.in.mrecorder.activity;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import vmc.in.mrecorder.R;
import vmc.in.mrecorder.adapter.ContactsPagerAdapter;
import vmc.in.mrecorder.fragment.AllCallsFragment;
import vmc.in.mrecorder.fragment.DialledCallFragment;
import vmc.in.mrecorder.fragment.MissedCallFragment;
import vmc.in.mrecorder.fragment.ReceivedCallFragment;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.syncadapter.SyncUtils;


public class ContactsActivity extends AppCompatActivity {

    private Fragment mAllCallsFragment;
    private Fragment mDialledCallsFragment;
    private Fragment mReceivedCallsFragment;
    private Fragment missedCallsFragment;
    private final String ALL_CALLS_FRAGMENT_KEY = "all_calls_fragment";
    private final String DIALLED_CALLS_FRAGMENT_KEY = "dialed_calls_fragment";
    private final String RECEIVED_CALLS_FRAGMENT_KEY = "received_calls_fragment";
    private final String MISSED_CALLS_FRAGMENT_KEY = "missed_calls_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {
            initializeFragments(savedInstanceState);
            setupViewPager(mViewPager);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        }

        toolbar.setTitle("Call Recorder");
        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.fabBtn);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecording();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing App");
                intent.putExtra(Intent.EXTRA_TEXT, "Hello I am using Call Recorder. You can also try at https://play.google.com/store/apps/details?id=" + getPackageName());
                SharedPreferences sharedpreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("share_count", sharedpreferences.getInt("share_count", 0) + 1);
                editor.commit();
                startActivityForResult(intent, 1285);

            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        if (mAllCallsFragment != null) {

            try {
                getFragmentManager().putFragment(outState, ALL_CALLS_FRAGMENT_KEY, mAllCallsFragment);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        if (mReceivedCallsFragment != null) {
            try {
                getFragmentManager().putFragment(outState, RECEIVED_CALLS_FRAGMENT_KEY, mReceivedCallsFragment);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        if (mDialledCallsFragment != null) {
            try {
                getFragmentManager().putFragment(outState, DIALLED_CALLS_FRAGMENT_KEY, mDialledCallsFragment);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        if (missedCallsFragment != null) {
            try {
                getFragmentManager().putFragment(outState, MISSED_CALLS_FRAGMENT_KEY, missedCallsFragment);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeFragments(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Fragment cFragment = getFragmentManager().getFragment(savedInstanceState,
                    ALL_CALLS_FRAGMENT_KEY);

            if (cFragment != null && cFragment instanceof AllCallsFragment) {
                mAllCallsFragment = (AllCallsFragment) cFragment;
            } else {
                mAllCallsFragment = AllCallsFragment.newInstance();
            }

            Fragment sFragment = getFragmentManager().getFragment(savedInstanceState,
                    DIALLED_CALLS_FRAGMENT_KEY);

            if (sFragment != null && sFragment instanceof DialledCallFragment) {
                mDialledCallsFragment = (DialledCallFragment) sFragment;
            } else {
                mDialledCallsFragment = DialledCallFragment.newInstance();
            }

            Fragment rFragment = getFragmentManager().getFragment(savedInstanceState,
                    RECEIVED_CALLS_FRAGMENT_KEY);

            if (rFragment != null && rFragment instanceof ReceivedCallFragment) {
                mReceivedCallsFragment = (ReceivedCallFragment) rFragment;
            } else {
                mReceivedCallsFragment = ReceivedCallFragment.newInstance();
            }
            Fragment mFragment = getFragmentManager().getFragment(savedInstanceState,
                    MISSED_CALLS_FRAGMENT_KEY);

            if (mFragment != null && mFragment instanceof MissedCallFragment) {
                missedCallsFragment = (MissedCallFragment) mFragment;
            } else {
                missedCallsFragment = MissedCallFragment.newInstance();
            }


        } else {
            mDialledCallsFragment = DialledCallFragment.newInstance();
            mReceivedCallsFragment = ReceivedCallFragment.newInstance();
            mAllCallsFragment = AllCallsFragment.newInstance();
            missedCallsFragment = MissedCallFragment.newInstance();

        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ContactsPagerAdapter mAdapter = new ContactsPagerAdapter(getFragmentManager());
        mAdapter.addFragment(mAllCallsFragment, "ALL CALLS");
        mAdapter.addFragment(mDialledCallsFragment, "DIALLED");
        mAdapter.addFragment(mReceivedCallsFragment, "RECEIVED");
        mAdapter.addFragment(missedCallsFragment, "MISSED");


        viewPager.setAdapter(mAdapter);
    }

    public void setRecording() {
        CallApplication.sp = getApplicationContext().getSharedPreferences("com.example.call", Context.MODE_PRIVATE);

        CallApplication.e = CallApplication.sp.edit();
        final Dialog dialog = new Dialog(ContactsActivity.this);
        dialog.setContentView(R.layout.layout_dialog);
        dialog.setTitle("Set Your Record Preference");
        RadioGroup group = (RadioGroup) dialog.findViewById(R.id.radioGroup1);
        //  final RelativeLayout rl = (RelativeLayout) dialog.findViewById(R.id.ask_layout);
        final TextView tv1 = (TextView) dialog.findViewById(R.id.r0);
        final TextView tv2 = (TextView) dialog.findViewById(R.id.r1);
        switch (CallApplication.sp.getInt("type", 0)) {
            case 0:
                group.check(R.id.radio0);
                break;

            case 1:
                group.check(R.id.radio1);
                break;


            default:
                break;
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.radio0:
                        CallApplication.e.putInt("type", 0);
                        // rl.setVisibility(View.GONE);
                        tv1.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.GONE);
                        break;
                    case R.id.radio1:
                        CallApplication.e.putInt("type", 1);
                        // rl.setVisibility(View.GONE);
                        tv1.setVisibility(View.GONE);
                        tv2.setVisibility(View.VISIBLE);
                        break;


                    default:
                        break;
                }
            }
        });
        Button save = (Button) dialog.findViewById(R.id.button1);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CallApplication.e.commit();
                CallApplication.getInstance().resetService();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void setUpReview() {

        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(2) // default 10
                .setRemindInterval(1) // default 1
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(ContactsActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);

    }


}
