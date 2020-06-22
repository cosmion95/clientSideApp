package com.example.sockettest1;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    private static final String TAG = "USER_MESSAGES_ACTIVITY";

    DateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm aa");

    public static boolean active = false;

    public static User currentUser;

    private TextView userName;
    private ImageButton backButton;
    private ImageButton sendMessage;

    private MessageAdapter adapter;
    private ArrayList<Message> messages;
    private UserMessagesList userMessagesList;

    private EditText textMessage;

    private RecyclerView messagestRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_messages);
        Toolbar toolbar = findViewById(R.id.user_toolbar);
        setSupportActionBar(toolbar);

        active = true;

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

        String nume = currentUser.getNume();
        if (nume.length() > 25) {
            nume = nume.substring(0, 25) + "...";
        }
        userName.setText(nume);

        setListAndAdapter();

        messagestRecyclerView = findViewById(R.id.user_messages_list);
        messagestRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messagestRecyclerView.setLayoutManager(linearLayoutManager);


        backButton = this.findViewById(R.id.user_back_button);
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
                messagestRecyclerView.scrollToPosition(messages.size() - 1);
            }
        });
    }

    public void setAdapter() {
        adapter = new MessageAdapter(messages);
        messagestRecyclerView.setAdapter(adapter);
        messagestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userMessagesList.setAdapter(adapter);
        adapter.notifyItemInserted(messages.size());
    }


    private void setListAndAdapter() {
        for (UserMessagesList u : MainActivity.friendsList) {
            if (u.getExpeditor().getId().equals(currentUser.getId())) {
                adapter = u.getAdapter();
                messages = u.getMessagesList();
                userMessagesList = u;
            }
        }
    }

    private void addMessageToList(String message) {
        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.DATE, -5);
        String date = df.format(cal.getTime());
        Message msg = new Message(currentUser, message, date, 0, "N");
        //update ordinea utilizatorilor in lista principala
        MainActivity.friendsList.remove(userMessagesList);
        messages.add(msg);
        MainActivity.friendsList.add(0, userMessagesList);
        MainActivity.dbAdapter.insertSent(msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setAdapter();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: on stop called !!!!!!");
        active = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_db_button) {
            MainActivity.dbAdapter.clearDB();
            for (UserMessagesList u : MainActivity.friendsList) {
                u.getMessagesList().clear();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                Log.d(TAG, "run: Trimit mesajul: " + msg);
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