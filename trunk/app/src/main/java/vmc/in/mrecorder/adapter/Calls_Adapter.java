package vmc.in.mrecorder.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.activity.LocationActivity;
import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.fragment.DownloadFile;
import vmc.in.mrecorder.fragment.LoginTask;
import vmc.in.mrecorder.util.ConnectivityReceiver;
import vmc.in.mrecorder.util.Utils;

/**
 * Created by mukesh on 3/24/2016.
 */
public class Calls_Adapter extends RecyclerView.Adapter<Calls_Adapter.CallViewHolder> implements TAG {

    private Context context;
    private ArrayList<CallData> CallDataArrayList;
    private CallClickedListner callClickedListner;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
    private int previousPosition = 0;
    public View mroot;
    public Fragment fragment;


    public Calls_Adapter(Context context, ArrayList<CallData> CallDataArrayList, View mroot, Fragment fragment) {
        this.context = context;
        this.CallDataArrayList = CallDataArrayList;
        this.mroot = mroot;
        this.fragment = fragment;
    }


    public void setClickedListner(CallClickedListner callClickedListner1) {
        this.callClickedListner = callClickedListner1;
    }


    @Override
    public CallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.callitem, parent, false);
        return new CallViewHolder(itemView, CallDataArrayList, callClickedListner);
    }

    @Override
    public void onBindViewHolder(final CallViewHolder holder, int position) {
        try {
            final CallData ci = CallDataArrayList.get(position);

            holder.callerNameTextView.setText(Utils.isEmpty(ci.getName()) ? UNKNOWN : ci.getName());
            holder.callFromTextView.setText(Utils.isEmpty(ci.getCallto()) ? UNKNOWN : ci.getCallto());
            try {
                holder.dateTextView.setText(sdfDate.format(ci.getStartTime()));
                holder.timeTextView.setText(sdfTime.format(ci.getStartTime()));
            } catch (Exception e) {
                Log.d(TAG, e.getMessage().toString());

            }
            holder.groupNameTextView.setText(Utils.isEmpty(ci.getEmpname()) ? UNKNOWN : ci.getEmpname());
            holder.overflow.setOnClickListener(new OnOverflowSelectedListener(context, holder.getAdapterPosition(), CallDataArrayList));
            holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.overflow.performClick();
                }
            });
            holder.img_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ConnectivityReceiver.isConnected()) {
                        if (!Utils.isEmpty(ci.getFilename())) {
                            ((Home) context).playAudio(ci);
                            Log.d(TAG, ci.getFilename());
                        }
                    } else {
                        Toast.makeText(context, "Check Internet Connection..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constants.isRate=true;
                    ((Home) context).onRatingsClick(ci);
                }
            });

            if(ci.getCalltype().equals("0")){
                holder.call_img.setImageResource(R.drawable.ic_call_missed);
            }else if(ci.getCalltype().equals("1")){
                holder.call_img.setImageResource(R.drawable.ic_call_incoming);
            }else {
                holder.call_img.setImageResource(R.drawable.ic_call_outgoing);
            }


            if (!ci.getReview().equals("0")) {
                holder.review.setVisibility(View.VISIBLE);
                holder.rate.setVisibility(View.VISIBLE);
                holder.review.setText(ci.getReview());

            } else {
                holder.review.setVisibility(View.GONE);
                holder.rate.setVisibility(View.GONE);
            }
            holder.img_play.setVisibility(View.VISIBLE);

           // holder.callFrom.setText(ci.getCalltype().equals("0") ? "Call From" : ci.getCalltype().equals("1") ? "Call From" : "Call To");

            if (ci.getCalltype().equals("0") || ci.getFilename().equals("")) {
                holder.img_play.setVisibility(View.INVISIBLE);
                holder.review.setVisibility(View.GONE);
                holder.rate.setVisibility(View.GONE);
            } else {
                if (ci.getSeen() != null && ci.getSeen().equals("1")) {
                    //if api 17  drwle with red image
                    if (Build.VERSION.SDK_INT < 18) {
                        holder.img_play.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_seen));
                    } else
                        holder.img_play.getDrawable().setColorFilter(ContextCompat.getColor(context, R.color.red), PorterDuff.Mode.SRC_ATOP);

                } else {
                    holder.img_play.getBackground().setColorFilter(fetchAccentColor(), PorterDuff.Mode.SRC_ATOP);
                    holder.review.setVisibility(View.GONE);
                    holder.rate.setVisibility(View.GONE);
                }
//                if (!ci.getReview().equals("0")) {
//                    holder.review.setVisibility(View.VISIBLE);
//                    holder.rate.setVisibility(View.VISIBLE);
//                } else {
//                    holder.review.setVisibility(View.GONE);
//                    holder.rate.setVisibility(View.GONE);
//                }
//
//                holder.review.setText(ci.getReview());
//                holder.img_play.setVisibility(View.VISIBLE);
            }

            // holder.groupNameTextView.setText(Utils.isEmpty(ci.getEmail()) ? UNKNOWN : ci.getEmail());

          //  holder.statusTextView.setText(ci.getCalltype().equals("0") ? MISSED : ci.getCalltype().equals("1") ? INCOMING : OUTGOING);
            Log.d(TAG, "" + ci.getCalltype().equals("0"));
            //    holder.contactphoto.setImageBitmap(getFacebookPhoto(ci.getCallto()));







        } catch (Exception e) {
            // Log.d("TAG", e.getMessage());
        }
        ;
        previousPosition = position;

    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    @Override
    public int getItemCount() {
        return CallDataArrayList.size();
    }

    public static class OnOverflowSelectedListener implements View.OnClickListener {
        private Context mContext;
        private int position;
        private ArrayList<CallData> callDatas;
        private DownloadFile fileDownloadFragment;
        private DownloadFile mTaskFragment;

        public OnOverflowSelectedListener(Context context, int pos, ArrayList<CallData> callDatas) {
            mContext = context;
            this.position = pos;
            this.callDatas = callDatas;

        }

        @Override
        public void onClick(final View v) {
            PopupMenu popupMenu = new PopupMenu(mContext, v) {
                @Override
                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.call:
                            if (!Utils.isEmpty(callDatas.get(position).getCallto())) {
                                Utils.makeAcall(callDatas.get(position).getCallto(), (Home) mContext);
                            } else {
                                Toast.makeText(mContext, "Invalid Number", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        case R.id.sms:
                            if (!Utils.isEmpty(callDatas.get(position).getCallto())) {
                                Utils.sendSms(callDatas.get(position).getCallto(), (Home) mContext);
                            } else {
                                Toast.makeText(mContext, "Invalid Number", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        case R.id.location:
                            if (ConnectivityReceiver.isConnected()) {
                                Gson gson = new Gson();
                                String TrackInfo = gson.toJson(callDatas.get(position));
                                Intent intent = new Intent(mContext, LocationActivity.class);
                                intent.putExtra("DATA", TrackInfo);
                                mContext.startActivity(intent);
                            } else {
                                Toast.makeText(mContext, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                            }
                            return true;

                        case R.id.share:
                            ((Home) mContext).onShareFile(callDatas.get(position).getFilename());
                            return true;
                        case R.id.rate:
                            ((Home) mContext).onRatingsClick(callDatas.get(position));
                            return true;

                        default:
                            return super.onMenuItemSelected(menu, item);
                    }
                }
            };

            // Force icons to show
            Object menuHelper = null;
            Class[] argTypes;
            try {
                Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                fMenuHelper.setAccessible(true);
                menuHelper = fMenuHelper.get(popupMenu);
                argTypes = new Class[]{boolean.class};
                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            } catch (Exception e) {
                Log.w("t", "error forcing menu icons to show", e);
                popupMenu.show();
                // Try to force some horizontal offset
                try {
                    Field fListPopup = menuHelper.getClass().getDeclaredField("mPopup");
                    fListPopup.setAccessible(true);
                    Object listPopup = fListPopup.get(menuHelper);
                    argTypes = new Class[]{int.class};
                    Class listPopupClass = listPopup.getClass();
                } catch (Exception e1) {

                    Log.w("T", "Unable to force offset", e);
                }
                return;
            }

            try {
                if (!Utils.isEmpty(callDatas.get(position).getFilename()) && callDatas.get(position).getLocation().length() > 7) {
                    popupMenu.inflate(R.menu.popupmenu_cmsl);
                } else if (!Utils.isEmpty(callDatas.get(position).getFilename()) && callDatas.get(position).getLocation().length() <= 7) {
                    popupMenu.inflate(R.menu.popupmenu_cms);
                } else if (Utils.isEmpty(callDatas.get(position).getFilename()) && callDatas.get(position).getLocation().length() > 7) {
                    popupMenu.inflate(R.menu.popupmenu_cml);
                } else {
                    popupMenu.inflate(R.menu.popupmenu);
                }
//                if (Utils.getFromPrefs(mContext, USERTYPE, DEFAULT).equals("0")) {
//                    if(popupMenu.getMenu().findItem(R.id.share)!=null)
//                    popupMenu.getMenu().findItem(R.id.share).setVisible(false);
//                } else {
//                    if(popupMenu.getMenu().findItem(R.id.share)!=null)
//                    popupMenu.getMenu().findItem(R.id.share).setVisible(true);
//                    //popupMenu.getMenu().findItem(R.id.share).setTitle("Share Edit");
//                }
                popupMenu.show();
            } catch (Exception e) {
                if (e.getMessage().toString() != null) {
                    Log.d("ERROR", e.getMessage().toString());
                }
            }
        }
    }

    public static class CallViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView overflow, rate, home;
        protected TextView callFromTextView, callerNameTextView,
                groupNameTextView, dateTextView, timeTextView, statusTextView, callFrom, review;
        protected ImageButton ibcall, ibmessage;
        private ArrayList<CallData> CallDataArrayList;
        private CallClickedListner callClickedListner;
        public ImageView contactphoto, img_play,call_img;
        private LinearLayout overflowMenu,ratelayout;

        public CallViewHolder(View v, ArrayList<CallData> callDataArrayList, CallClickedListner callClickedListner) {
            super(v);
            overflowMenu = (LinearLayout) v.findViewById(R.id.overflowmenu);
            ratelayout = (LinearLayout) v.findViewById(R.id.rateLayout);
            callFromTextView = (TextView) v.findViewById(R.id.fCallFromTextView);
            //callFrom = (TextView) v.findViewById(R.id.fCallFromLabel);
            review = (TextView) v.findViewById(R.id.review);
            img_play = (ImageView) v.findViewById(R.id.ivplay);
            rate = (ImageView) v.findViewById(R.id.rate);
            call_img = (ImageView) v.findViewById(R.id.call_img);
            home = (ImageView) v.findViewById(R.id.home);
            callerNameTextView = (TextView) v.findViewById(R.id.fCallerNameTextView);
            groupNameTextView = (TextView) v.findViewById(R.id.fGroupNameTextView);
            dateTextView = (TextView) v.findViewById(R.id.fDateTextView);
            timeTextView = (TextView) v.findViewById(R.id.fTimeTextView);
            statusTextView = (TextView) v.findViewById(R.id.fStatusTextView);
            overflow = (ImageView) v.findViewById(R.id.ic_more);
            contactphoto = (ImageView) v.findViewById(R.id.df);

            //callFromTextView=(TextView) v.findViewById(R.id.ch);
            this.callClickedListner = callClickedListner;
            this.CallDataArrayList = callDataArrayList;
            v.setClickable(true);
            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (callClickedListner != null) {
                callClickedListner.OnItemClick(CallDataArrayList.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }


    public interface CallClickedListner {
         void OnItemClick(CallData callData, int position);
    }

    public void setTextTheme(TextView view) {
        int id = Integer.parseInt(Utils.getFromPrefs(context, THEME, "5"));
        ;
        switch (id) {
            case 0:
                view.setTextColor(Color.parseColor("#2196F3"));
                break;
            case 1:
                view.setTextColor(Color.parseColor("#F44336"));
                break;
            case 2:
                view.setTextColor(Color.parseColor("#8BC34A"));
                break;
            default:
                view.setTextColor(Color.parseColor("#FF5722"));
                break;
        }
    }


}
