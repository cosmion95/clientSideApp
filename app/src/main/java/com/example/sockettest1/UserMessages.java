package com.example.sockettest1;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class UserMessages extends AppCompatActivity {

    DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss");

    private User currentUser;

    private TextView userName;
    private Button backButton;
    private Button sendMessage;

    private UserMessageAdapter adapter;
    private ArrayList<Message> messages;

    private EditText textMessage;

    private ListView messagestListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_messages);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                currentUser = null;
            } else {
                currentUser = (User) getIntent().getSerializableExtra("CURRENT_USER");
            }
        } else {
            currentUser = (User) savedInstanceState.getSerializable("CURRENT_USER");
        }

        userName = findViewById(R.id.username_text_view);
        userName.setText(currentUser.getNume());

        setListAndAdapter();

        messagestListView = findViewById(R.id.user_messages_list);
        messagestListView.setAdapter(adapter);

        backButton = (Button) this.findViewById(R.id.user_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textMessage = findViewById(R.id.user_text_message);

        sendMessage = this.findViewById(R.id.user_send_button);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessageToList(textMessage.getText().toString());
                new Thread(new SendMessageThread(textMessage.getText().toString())).start();
                textMessage.setText("");
            }
        });
    }


    private void setListAndAdapter() {
        for (UserMessagesList u : MainActivity.friendsList) {
            if (u.getExpeditor().getId().equals(currentUser.getId())) {
                adapter = u.getAdapter();
                messages = u.getMessagesList();
            }
        }
    }

    private void addMessageToList(String message) {
        String date = df.format(Calendar.getInstance().getTime());
        Message msg = new Message(currentUser, message, date, 0, "N");
        messages.add(msg);
        MainActivity.dbAdapter.insertSent(msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    class SendMessageThread implements Runnable {
        String msg;

        SendMessageThread(String msg) {
            //formatez mesajul pe care il trimit catre server
            String formattedMessage = msg + " ~~~ " + currentUser.getId() + " @@@ " + currentUser.getNume();
            this.msg = formattedMessage;
        }

        @Override
        public void run() {
            try {
                OutputStream output = MainActivity.socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.print(msg);
                writer.flush();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}