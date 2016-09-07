package vmc.in.mrecorder.fragment;


import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import vmc.in.mrecorder.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RateFragment extends Fragment {


    private RatingBar ratingBar;
    private TextView rateComment;

    public RateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_rate, container, false);
        ratingBar = (RatingBar) view.findViewById(R.id.rate);
        rateComment = (TextView) view.findViewById(R.id.rateComment);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(fetchAccentColor(), PorterDuff.Mode.SRC_ATOP);


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setRateComment(rating);

            }
        });
        return view;

}

   public void setRateComment(float rating){

       int val=Math.round(rating);
        switch (val){
            case 1:
                rateComment.setText("Hated it");
                break;
            case 2:
                rateComment.setText("Disliked it");
                break;
            case 3:
                rateComment.setText("It's ok");
                break;
            case 4:
                rateComment.setText("Liked it");
                break;
            case 5:
                rateComment.setText("Loved it");
                break;
        }


   }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = getActivity().obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }
}
