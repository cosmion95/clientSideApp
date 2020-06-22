package com.example.sockettest1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<UserMessagesList> userList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UserMessagesList item);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View v;

        public UserViewHolder(View v) {
            super(v);
            this.v = v;
        }

        public void bind(final UserMessagesList item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public UserAdapter(ArrayList<UserMessagesList> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(userList.get(position), listener);
        String userName = userList.get(position).getExpeditor().getNume();
        String userId = userList.get(position).getExpeditor().getId();
        Message lMessage = userList.get(position).getLastMessage();
        int unreadMessages = userList.get(position).getUnreadMessages();
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

        TextView userInitialTextView = holder.v.findViewById(R.id.user_item_circle);
        TextView userNameTextView = holder.v.findViewById(R.id.user_name);
        TextView userIdTextView = holder.v.findViewById(R.id.user_id);
        TextView lastMessageTextView = holder.v.findViewById(R.id.user_last_message);
        TextView lastDateTextView = holder.v.findViewById(R.id.user_last_date);
        TextView unreadMessagesTextView = holder.v.findViewById(R.id.user_item_last_message_circle);

        GradientDrawable drawable = (GradientDrawable) holder.v.getContext().getDrawable(R.drawable.name_circle);

        int circleColor = getMatColor(holder.v.getContext(), "500");
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
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private int getMatColor(Context mContext, String typeColor) {
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

    public void setOnClick(OnItemClickListener listener) {
        this.listener = listener;
    }
}


