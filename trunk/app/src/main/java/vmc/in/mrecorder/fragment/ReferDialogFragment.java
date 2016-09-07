package vmc.in.mrecorder.fragment;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.util.Utils;

/**
 * Created by mukesh on 2/12/15.
 */
public class ReferDialogFragment extends DialogFragment {
    static String DialogboxTitle;
    EditText txtname, txtemail, txtmessage, txtnum;
    Button btnsubmit, btnCancel;
    String siteID;
    private ViewPager mViewPager;
    private MyPagerAdapter myPagerAdapter;
    private TabLayout mTabLayout;


    //---empty constructor required
    public ReferDialogFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(
                R.layout.referal, container);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        myPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(myPagerAdapter);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (Utils.tabletSize(getContext()) < 5.0) {
            window.setLayout(440, 520);
        } else if (Utils.tabletSize(getContext()) < 6.0){
            window.setLayout(650, 700);
        }else{
            window.setLayout(550, 550);
        }

        window.setGravity(Gravity.CENTER);
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
        private String titles[] = {"RATE", "REVIEWS"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment = null;
            switch (position) {
                case 0:
                    myFragment = new RateFragment();
                    break;
                case 1:
                    myFragment = new ReviewFragment();
                    break;
            }
            return myFragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
            //  return null;
        }
    }
}
