package vmc.in.mrecorder.adapter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.fragment.AllCalls;
import vmc.in.mrecorder.util.Utils;

/**
 * Created by mukesh on 3/24/2016.
 */
public class Calls_Adapter extends RecyclerView.Adapter<Calls_Adapter.CallViewHolder> implements TAG {
    private final ImageLoader imageLoader;
    private final DisplayImageOptions options;
    private Context context;
    private LayoutInflater inflator;
    private ArrayList<CallData> CallDataArrayList;
    private CallClickedListner callClickedListner;
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
    //SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
    private int previousPosition = 0;
    public View mroot;
    public Fragment fragment;


    public Calls_Adapter(Context context, ArrayList<CallData> CallDataArrayList, View mroot, Fragment fragment) {
        this.context = context;
        this.CallDataArrayList = CallDataArrayList;
        this.mroot = mroot;
        this.fragment = fragment;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.def_img)
                .showImageForEmptyUri(R.drawable.def_img)
                .showImageOnFail(R.drawable.error)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();

        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

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


            try {
                holder.dateTextView.setText(sdfDate.format(ci.getStartTime()));
                holder.timeTextView.setText(sdfTime.format(ci.getStartTime()));
            } catch (Exception e) {
                Log.d(TAG,e.getMessage().toString());

            }
            holder.groupNameTextView.setText(Utils.isEmpty(ci.getEmail()) ? UNKNOWN : ci.getEmail());

            holder.statusTextView.setText(ci.getCalltype().equals("0") ? MISSED : ci.getCalltype().equals("1") ? INCOMING : OUTGOING);
            // Log.d("TAG", ci.getStatus());

//            Uri bmpUri = null;
//
//            try {
//                bmpUri = getContactPhoto(ci.getCallfrom());
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.d("TAG", e.getMessage());
//            }
//
//            if (bmpUri != null) {
//                imageLoader.displayImage(bmpUri.toString(), holder.contactphoto, options);
//            } else {
//                Random r = new Random();
//                int a = r.nextInt(255);
//                //holder.contactphoto.setBackgroundColor(Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
//                holder.contactphoto.setImageResource(R.drawable.def_img);
//            }


            holder.contactphoto.setImageBitmap(getFacebookPhoto(ci.getCallto()));


        } catch (Exception e) {
            Log.d("TAG", e.getMessage());
        }
        ;
        previousPosition = position;

    }


    @Override
    public int getItemCount() {
        return CallDataArrayList.size();
    }

    public static class OnOverflowSelectedListener implements View.OnClickListener {
        private Context mContext;
        private int position;
        private ArrayList<CallData> callDatas;


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

            popupMenu.inflate(R.menu.popupmenu);
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
        public ImageView contactphoto;

        public CallViewHolder(View v, ArrayList<CallData> callDataArrayList, CallClickedListner callClickedListner) {
            super(v);

            callFromTextView = (TextView) v.findViewById(R.id.fCallFromTextView);
            callFrom = (TextView) v.findViewById(R.id.fCallFromLabel);

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

    public Uri getContactPhoto(String phoneNumber) throws Exception {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},
                null, null, null);
        cursor.close();

        long contactId = 0;

        if (cursor.moveToFirst()) {
            do {
                contactId = cursor.getLong(cursor
                        .getColumnIndex(ContactsContract.PhoneLookup._ID));
            } while (cursor.moveToNext());
        }

        return getUserPictureUri(contactId);

    }

    private Uri getUserPictureUri(long id) throws Exception {
        Uri person = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, id);

        Uri picUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        try {
            InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(
                    context.getContentResolver(), picUri);
            is.close();
        } catch (FileNotFoundException e) {
            //Contact image does not exist
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            //Log.d(" picture exception", "called");
            return null;
        }

        return picUri;
    }

    public interface CallClickedListner {
        public void OnItemClick(CallData callData, int position);
    }


    public Bitmap getFacebookPhoto(String phoneNumber) {
        Uri phoneUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Uri photoUri = null;
        ContentResolver cr = context.getContentResolver();
        Cursor contact = cr.query(phoneUri,
                new String[]{ContactsContract.Contacts._ID}, null, null, null);

        if (contact.moveToFirst()) {
            long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
            contact.close();
            photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);

        } else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.def_img);
            return defaultPhoto;
        }
        if (photoUri != null) {
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
                    cr, photoUri);
            if (input != null) {
                return BitmapFactory.decodeStream(input);
            }
        } else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.def_img);
            return defaultPhoto;
        }

        Bitmap defaultPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.def_img);
        return defaultPhoto;
    }
}
