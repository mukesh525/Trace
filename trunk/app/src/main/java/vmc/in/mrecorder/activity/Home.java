package vmc.in.mrecorder.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.fragment.AllCalls;
import vmc.in.mrecorder.fragment.InboundCalls;
import vmc.in.mrecorder.fragment.MissedCalls;
import vmc.in.mrecorder.fragment.OutboundCalls;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.provider.GPSTracker;
import vmc.in.mrecorder.service.CallRecorderServiceAll;
import vmc.in.mrecorder.syncadapter.SyncUtils;
import vmc.in.mrecorder.util.CustomTheme;
import vmc.in.mrecorder.util.Utils;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TAG {

    private Toolbar mToolbar;
    public FloatingActionButton floatingActionButton, floatingActionButtonSync;
    private String titles[] = {"ALL", "INBOUND", "OUTBOUND", "MISSED"};
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyPagerAdapter myPagerAdapter;
    private boolean doubleBackToExitPressedOnce;
    private TextView user, email;
    private double latitude, longitude;
    public FloatingActionsMenu fabMenu;
    public CoordinatorLayout coordinatorLayout;
    private Snackbar snack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CustomTheme.onActivityCreateSetTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordi_layout);
        String Firsttym = Utils.getFromPrefs(Home.this, FIRST_TYME, DEFAULT);
        if (Firsttym.equals(DEFAULT)) {
            if (!Utils.isMyServiceRunning(CallRecorderServiceAll.class, Home.this)) {
                CallApplication.getInstance().startRecording();
            }
            Utils.saveToPrefs(Home.this, FIRST_TYME, "TRUE");
        }

        // CallApplication.getInstance().startRecording();
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

        if (android.os.Build.VERSION.SDK_INT > 19) {
            header.setBackgroundResource(R.drawable.side_nav_bar);

        }


        user = (TextView) header.findViewById(R.id.tv_name);
        email = (TextView) header.findViewById(R.id.tv_email);
        String useremail = Utils.getFromPrefs(this, EMAIL, DEFAULT);
        String username = Utils.getFromPrefs(this, NAME, DEFAULT);
        user.setText("Hi " + username);
        email.setText(useremail);
        mDrawer.setNavigationItemSelectedListener(this);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButtonSync = (FloatingActionButton) findViewById(R.id.fab_sync);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Utils.setRecording(Home.this);
                startActivity(new Intent(Home.this, Settings.class));
                fabMenu.collapse();
            }
        });
        floatingActionButtonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncUtils.TriggerRefresh();
                fabMenu.collapse();
            }
        });

        showprefrenceValues();
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
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });
        // fabMenu.setBackground(Color.parseColor("#795548"));

    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("MTrack");
        // Setting Dialog Message
        alertDialog.setMessage("Do you want to logout?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Utils.isLogout(Home.this);

            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
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
        showprefrenceValues();
        if (!Utils.isLogin(Home.this)) {
            Intent intent = new Intent(Home.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            Home.this.startActivity(intent);
            Log.d("Logout", "LOgout on resume");
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
            showSettingsAlert();

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

    public void playAudio(String url) {
        if (url != null && url.length() > 4) {
            Log.d("AUDIO", url);
            // Toast.makeText(HomeActivity.this, url, Toast.LENGTH_LONG).show();
            //Uri myUri = Uri.parse("http://mcube.vmctechnologies.com/sounds/99000220411460096169.wav");
            Uri myUri = Uri.parse(STREAM_TRACKER + url);
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(myUri, "audio/*");
            startActivity(intent);
        }
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
        if (mSelectedId == R.id.settings) {
            startActivity(new Intent(Home.this, Settings.class));
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


    public void showprefrenceValues() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        StringBuilder builder = new StringBuilder();

        builder.append("\n Audio Source: "
                + sharedPrefs.getString("audiosource", "NULL"));

        builder.append("\n Audio Format:"
                + sharedPrefs.getString("audioformat", "NULL"));

        builder.append("\n Sync Frequency: "
                + sharedPrefs.getString("prefSyncFrequency", "NULL"));

        Log.d("SETTINGS", builder.toString());
    }

}
