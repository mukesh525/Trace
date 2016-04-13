package vmc.in.mrecorder.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.adapter.NewSpeedDialAdpter;
import vmc.in.mrecorder.entity.Model;
import vmc.in.mrecorder.myapplication.CallApplication;

/**
 * Created by Jay Rambhia on 11/06/15.
 */
public class AllCallsFragment extends Fragment {

    private RecyclerView mRecyclerView;

    NewSpeedDialAdpter mAdapter;
    private TextView default_text;
    private ArrayList<Model> Calllist;

    public static AllCallsFragment newInstance() {
        return new AllCallsFragment();
    }

    public AllCallsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstaceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_speeddial_layout, parent, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        default_text = (TextView) rootView.findViewById(R.id.default_text);
        Calllist = new ArrayList<Model>();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Calllist = CallApplication.getWritabledatabase().getAllOfflineCalls();
        if (Calllist.size() > 0) {
            Collections.sort(Calllist, Collections.reverseOrder());
        }

        mAdapter = new NewSpeedDialAdpter(getActivity(), Calllist);
        if (Calllist.size() == 0) {
            default_text.setVisibility(View.VISIBLE);
        } else
            default_text.setVisibility(View.GONE);
        mRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }


}
