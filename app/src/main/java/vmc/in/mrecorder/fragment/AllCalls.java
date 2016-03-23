package vmc.in.mrecorder.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.adapter.RecyclerAdapter;


public class AllCalls extends Fragment {
    View view;
    RecyclerAdapter adapter;
    public RecyclerView recyclerView;

    public AllCalls() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllCalls.
     */
    // TODO: Rename and change types and number of parameters
    public static AllCalls newInstance(String param1, String param2) {
        AllCalls fragment = new AllCalls();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_all_calls, container, false);
        return view;

    }




}
