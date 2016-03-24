package vmc.in.mrecorder.activity;

import android.content.Intent;
import android.os.Bundle;
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
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FloatingActionButton floatingActionButton;
    ContactsActivity ca;
    TextView tv_name;
    private String titles[] = {"ALL", "INBOUND", "OUTBOUND", "MISSED"};
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyPagerAdapter myPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        CallApplication.getInstance().startRecording();
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
        mDrawer.setNavigationItemSelectedListener(this);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setRecording(Home.this);
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


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllCalls(), "ALL");
        adapter.addFragment(new InboundCalls(), "INBOUND");
        adapter.addFragment(new OutboundCalls(), "OUTBOUND");
        adapter.addFragment(new MissedCalls(), "MISSED");
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
            // return null to display only the icon
            //return null;
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
            super.onBackPressed();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            SharedPreferences pref = getSharedPreferences("Mydata", Context.MODE_PRIVATE);
//            pref.edit().clear().commit();
            //CallApplication.sp.edit().putInt("type", 1);
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


        invalidateOptionsMenu();
    }

    private void setSelection(int item) {
        hideDrawer();
        mTabLayout.setScrollPosition(item, 0f, true);
        mViewPager.setCurrentItem(item);
// onNavigationItemSelected(mDrawer.getMenu().getItem(0));
    }

    private void showDrawer() {
        this.mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer() {
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
    }
}
