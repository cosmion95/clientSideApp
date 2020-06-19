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

        User user = new User(userName, userId);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView userNameTextView = convertView.findViewById(R.id.user_name);
        TextView userIdTextView = convertView.findViewById(R.id.user_id);

        userNameTextView.setText(userName);
        userIdTextView.setText(userId);

        return convertView;
    }
}
