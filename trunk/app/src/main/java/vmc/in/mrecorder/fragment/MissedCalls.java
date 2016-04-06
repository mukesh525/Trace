package vmc.in.mrecorder.fragment;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.adapter.Calls_Adapter;
import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.callbacks.EndlessScrollListener;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.datahandler.MDatabase;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.util.JSONParser;
import vmc.in.mrecorder.util.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class MissedCalls extends Fragment  implements SwipeRefreshLayout.OnRefreshListener, TAG, Calls_Adapter.CallClickedListner  {
    private Calls_Adapter adapter;
    public RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String recordLimit;
    private LinearLayout mprogressLayout, retrylayout;
    private LinearLayout pdloadmore;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionsMenu mroot;
    private boolean loading;
    private ArrayList<CallData> callDataArrayList;
    private int offset = 0;
    private int totalCount = 0;
    private String authkey;

    public MissedCalls() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_calls, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipefollowUp);
       // mroot = (RelativeLayout) view.findViewById(R.id.fragment_followup);
        mroot = ((Home) getActivity()).fabMenu;
        mprogressLayout = (LinearLayout) view.findViewById(R.id.mprogressLayout);
        retrylayout = (LinearLayout) view.findViewById(R.id.retryLayout);
        pdloadmore = (LinearLayout) view.findViewById(R.id.loadmorepd1);
        swipeRefreshLayout.setOnRefreshListener(this);
        recordLimit = "10";
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        authkey =Utils.getFromPrefs(getActivity(),AUTHKEY,"N/A");
        Log.d("AUTHKEY",authkey);
        callDataArrayList = new ArrayList<CallData>();
        recyclerView.addOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
               // ((Home) getActivity()).floatingActionButton.hide();
                if (pdloadmore.getVisibility() == View.GONE) {
                    pdloadmore.setVisibility(View.VISIBLE);
                }
                if (!loading) {
                    DownloadMore();
                }

            }

            @Override
            public void onLoadUp() {
               // ((Home) getActivity()).floatingActionButton.hide();
                // if (VisitData != null && VisitData.size() >= MAX) {
                if (pdloadmore.getVisibility() == View.VISIBLE) {
                    pdloadmore.setVisibility(View.GONE);


                }

            }

            @Override
            public void onIdle() {
              //  ((Home) getActivity()).floatingActionButton.show();
            }

        });
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        adapter = new Calls_Adapter(getActivity(), callDataArrayList, mroot, MissedCalls.this);
        adapter.setClickedListner(MissedCalls.this);
        recyclerView.setAdapter(adapter);
        //DownloadCalls();
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        callDataArrayList = CallApplication.getWritabledatabase().getAllCalls(MDatabase.MISSED);
        if (callDataArrayList != null && callDataArrayList.size() > 0) {
            Log.d("TABLE", callDataArrayList.size()+"");
            adapter = new Calls_Adapter(getActivity(), callDataArrayList, mroot, MissedCalls.this);
            adapter.setClickedListner(MissedCalls.this);
            recyclerView.setAdapter(adapter);
        } else {
            DownloadCalls();
        }

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onRefresh() {
        offset = 0;
       // ((Home) getActivity()).floatingActionButton.show();
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);

        }
        DownloadCalls();
    }

    protected void DownloadCalls() {
        if (Utils.onlineStatus1(getActivity())) {
            new DownloadCallData().execute();
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (retrylayout.getVisibility() == View.GONE) {
                retrylayout.setVisibility(View.VISIBLE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
            if (getActivity() != null) {
                Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DownloadCalls();

                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.accent));
                TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }

        }
    }

    protected void DownloadMore() {
        if (Utils.onlineStatus1(getActivity())) {
            new DownloadMoreData().execute();
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (getActivity() != null) {
                Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DownloadMore();

                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.accent));
                TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }

        }


    }

    @Override
    public void OnItemClick(CallData callData, int position) {

    }

    class DownloadCallData extends AsyncTask<Void, Void, ArrayList<CallData>> {
        private String code="n/a", msg="n/a";

        @Override
        protected void onPreExecute() {
            if (mprogressLayout.getVisibility() == View.GONE) {
                mprogressLayout.setVisibility(View.VISIBLE);
            }
            offset = 0;
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }

            loading = true;
            if (retrylayout.getVisibility() == View.VISIBLE) {
                retrylayout.setVisibility(View.GONE);
            }
            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }
            super.onPreExecute();
        }


        @Override
        protected ArrayList<CallData> doInBackground(Void... params) {
            /// TODO Auto-generated method stub
            JSONObject response = null;
            try {
                response = JSONParser.getCallsData(GET_CALL_LIST, authkey, "10", offset + "",
                        CallApplication.getInstance().getDeviceId(), TYPE_MISSED);
                Log.d(TAG, response.toString());
            } catch (Exception e) {
            }
            if (response != null) {


                System.out.println(response);
                JSONArray recordsArray = null;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                try {

                    if (response.has(CODE)) {
                        code = response.getString(CODE);
                    }
                    if (response.has(MESSAGE)) {
                        msg = response.getString(MESSAGE);
                    }
                    callDataArrayList = vmc.in.mrecorder.util.Parser.ParseData(response);



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return callDataArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<CallData> data) {

            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
            }

            if (mprogressLayout.getVisibility() == View.VISIBLE) {
                mprogressLayout.setVisibility(View.GONE);
            }

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);

            }
            loading = false;


            if (data != null && getActivity() != null && data.size() > 0) {
                adapter = new Calls_Adapter(getActivity(), data, mroot, MissedCalls.this);
                adapter.setClickedListner(MissedCalls.this);
                callDataArrayList = data;
                // MyApplication.getWritableDatabase().insertFollowup(data, true);
                CallApplication.getWritabledatabase().insertCallRecords(MDatabase.MISSED, data, true);
                recyclerView.setAdapter(adapter);

            } else if (code.equals("202") || code.equals("401")) {
                if (retrylayout.getVisibility() == View.GONE) {
                    retrylayout.setVisibility(View.VISIBLE);
                }
                if (getActivity() != null && Constants.position == 3) {
                    try {
                        Snackbar snack = Snackbar.make(mroot, "Login to Continue", Snackbar.LENGTH_SHORT)
                                .setAction(getString(R.string.login), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Utils.isLogout(getActivity());

                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.accent));
                        TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.WHITE);
                        snack.show();
                    } catch (Exception e) {

                    }
                }

            } else {
                if (retrylayout.getVisibility() == View.GONE) {
                    retrylayout.setVisibility(View.VISIBLE);
                }

                if (getActivity() != null && Constants.position == 3) {
                    try {
                        Snackbar snack = Snackbar.make(mroot, "No Data Available", Snackbar.LENGTH_SHORT)
                                .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DownloadCalls();

                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.accent));
                        TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.WHITE);
                        snack.show();
                    } catch (Exception e) {

                    }
                }
            }
        }

    }

    class DownloadMoreData extends AsyncTask<Void, Void, ArrayList<CallData>> {
        private String code="n/a", msg="n/a";
        private ArrayList<CallData> data;
        @Override
        protected void onPreExecute() {
            offset=callDataArrayList.size();
            if (pdloadmore.getVisibility() == View.GONE) {
                pdloadmore.setVisibility(View.VISIBLE);
            }
            loading = true;

            super.onPreExecute();
        }


        @Override
        protected ArrayList<CallData> doInBackground(Void... params) {
            /// TODO Auto-generated method stub
            JSONObject response = null;
            try {
                response = JSONParser.getCallsData(GET_CALL_LIST, authkey, "10", offset + "",
                        CallApplication.getInstance().getDeviceId(), TYPE_MISSED);
                Log.d(TAG, response.toString());
            } catch (Exception e) {
            }
            if (response != null) {
                data = new ArrayList<CallData>();
                System.out.println(response);
                JSONArray recordsArray = null;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                try {

                    if (response.has(CODE)) {
                        code = response.getString(CODE);
                    }
                    if (response.has(MESSAGE)) {
                        msg = response.getString(MESSAGE);
                    }
                    data = vmc.in.mrecorder.util.Parser.ParseData(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<CallData> data) {
            loading = false;
            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }

            if (data != null && getActivity() != null && data.size() > 0) {
                callDataArrayList.addAll(data);
                // MyApplication.getWritableDatabase().insertFollowup(data, false);
                CallApplication.getWritabledatabase().insertCallRecords(MDatabase.MISSED, data, true);
                adapter.notifyDataSetChanged();


            } else if (code.equals("202") || code.equals("401")) {

                if (getActivity() != null && Constants.position == 3) {
                    try {
                        Snackbar snack = Snackbar.make(mroot, "Login to Continue", Snackbar.LENGTH_SHORT)
                                .setAction(getString(R.string.login), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Utils.isLogout(getActivity());

                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.accent));
                        TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.WHITE);
                        snack.show();
                    } catch (Exception e) {

                    }
                }

            } else {


                if (getActivity() != null && Constants.position == 3) {
                    try {
                        Snackbar snack = Snackbar.make(mroot, "No Data Available", Snackbar.LENGTH_SHORT)
                                .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DownloadCalls();

                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.accent));
                        TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.WHITE);
                        snack.show();
                    } catch (Exception e) {

                    }
                }
            }
        }

    }

}
