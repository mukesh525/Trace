package vmc.in.mrecorder.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Date;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.adapter.Ratings_Adapter;
import vmc.in.mrecorder.entity.RateData;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Ratings_Adapter adapter;
    public RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String recordLimit;
    private LinearLayout mprogressLayout, retrylayout;
    private LinearLayout pdloadmore;
    private LinearLayoutManager mLayoutManager;

    private ArrayList<RateData> callDataArrayList;


    public ReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_review, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipefollowUp);
        //  mroot = (RelativeLayout) view.findViewById(R.id.fragment_followup);

        mprogressLayout = (LinearLayout) view.findViewById(R.id.mprogressLayout);
        retrylayout = (LinearLayout) view.findViewById(R.id.retryLayout);
        pdloadmore = (LinearLayout) view.findViewById(R.id.loadmorepd1);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        callDataArrayList = new ArrayList<RateData>();
        for (int i = 0; i < 10; i++) {
            RateData rateData = new RateData();
            if (i % 2 == 0) {
                rateData.setDate(new Date());
                rateData.setName("Rahul Choudhary");
                rateData.setTitle("Execellent");
                rateData.setDesc("Execellent Execellent Execellent Execellent");
                rateData.setRate("3.5");
            }else{
                rateData.setDate(new Date());
                rateData.setName("Mukesh Jha");
                rateData.setTitle("Better");
                rateData.setDesc("Better then previous call");
                rateData.setRate("1.5");
            }
            callDataArrayList.add(rateData);
        }


        adapter = new Ratings_Adapter(getActivity(), callDataArrayList);

        recyclerView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onRefresh() {


        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }


}
