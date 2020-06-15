package com.example.sockettest1;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final int HEADER = 64;
    private static final int PORT = 5050;
    private static final String SERVER = "192.168.0.118";
    private static final String FORMAT = "utf-8";
    private static final String DISCONNECT_MESSAGE = "DISCONNECT";
    private static final String AUTH_SUCCESS = "USER_AUTHENTICATED";
    private static final String AUTH_FAIL = "USER_NOT_AUTHENTICATED";
    private static final int MSG_SIZE = 100;
    //ADDR = (SERVER, PORT)

    private Socket socket;
    private EditText textMessage;
    private Button sendMessage;

    private ArrayList<Message> messagesList;
    private ListView messagestListView;

    private MessageAdapter messageListAdapter;

    DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss");

    private String serverMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        messagesList = new ArrayList<>();

        messagestListView = findViewById(R.id.messages_list);

        textMessage = findViewById(R.id.text_message);
        sendMessage = findViewById(R.id.send_button);

        messageListAdapter = new MessageAdapter(this,
                R.layout.message_item, messagesList);

        messagestListView.setAdapter(messageListAdapter);

        new Thread(new ConnectToServer()).start();


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addMessageToList(textMessage.getText().toString());
               new Thread(new SendMessageThread(textMessage.getText().toString())).start();
               textMessage.setText("");
            }
        });


    }

    private void addMessageToList(String message) {
        String date = df.format(Calendar.getInstance().getTime());
        messagesList.add(new Message(message, date, 0));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageListAdapter.notifyDataSetChanged();
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SendMessageThread implements Runnable {

        String msg;

        SendMessageThread(String msg){
            this.msg = msg;
        }

        @Override
        public void run(){
                try {
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);
                    /*int msg_length = msg.length();
                    String send_length = String.valueOf(msg_length);
                    for (int i = 0; i < MSG_SIZE - send_length.length(); i++) {
                        send_length += " ";
                    }

                    Log.d(TAG, "run: send length " + send_length);
                    writer.print(send_length + " " + msg);
                    writer.flush();*/

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

    class ConnectToServer implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER);

                socket = new Socket(serverAddr, PORT);

                boolean authenticated = false;

                try {
                    //trimit userul cu care ma conectez - userul cu id 1
                    new Thread(new SendMessageThread("1")).start();
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    int charsRead = 0;
                    char[] buffer = new char[MSG_SIZE];
                    if (!authenticated) {
                        //primul mesaj de la server e legat de autentificare
                        charsRead = in.read(buffer);
                        serverMessage = new String(buffer).substring(0, charsRead);
                        if (serverMessage.equals(AUTH_SUCCESS)) {
                            //user autentificat cu succes
                            Log.d(TAG, "run: user autentificat cu succes");
                            //obtin lista de utilizatori

                        } else if (serverMessage.equals(AUTH_FAIL)) {
                            //user neidentificat
                            //screen de register?
                            Log.d(TAG, "run: user invalid");
                        }
                    }
                    while (true) {
                        charsRead = in.read(buffer);
                        serverMessage = new String(buffer).substring(0, charsRead);
                        Log.d(TAG, "run: received a new message from server: " + serverMessage);
                        addMessageToList(serverMessage);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

}