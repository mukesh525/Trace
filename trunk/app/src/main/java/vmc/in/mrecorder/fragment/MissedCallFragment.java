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

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.adapter.NewSpeedDialAdpter;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.Model;
import vmc.in.mrecorder.myapplication.CallApplication;

/**
 * Created by gousebabjan on 4/3/16.
 */
public class MissedCallFragment extends Fragment implements TAG {

    private RecyclerView mRecyclerView;
    NewSpeedDialAdpter mAdapter;
    private TextView default_text;
    private ArrayList<Model> Calllist;

    public static MissedCallFragment newInstance() {
        return new MissedCallFragment();
    }

    public MissedCallFragment() {

    }

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
        Calllist = getSortList("missed", Calllist);
        mAdapter = new NewSpeedDialAdpter(getActivity(), Calllist);
        if (Calllist.size() == 0) {
            default_text.setVisibility(View.VISIBLE);
        } else
            default_text.setVisibility(View.GONE);
        mRecyclerView.setAdapter(mAdapter);
    }


    public ArrayList<Model> getSortList(String name, ArrayList<Model> list) {
        ArrayList<Model> temp = new ArrayList<Model>();
        for (Model model : list) {
            if (model.getCallType().equals(name)) {
                temp.add(model);
            }
        }
        return temp;
    }


}
