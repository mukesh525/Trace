package vmc.in.mrecorder.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vmc.in.mrecorder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MissedCalls extends Fragment {


    public MissedCalls() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_all_calls, container, false);
    }

}
