package com.example.sockettest1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserMessageAdapter extends ArrayAdapter<Message> {

    private static final String TAG = "UserMessageAdapter";
    private Context mContext;
    private int mResource;

    public UserMessageAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Message> objects) {
        super(context, resource, objects);
        this.mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String message = getItem(position).getMsg();
        String date = getItem(position).getDate();
        int type = getItem(position).getType();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView msgTextView = convertView.findViewById(R.id.user_msg_item);
        TextView dateTextView = convertView.findViewById(R.id.user_date_item);
        TextView typeTextView = convertView.findViewById(R.id.user_msg_type);

        msgTextView.setText(message);
        dateTextView.setText(date);
        typeTextView.setText(String.valueOf(type));

        return convertView;
    }
}
