package vmc.in.mrecorder.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.activity.LocationActivity;
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

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_item, parent, false);
        return new CallViewHolder(itemView, CallDataArrayList, callClickedListner);
    }

    @Override
    public void onBindViewHolder(CallViewHolder holder, int position) {
        try {
            final CallData ci = CallDataArrayList.get(position);

            holder.callerNameTextView.setText(Utils.isEmpty(ci.getName()) ? UNKNOWN : ci.getName());
            holder.callFromTextView.setText(Utils.isEmpty(ci.getCallto()) ? UNKNOWN : ci.getCallto());
            holder.overflow.setOnClickListener(new OnOverflowSelectedListener(context, holder.getAdapterPosition(), CallDataArrayList));
            holder.callFrom.setText(ci.getCalltype().equals("0") ? "Call From" : ci.getCalltype().equals("1") ? "Call From" : "Call To");

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
            if (ci.getCalltype().equals("0") || ci.getFilename().equals("")) {
                holder.img_play.setVisibility(View.GONE);
            } else {
                if (ci.getSeen() != null && ci.getSeen().equals("1")) {
                    //if api 17  drwle with red image
                    holder.img_play.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.red), PorterDuff.Mode.SRC_ATOP);
                } else {
                    //if api 17 normaldrwle with red image
                    holder.img_play.getBackground().setColorFilter(fetchAccentColor(), PorterDuff.Mode.SRC_ATOP);
                }
                holder.img_play.setVisibility(View.VISIBLE);
            }
            try {
                holder.dateTextView.setText(sdfDate.format(ci.getStartTime()));
                holder.timeTextView.setText(sdfTime.format(ci.getStartTime()));
            } catch (Exception e) {
                Log.d(TAG, e.getMessage().toString());

            }
            // holder.groupNameTextView.setText(Utils.isEmpty(ci.getEmail()) ? UNKNOWN : ci.getEmail());
            holder.groupNameTextView.setText(Utils.isEmpty(ci.getEmpname()) ? UNKNOWN : ci.getEmpname());

            holder.statusTextView.setText(ci.getCalltype().equals("0") ? MISSED : ci.getCalltype().equals("1") ? INCOMING : OUTGOING);
            Log.d(TAG, "" + ci.getCalltype().equals("0"));
            //    holder.contactphoto.setImageBitmap(getFacebookPhoto(ci.getCallto()));


        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
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
                            Gson gson = new Gson();
                            String TrackInfo = gson.toJson(callDatas.get(position));
                            Intent intent = new Intent(mContext, LocationActivity.class);
                            intent.putExtra("DATA", TrackInfo);
                            mContext.startActivity(intent);
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


            if (!Utils.isEmpty(callDatas.get(position).getFilename()) && callDatas.get(position).getLocation().length() > 7) {
                popupMenu.inflate(R.menu.popupmenu_cmsl);
            } else if (!Utils.isEmpty(callDatas.get(position).getFilename()) && callDatas.get(position).getLocation().length() <= 7) {
                popupMenu.inflate(R.menu.popupmenu_cms);
            } else if (Utils.isEmpty(callDatas.get(position).getFilename()) && callDatas.get(position).getLocation().length() > 7) {
                popupMenu.inflate(R.menu.popupmenu_cml);
            } else {
                popupMenu.inflate(R.menu.popupmenu);
            }

            popupMenu.show();
        }
    }

    public static class CallViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView overflow;
        protected TextView callFromTextView, callerNameTextView,
                groupNameTextView, dateTextView, timeTextView, statusTextView, callFrom;
        protected ImageButton ibcall, ibmessage;
        private ArrayList<CallData> CallDataArrayList;
        private CallClickedListner callClickedListner;
        public ImageView contactphoto, img_play;

        public CallViewHolder(View v, ArrayList<CallData> callDataArrayList, CallClickedListner callClickedListner) {
            super(v);

            callFromTextView = (TextView) v.findViewById(R.id.fCallFromTextView);
            callFrom = (TextView) v.findViewById(R.id.fCallFromLabel);
            img_play = (ImageView) v.findViewById(R.id.ivplay);
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
        public void OnItemClick(CallData callData, int position);
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
