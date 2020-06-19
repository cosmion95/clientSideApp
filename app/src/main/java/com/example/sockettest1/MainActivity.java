package com.example.sockettest1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
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
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

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

    public static Socket socket;

    public static ArrayList<UserMessagesList> friendsList;

    private ListView usersListView;

    public static UserAdapter userAdapter;

    DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss");

    private String serverMessage = "";

    private boolean canDisconnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        friendsList = new ArrayList<>();
        usersListView = findViewById(R.id.users_list);

        userAdapter = new UserAdapter(this, R.layout.user_item, friendsList);
        usersListView.setAdapter(userAdapter);

        new Thread(new ConnectToServer(this)).start();

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserMessagesList user = (UserMessagesList) parent.getAdapter().getItem(position);
                Intent intent = new Intent(MainActivity.this, UserMessages.class);
                intent.putExtra("CURRENT_USER", user.getExpeditor());
                startActivity(intent);
            }
        });


    }

    private void receiveMessage(String message, Context context) {
        String date = df.format(Calendar.getInstance().getTime());
        //messagesList.add(new Message(message, date, 0));
        //obtin expeditorul si mesajul
        boolean found = false;
        String[] msgUser = message.split("~~~");
        User user = new User(msgUser[1].split("@@@")[0].trim(), msgUser[1].split("@@@")[1].trim());
        Message msg = new Message(msgUser[0].trim(), date, 1);
        //verific daca am alte mesaje de la acelasi expeditor
        for (UserMessagesList u : friendsList) {
            if (u.getExpeditor().getId().equals(user.getId())) {
                //mai am mesaje de la aceasta persoana
                u.getMessagesList().add(msg);
                found = true;
                updateUI(u.getAdapter(), userAdapter);

                Log.d(TAG, "addMessageToList: message received from already added person " + user.getNume());
                break;
            }
        }
        if (!found) {
            Log.d(TAG, "addMessageToList: message received from unknown person: " + user.getNume());
            UserMessagesList newUser = new UserMessagesList(context, user, msg);
            friendsList.add(newUser);
            updateUI(newUser.getAdapter(), userAdapter);
        }

    }

    private void updateUI(final UserMessageAdapter uma, final UserAdapter ua) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uma.notifyDataSetChanged();
                ua.notifyDataSetChanged();
            }
        });
    }

    private void addUserToList(User user, Context context) {
        friendsList.add(new UserMessagesList(context, user));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ON STOP");
        //new Thread(new DisconnectFromSocket()).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ON DESTROY");
        new Thread(new DisconnectFromSocket()).start();
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

    class DisconnectFromSocket implements Runnable {

        @Override
        public void run() {
            try {
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                writer.print(DISCONNECT_MESSAGE);
                writer.flush();

                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class SendMessageThread implements Runnable {
        String msg;
        SendMessageThread(String msg) {
            this.msg = msg;
        }
        @Override
        public void run(){
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

    class ConnectToServer implements Runnable {
        private Context context;
        public ConnectToServer(Context context) {
            this.context = context;
        }
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
                            //obtin lista de utilizatori
                            charsRead = in.read(buffer);
                            serverMessage = new String(buffer).substring(0, charsRead);
                            if (serverMessage.equals(CONTACTS_START)) {
                                boolean finished = false;
                                char[] contactBuffer = new char[CONTACT_LIST_ITEM];
                                while (!finished) {
                                    charsRead = in.read(contactBuffer);
                                    serverMessage = new String(contactBuffer).substring(0, charsRead);
                                    if (serverMessage.contains("///")) {
                                        String[] userConcat = serverMessage.split("&&&");
                                        for (String x : userConcat) {
                                            String[] user = x.split("///");
                                            addUserToList(new User(user[0], user[1]), context);
                                        }
                                    }
                                    if (serverMessage.equals(CONTACTS_FINISH)) {
                                        finished = true;
                                        for (UserMessagesList u : friendsList) {
                                            Log.d(TAG, "friend " + u.getExpeditor().getNume());
                                        }
                                    }
                                }
                            }
                        } else if (serverMessage.equals(AUTH_FAIL)) {
                            //user neidentificat
                            //screen de register?
                            Log.d(TAG, "run: user invalid");
                        }
                    }
                    while (!socket.isClosed() && socket.isConnected()) {
                        charsRead = in.read(buffer);
                        serverMessage = new String(buffer).substring(0, charsRead);
                        Log.d(TAG, "run: received a new message from server: " + serverMessage);
                        receiveMessage(serverMessage, context);
                    }
                } catch (SocketException s) {
                    Log.d(TAG, "run: socket connection has been closed");
                } catch (Exception e) {
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