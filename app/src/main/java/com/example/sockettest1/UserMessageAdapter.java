package com.example.sockettest1;// 1 = mesaj prim

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
        User user = getItem(position).getUser();
        String read = getItem(position).getRead();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView msgTextView = convertView.findViewById(R.id.user_msg_item);
        TextView dateTextView = convertView.findViewById(R.id.user_date_item);
        ImageView readImageView = convertView.findViewById(R.id.user_msg_read);
        RelativeLayout relativeLayout = convertView.findViewById(R.id.rl_squircle);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();

        msgTextView.setText(message);
        dateTextView.setText(date);
        dateTextView.setAlpha(0.5f);

        //mesaj primit
        if (type == 1) {
            readImageView.setVisibility(View.GONE);
        }
        //mesaj trimis
        else {
            relativeLayout.setBackgroundResource(R.drawable.squircle_sent);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            relativeLayout.setLayoutParams(params);
            if (read.equals("D")) {
                //iconita pentru citit
                readImageView.setBackgroundResource(R.drawable.ic_check_circle_full);
            } else {
                //iconita pentru necitit
                readImageView.setBackgroundResource(R.drawable.ic_check_circle_empty);
            }
        }

        return convertView;
    }
}
