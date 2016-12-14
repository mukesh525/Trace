package vmc.in.mrecorder.fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.adapter.Calls_Adapter;

import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.callbacks.EndlessScrollListener;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.datahandler.MDatabase;
import vmc.in.mrecorder.download.DownloadCalls;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.parser.Requestor;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.JSONParser;
import vmc.in.mrecorder.util.SingleTon;
import vmc.in.mrecorder.util.Utils;


public class AllCalls extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TAG, Calls_Adapter.CallClickedListner,
        ConnectivityReceiver.ConnectivityReceiverListener, DownloadCalls.CallReportFinish {
    private Calls_Adapter adapter;
    public RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String recordLimit;
    private LinearLayout mprogressLayout, retrylayout;
    private LinearLayout pdloadmore;
    private LinearLayoutManager mLayoutManager;
    //private FloatingActionsMenu mroot;
    private boolean loading;
    private ArrayList<CallData> callDataArrayList;
    private int offset = 0;
    private int totalCount = 0;
    private String authkey;
    private SharedPreferences prefs;
    private FloatingActionsMenu mroot;
    private RequestQueue requestQueue;
    private SingleTon volleySingleton;
    private String sessionID, CallType;
    private boolean FirstLoaded = false;

    public AllCalls() {
        // Required empty public constructor
    }

    public static AllCalls newInstance(String type) {
        AllCalls fragment = new AllCalls();
        Bundle args = new Bundle();
        args.putString("TYPE", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            CallType = getArguments().getString("TYPE");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_calls, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipefollowUp);
        //  mroot = (RelativeLayout) view.findViewById(R.id.fragment_followup);
        mroot = ((Home) getActivity()).fabMenu;
        volleySingleton = SingleTon.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        mprogressLayout = (LinearLayout) view.findViewById(R.id.mprogressLayout);
        retrylayout = (LinearLayout) view.findViewById(R.id.retryLayout);
        pdloadmore = (LinearLayout) view.findViewById(R.id.loadmorepd1);
        swipeRefreshLayout.setOnRefreshListener(this);
        recordLimit = "10";
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        callDataArrayList = new ArrayList<CallData>();
        authkey = Utils.getFromPrefs(getActivity(), AUTHKEY, "N/A");
        sessionID = Utils.getFromPrefs(getContext(), SESSION_ID, UNKNOWN);
        Log.d("SESSION_ID", "All Calls OncCreate " + sessionID);
        Log.d("AUTHKEY", authkey);
        retrylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadCalls();
            }
        });
        recyclerView.addOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                //  ((Home) getActivity()).floatingActionButton.hide();
                if (pdloadmore.getVisibility() == View.GONE) {
                    pdloadmore.setVisibility(View.VISIBLE);
                }
                if (!loading) {
                    DownloadMore();
                }

            }


            @Override
            public void onLoadUp() {
                //  ((Home) getActivity()).floatingActionButton.hide();
                // if (VisitData != null && VisitData.size() >= MAX) {
                if (pdloadmore.getVisibility() == View.VISIBLE) {
                    pdloadmore.setVisibility(View.GONE);


                }

            }

            @Override
            public void onIdle() {
                // ((Home) getActivity()).floatingActionButton.show();
            }


        });
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);
        adapter = new Calls_Adapter(getActivity(), callDataArrayList, mroot, AllCalls.this);
        adapter.setClickedListner(AllCalls.this);
        recyclerView.setAdapter(adapter);

        //  DownloadCalls();

        return view;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callDataArrayList = CallApplication.getWritabledatabase().getAllCalls(getTable(CallType));
        if (callDataArrayList != null && callDataArrayList.size() > 0) {
            Log.d("TABLE", callDataArrayList.size() + "");
            Log.d("TABLE", callDataArrayList.get(0).getLocation() + "");
            adapter = new Calls_Adapter(getActivity(), callDataArrayList, mroot, AllCalls.this);
            adapter.setClickedListner(AllCalls.this);
            recyclerView.setAdapter(adapter);
            FirstLoaded = true;
        } else {
            DownloadCalls();
        }

    }

    public int getTable(String type) {

        return type.equals(TYPE_ALL) ? MDatabase.ALL : type.equals(TYPE_INCOMING) ? MDatabase.INBOUND : type.equals(TYPE_OUTGOING) ? MDatabase.OUTBOUND : MDatabase.MISSED;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onRefresh() {
        offset = 0;
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);

        }
        if (!ConnectivityReceiver.isConnected()) {
            Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownloadCalls();

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.accent));

            TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.show();
        } else {
            DownloadCalls();

        }
    }

    protected void DownloadCalls() {
        if (ConnectivityReceiver.isConnected()) {

            if (mprogressLayout.getVisibility() == View.GONE) {
                mprogressLayout.setVisibility(View.VISIBLE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
            if (retrylayout.getVisibility() == View.VISIBLE) {
                retrylayout.setVisibility(View.GONE);
            }
            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }
            loading = true;
            callDataArrayList.clear();
            new DownloadCalls(this, getActivity(), CallType, offset + "", false).execute();
        } else {

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }
            if (retrylayout.getVisibility() == View.VISIBLE) {
                retrylayout.setVisibility(View.GONE);
            }
            if (getActivity() != null) {
                Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DownloadCalls();

                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
                TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }
        }
    }

    protected void DownloadMore() {
        if (ConnectivityReceiver.isConnected()) {
            if (pdloadmore.getVisibility() == View.GONE) {
                pdloadmore.setVisibility(View.VISIBLE);
            }
            loading = true;
            offset = callDataArrayList.size();
            Log.d("DATA", callDataArrayList.size() + "");
            new DownloadCalls(this, getActivity(), CallType, offset + "", true).execute();
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }
            if (retrylayout.getVisibility() == View.VISIBLE) {
                retrylayout.setVisibility(View.GONE);
            }

            if (getActivity() != null) {
                Snackbar snack = Snackbar.make(mroot, "No Internet Connection", Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DownloadMore();

                            }
                        })
                        .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
                TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }

        }


    }

    @Override
    public void OnItemClick(CallData callData, int position) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        if (getActivity() != null) {
            showSnack(isConnected);
        }

    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

            Snackbar snackbar = Snackbar
                    .make(mroot, message, Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    @Override
    public void onCallReportDownLoadFinished(ArrayList<CallData> data, final boolean isMore,String code,String msg) {

        if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }

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

        if (code!=null && code.equals("400")) {
            if (data != null && data.size() > 0 && getActivity() != null) {
                callDataArrayList.addAll(data);
                if (!isMore) {
                    adapter = new Calls_Adapter(getActivity(), callDataArrayList, mroot, AllCalls.this);
                    adapter.setClickedListner(AllCalls.this);
                    FirstLoaded = true;
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }


            } else {
                if (!isMore) {
                    if (retrylayout.getVisibility() == View.GONE) {
                        retrylayout.setVisibility(View.VISIBLE);
                    }
                    if (pdloadmore.getVisibility() == View.VISIBLE) {
                        pdloadmore.setVisibility(View.GONE);
                    }
                }
                if (getActivity() != null && data != null && data.size() <= 0) {
                    try {
                        if (pdloadmore.getVisibility() == View.VISIBLE) {
                            pdloadmore.setVisibility(View.GONE);
                        }
                        if (retrylayout.getVisibility() == View.VISIBLE) {
                            retrylayout.setVisibility(View.GONE);
                        }

                        Snackbar snack = Snackbar.make(mroot, "No Data Available", Snackbar.LENGTH_SHORT)
                                .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!isMore) {
                                            DownloadCalls();
                                        } else {

                                            DownloadMore();
                                        }

                                    }
                                })
                                .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
                        TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.WHITE);
                        snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
                        snack.show();

                    } catch (Exception e) {

                    }
                }
            }


        }else{
            Snackbar snack = Snackbar.make(mroot, msg, Snackbar.LENGTH_SHORT);
//                    .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (!isMore) {
//                                DownloadCalls();
//                            } else {
//
//                                DownloadMore();
//                            }
//
//                        }
//                    })
//                    .setActionTextColor(getResources().getColor(R.color.accent));
            TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snack.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary));
            snack.show();

        }
    }


}
