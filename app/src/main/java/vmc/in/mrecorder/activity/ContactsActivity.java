package vmc.in.mrecorder.activity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import java.io.File;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.fragment.AllCallsFragment;
import vmc.in.mrecorder.util.CustomTheme;
import vmc.in.mrecorder.util.Utils;


public class ContactsActivity extends AppCompatActivity implements vmc.in.mrecorder.callbacks.TAG {

    private Fragment mAllCallsFragment;
    private Fragment mDialledCallsFragment;
    private Fragment mReceivedCallsFragment;
    private Fragment missedCallsFragment;

    private final String ALL_CALLS_FRAGMENT_KEY = "all_calls_fragment";
    private final String DIALLED_CALLS_FRAGMENT_KEY = "dialed_calls_fragment";
    private final String RECEIVED_CALLS_FRAGMENT_KEY = "received_calls_fragment";
    private final String MISSED_CALLS_FRAGMENT_KEY = "missed_calls_fragment";
    private String titles[] = {"ALL CALLS", "RECEIVED", "DIALLED", "MISSED"};
    private String TYPE;
    private MyPagerAdapter myPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomTheme.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        if (Utils.tabletSize(ContactsActivity.this) < 6.0) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_contacts_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        for(int i=0;i< titles.length;i++){
            tabLayout.getTabAt(i).setText(titles[i]);

        }

        if (mViewPager != null) {
//            initializeFragments(savedInstanceState);
//            setupViewPager(mViewPager);
//            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//            tabLayout.setupWithViewPager(mViewPager);
        }

        toolbar.setTitle("MCube Tracker");
        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.fabBtn);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Utils.setRecording(ContactsActivity.this);
                startActivity(new Intent(ContactsActivity.this, Settings.class));
            }
        });
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

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

//
//        if (mAllCallsFragment != null) {
//
//            /* Bug? -> https://code.google.com/p/android/issues/detail?id=77285 */
//            try {
//                getFragmentManager().putFragment(outState, ALL_CALLS_FRAGMENT_KEY, mAllCallsFragment);
//                outState.putString("ALL", "ALL");
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (mReceivedCallsFragment != null) {
//            try {
//                getFragmentManager().putFragment(outState, RECEIVED_CALLS_FRAGMENT_KEY, mReceivedCallsFragment);
//                outState.putString("RECEIVED", INCOMING);
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (mDialledCallsFragment != null) {
//            try {
//                getFragmentManager().putFragment(outState, DIALLED_CALLS_FRAGMENT_KEY, mDialledCallsFragment);
//                outState.putString("DIALLED", OUTGOING);
//
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//        }
//        if (missedCallsFragment != null) {
//            try {
//                getFragmentManager().putFragment(outState, MISSED_CALLS_FRAGMENT_KEY, missedCallsFragment);
//                outState.putString("MISSED", MISSED);
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//        }
    }

//    private void initializeFragments(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            Fragment cFragment = getFragmentManager().getFragment(savedInstanceState,
//                    ALL_CALLS_FRAGMENT_KEY);
//
//            if (cFragment != null && cFragment instanceof AllCallsFragment) {
//                mAllCallsFragment = (AllCallsFragment) cFragment;
//            } else {
//                mAllCallsFragment = AllCallsFragment.newInstance("ALL");
//            }
//
//            Fragment sFragment = getFragmentManager().getFragment(savedInstanceState,
//                    DIALLED_CALLS_FRAGMENT_KEY);
//
//            if (sFragment != null && sFragment instanceof DialledCallFragment) {
//                mDialledCallsFragment = (DialledCallFragment) sFragment;
//            } else {
//                mDialledCallsFragment = DialledCallFragment.newInstance();
//            }
//
//            Fragment rFragment = getFragmentManager().getFragment(savedInstanceState,
//                    RECEIVED_CALLS_FRAGMENT_KEY);
//
//            if (rFragment != null && rFragment instanceof ReceivedCallFragment) {
//                mReceivedCallsFragment = (ReceivedCallFragment) rFragment;
//            } else {
//                mReceivedCallsFragment = ReceivedCallFragment.newInstance();
//            }
//            Fragment mFragment = getFragmentManager().getFragment(savedInstanceState,
//                    MISSED_CALLS_FRAGMENT_KEY);
//
//            if (mFragment != null && mFragment instanceof MissedCallFragment) {
//                missedCallsFragment = (MissedCallFragment) mFragment;
//            } else {
//                missedCallsFragment = MissedCallFragment.newInstance();
//            }
//
//
//        } else {
//            mDialledCallsFragment = DialledCallFragment.newInstance();
//            mReceivedCallsFragment = ReceivedCallFragment.newInstance();
//            mAllCallsFragment = AllCallsFragment.newInstance("ALL");
//            missedCallsFragment = MissedCallFragment.newInstance();
//
//        }
//    }

//    private void setupViewPager(ViewPager viewPager) {
//        ContactsPagerAdapter mAdapter = new ContactsPagerAdapter(getFragmentManager());
//        mAdapter.addFragment(mAllCallsFragment, "ALL CALLS");
//        mAdapter.addFragment(mDialledCallsFragment, "DIALLED");
//        mAdapter.addFragment(mReceivedCallsFragment, "RECEIVED");
//        mAdapter.addFragment(missedCallsFragment, "MISSED");
//
////        for(int i=0;i<titles.length;i++){
////            mAdapter.addFragment(mAllCallsFragment, titles[i]);
////        }
//
//        viewPager.setAdapter(mAdapter);
//    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment = null;
            switch (position) {
                case 0:
                    myFragment = AllCallsFragment.newInstance(TYPE_ALL);
                    // myFragment = new AllCalls();
                    break;
                case 1:
                    //myFragment = new InboundCalls();
                    myFragment = AllCallsFragment.newInstance(INCOMING);
                    break;
                case 2:
                    //  myFragment = new OutboundCalls();
                    myFragment = AllCallsFragment.newInstance(OUTGOING);
                    break;
                case 3:
                    // myFragment = new MissedCalls();
                    myFragment = AllCallsFragment.newInstance(MISSED);
                    break;
            }
            return myFragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }
}
