package com.example.sockettest1;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<Message> messagesList;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        View v;

        public MessageViewHolder(View v) {
            super(v);
            this.v = v;
        }
    }

    public MessageAdapter(ArrayList<Message> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_item, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String message = messagesList.get(position).getMsg();
        String date = messagesList.get(position).getDate();
        int type = messagesList.get(position).getType();
        String read = messagesList.get(position).getRead();

        TextView msgTextView = holder.itemView.findViewById(R.id.user_msg_item);
        TextView dateTextView = holder.itemView.findViewById(R.id.user_date_item);
        ImageView readImageView = holder.itemView.findViewById(R.id.user_msg_read);
        RelativeLayout relativeLayout = holder.itemView.findViewById(R.id.rl_squircle);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();

        msgTextView.setText(message);
        dateTextView.setText(date);
        dateTextView.setAlpha(0.5f);

        //mesaj primit
        if (type == 1) {
            Log.d(TAG, "onBindViewHolder: intrat in type == 1 pentru mesajul " + message);
            readImageView.setVisibility(View.GONE);
        }
        //mesaj trimis
        else {
            Log.d(TAG, "onBindViewHolder: intrat in type == 0 pentru mesajul " + message);
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
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

}
