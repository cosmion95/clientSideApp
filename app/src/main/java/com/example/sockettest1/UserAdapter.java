package com.example.sockettest1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class UserAdapter extends ArrayAdapter<UserMessagesList> {

    private static final String TAG = "UserAdapter";
    private Context mContext;
    private int mResource;

    public UserAdapter(@NonNull Context context, int resource, @NonNull ArrayList<UserMessagesList> objects) {
        super(context, resource, objects);
        this.mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String userName = Objects.requireNonNull(getItem(position)).getExpeditor().getNume();
        String userId = Objects.requireNonNull(getItem(position)).getExpeditor().getId();
        Message lMessage = Objects.requireNonNull(getItem(position)).getLastMessage();
        int unreadMessages = Objects.requireNonNull(getItem(position)).getUnreadMessages();
        String lastMessage = "";
        String lastDate = "";
        if (lMessage != null) {
            lastMessage = lMessage.getMsg();
            lastDate = lMessage.getDate();
        }
        User user = new User(userName, userId);

        String formattedDate = "";
        DateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm aa");
        String date = df.format(Calendar.getInstance().getTime());
        if (lastDate.split(" ")[0].equals(date.split(" ")[0])) {
            formattedDate = lastDate.split(" ")[1] + " " + lastDate.split(" ")[2];
        } else {
            formattedDate = lastDate.split(" ")[0];
        }

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView userInitialTextView = convertView.findViewById(R.id.user_item_circle);
        TextView userNameTextView = convertView.findViewById(R.id.user_name);
        TextView userIdTextView = convertView.findViewById(R.id.user_id);
        TextView lastMessageTextView = convertView.findViewById(R.id.user_last_message);
        TextView lastDateTextView = convertView.findViewById(R.id.user_last_date);
        TextView unreadMessagesTextView = convertView.findViewById(R.id.user_item_last_message_circle);

        GradientDrawable drawable = (GradientDrawable) mContext.getDrawable(R.drawable.name_circle);

        int circleColor = getMatColor("500");
        drawable.setColor(circleColor);
        drawable.setStroke(1, circleColor);

        userNameTextView.setText(userName);
        userIdTextView.setText(userId);
        lastMessageTextView.setText(lastMessage);
        lastDateTextView.setText(formattedDate);
        userInitialTextView.setText(userName.substring(0, 1));

        lastMessageTextView.setAlpha(0.5f);

        //are mesaje necitite
        if (unreadMessages > 0) {
            unreadMessagesTextView.setText(String.valueOf(unreadMessages));
            unreadMessagesTextView.setVisibility(View.VISIBLE);
            lastDateTextView.setTextColor(Color.parseColor("#5bb0a9"));
        } else {
            lastDateTextView.setAlpha(0.5f);
        }

        return convertView;
    }

    private int getMatColor(String typeColor) {
        int returnColor = Color.BLACK;
        int arrayId = mContext.getResources().getIdentifier("mdcolor_" + typeColor, "array", mContext.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = mContext.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }
}
