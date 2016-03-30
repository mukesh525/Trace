package vmc.in.mrecorder.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.fragment.AllCalls;
import vmc.in.mrecorder.fragment.InboundCalls;
import vmc.in.mrecorder.fragment.MissedCalls;
import vmc.in.mrecorder.fragment.OutboundCalls;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.service.CallRecorderServiceAll;
import vmc.in.mrecorder.util.Utils;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TAG {

    private Toolbar mToolbar;
    public FloatingActionButton floatingActionButton;
    private String titles[] = {"ALL", "INBOUND", "OUTBOUND", "MISSED"};
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyPagerAdapter myPagerAdapter;
    private boolean doubleBackToExitPressedOnce;
    private TextView user, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (!Utils.isMyServiceRunning(CallRecorderServiceAll.class, Home.this)) {
            CallApplication.getInstance().startRecording();
        }
        mDrawer = (NavigationView) findViewById(R.id.nav_view);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myPagerAdapter);
        mTabLayout.setTabsFromPagerAdapter(myPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mDrawer.setItemIconTintList(null);
        View header = mDrawer.getHeaderView(0);
        user = (TextView) header.findViewById(R.id.tv_name);
        email = (TextView) header.findViewById(R.id.tv_email);
        String useremail =Utils.getFromPrefs(this, EMAIL, DEFAULT);
        String username = Utils.getFromPrefs(this, NAME, DEFAULT);
        user.setText("Hi " + username);
        email.setText(useremail);
        mDrawer.setNavigationItemSelectedListener(this);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setRecording(Home.this);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Constants.position = position;
                onNavigationItemSelected(mDrawer.getMenu().getItem(position));


            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });


    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment = null;
            switch (position) {
                case 0:
                    myFragment = new AllCalls();
                    break;
                case 1:
                    myFragment = new InboundCalls();
                    break;
                case 2:
                    myFragment = new OutboundCalls();
                    break;
                case 3:
                    myFragment = new MissedCalls();
                    break;
            }
            return myFragment;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!Utils.isLogin(Home.this)) {
            Intent intent = new Intent(Home.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            Home.this.startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Utils.isLogout(this);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        item.setChecked(true);
        navigate(item.getItemId());
        return true;
    }

    private void navigate(int mSelectedId) {
        if (mSelectedId == R.id.all) {
            setSelection(0);
        }
        if (mSelectedId == R.id.inbound) {
            setSelection(1);
        }
        if (mSelectedId == R.id.outbound) {
            setSelection(2);
        }
        if (mSelectedId == R.id.missed) {
            setSelection(3);
        }
        if (mSelectedId == R.id.cal_log) {
            startActivity(new Intent(Home.this, ContactsActivity.class));
        }
        if (mSelectedId == R.id.nav_help_feedback) {
            startActivity(new Intent(Home.this, Feedback.class));
        }


        invalidateOptionsMenu();
    }

    private void setSelection(int item) {
        hideDrawer();
        mTabLayout.setScrollPosition(item, 0f, true);
        mViewPager.setCurrentItem(item);
    }

    private void showDrawer() {
        this.mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer() {
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
    }
}
