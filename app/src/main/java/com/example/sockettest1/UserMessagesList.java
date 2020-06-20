package com.example.sockettest1;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserMessagesList {

    private User expeditor;
    private ArrayList<Message> messagesList;
    private UserMessageAdapter adapter;

    public UserMessagesList(Context context, User user) {
        expeditor = user;
        messagesList = new ArrayList<Message>();
        adapter = new UserMessageAdapter(context, R.layout.user_message_item, messagesList);
    }

    public UserMessagesList(Context context, User user, Message msg) {
        expeditor = user;
        messagesList = new ArrayList<Message>();
        messagesList.add(msg);
        adapter = new UserMessageAdapter(context, R.layout.user_message_item, messagesList);
    }

    public UserMessagesList(Context context, User user, ArrayList<Message> messagesList) {
        expeditor = user;
        this.messagesList = messagesList;
        adapter = new UserMessageAdapter(context, R.layout.user_message_item, messagesList);
    }

    public User getExpeditor() {
        return expeditor;
    }

    public ArrayList<Message> getMessagesList() {
        return messagesList;
    }

    public UserMessageAdapter getAdapter() {
        return adapter;
    }

    public void addMessage(Message message) {
        messagesList.add(message);
    }

    public Message getLastMessage() {
        if (messagesList != null && !messagesList.isEmpty()) {
            return messagesList.get(messagesList.size() - 1);
        }
        return null;
    }

    public int getUnreadMessages() {
        int counter = 0;
        for (Message m : messagesList) {
            if (m.getType() == 1 && m.getRead().equals("N")) {
                Log.d(TAG, "getUnreadMessages: gasit mesajul necitit:" + m.getMsg());
                counter++;
            }
        }
        Log.d(TAG, "getUnreadMessages: intorc un total de " + counter + " mesaje necitite");
        return counter;
    }

}
