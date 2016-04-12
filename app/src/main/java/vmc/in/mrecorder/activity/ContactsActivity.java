package vmc.in.mrecorder.activity;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import java.io.File;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.adapter.ContactsPagerAdapter;
import vmc.in.mrecorder.fragment.AllCallsFragment;
import vmc.in.mrecorder.fragment.DialledCallFragment;
import vmc.in.mrecorder.fragment.MissedCallFragment;
import vmc.in.mrecorder.fragment.ReceivedCallFragment;
import vmc.in.mrecorder.util.Utils;


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

        toolbar.setTitle("MCube Tracker");
        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.fabBtn);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setRecording(ContactsActivity.this);
            }
        });


    }

    public void playAudioPath(String path) {

        // Toast.makeText(HomeActivity.this, url, Toast.LENGTH_LONG).show();//Uri myUri = Uri.parse("http://mcube.vmctechnologies.com/sounds/99000220411460096169.wav");
        File file = new File(path);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "audio/*");
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        if (mAllCallsFragment != null) {

            /* Bug? -> https://code.google.com/p/android/issues/detail?id=77285 */
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

}
