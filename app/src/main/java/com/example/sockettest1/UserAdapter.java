package com.example.sockettest1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

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
        String userName = getItem(position).getExpeditor().getNume();
        String userId = getItem(position).getExpeditor().getId();
        Message lMessage = getItem(position).getLastMessage();
        int unreadMessages = getItem(position).getUnreadMessages();

        String lastMessage = "";
        String lastDate = "";
        if (lMessage != null) {
            lastMessage = lMessage.getMsg();
            lastDate = lMessage.getDate();
        }

        User user = new User(userName, userId);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView userNameTextView = convertView.findViewById(R.id.user_name);
        TextView userIdTextView = convertView.findViewById(R.id.user_id);
        TextView lastMessageTextView = convertView.findViewById(R.id.user_last_message);
        TextView lastDateTextView = convertView.findViewById(R.id.user_last_date);
        TextView unreadMessagesTextView = convertView.findViewById(R.id.user_new_messages);

        userNameTextView.setText(userName);
        userIdTextView.setText(userId);
        lastMessageTextView.setText(lastMessage);
        lastDateTextView.setText(lastDate);

        Log.d(TAG, "getView: unread messages este " + unreadMessages + " !!!!!!!!");
        if (unreadMessages > 0) {
            unreadMessagesTextView.setText(String.valueOf(unreadMessages));
        }

        return convertView;
    }
}
