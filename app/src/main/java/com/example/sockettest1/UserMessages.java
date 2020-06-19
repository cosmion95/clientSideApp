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

    private static final String TAG = "MAIN_ACTIVITY";
    private static final int PORT = 5050;
    private static final String SERVER = "192.168.0.118";
    private static final String FORMAT = "utf-8";
    private static final String DISCONNECT_MESSAGE = "DISCONNECT";
    private static final String AUTH_SUCCESS = "USER_AUTHENTICATED";
    private static final String AUTH_FAIL = "USER_NOT_AUTHENTICATED";
    private static final int MSG_SIZE = 100;
    private static final String CONTACTS_START = "CONTACTS_LIST_STARTED";
    private static final String CONTACTS_FINISH = "CONTACTS_LIST_FINISHED";
    private static final int CONTACT_LIST_ITEM = 10000;

    DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss");

    private User currentUser;

    public static Socket socket;

    private TextView userName;
    private Button backButton;
    private Button sendMessage;


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
        messagestListView = findViewById(R.id.user_messages_list);
        messagestListView.setAdapter(getAdapter());

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

    private UserMessageAdapter getAdapter() {
        for (UserMessagesList u : MainActivity.friendsList) {
            if (u.getExpeditor().getId().equals(currentUser.getId())) {
                return u.getAdapter();
            }
        }
        return null;
    }

    private void addMessageToList(String message) {
        String date = df.format(Calendar.getInstance().getTime());
        //messagesList.add(new Message(message, date, 0));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //messageListAdapter.notifyDataSetChanged();
            }
        });
    }

    class SendMessageThread implements Runnable {
        String msg;

        SendMessageThread(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                OutputStream output = socket.getOutputStream();
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