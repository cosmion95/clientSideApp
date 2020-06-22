package com.example.sockettest1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
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

public class MainActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final int PORT = 5050;
    private static final String SERVER = "192.168.0.118";
    private static final String FORMAT = "utf-8";
    private static final String DISCONNECT_MESSAGE = "DISCONNECT";
    private static final String AUTH_SUCCESS = "USER_AUTHENTICATED";
    private static final String AUTH_FAIL = "USER_NOT_AUTHENTICATED";
    private static final int MSG_SIZE = 1000;
    private static final String CONTACTS_START = "CONTACTS_LIST_STARTED";
    private static final String CONTACTS_FINISH = "CONTACTS_LIST_FINISHED";
    private static final int CONTACT_LIST_ITEM = 10000;
    public static final String SEEN_CODE = "50kX4OBkxdnYwMTAa3md8OODKGnKSm5D7vrb";

    public static DBAdapter dbAdapter;
    public static Socket socket;
    public static ArrayList<UserMessagesList> friendsList;

    private static RecyclerView usersRecView;
    public static UserAdapter userAdapter;

    DateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm aa");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbAdapter = new DBAdapter(this);

        friendsList = new ArrayList<>();
        usersRecView = findViewById(R.id.users_list);

        setUserAdapter();
        userAdapter.setOnClick(this);

        AsyncConnection runner = new AsyncConnection(this);
        runner.execute();

    }

    public void setUserAdapter() {
        userAdapter = new UserAdapter(friendsList);
        usersRecView.setAdapter(userAdapter);
        usersRecView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter.setOnClick(this);
        userAdapter.notifyDataSetChanged();
    }

    public void setMessageAdapter(UserMessagesList user) {
        user.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onItemClick(UserMessagesList item) {
        //dau seen
        setSeenMessages(item.getExpeditor());
        Intent intent = new Intent(MainActivity.this, UserMessages.class);
        intent.putExtra("CURRENT_USER", item.getExpeditor());
        startActivity(intent);
    }


    private void receiveMessage(String message, Context context) {
        String date = df.format(Calendar.getInstance().getTime());
        //obtin expeditorul si mesajul
        boolean found = false;
        String[] msgUser = message.split("~~~");
        User user = new User(msgUser[1].split("@@@")[0].trim(), msgUser[1].split("@@@")[1].trim());
        Message msg = new Message(user, msgUser[0].trim(), date, 1, "N");
        //verific daca am alte mesaje de la acelasi expeditor
        for (UserMessagesList u : friendsList) {
            if (u.getExpeditor().getId().equals(user.getId())) {
                //mai am mesaje de la aceasta persoana
                //mut persoana la inceputul listei
                friendsList.remove(u);
                friendsList.add(0, u);
                u.getMessagesList().add(msg);
                found = true;
                dbAdapter.insertReceived(msg);
                //dau seen daca am deschisa activitatea user_messages cu user
                if (UserMessages.active && UserMessages.currentUser.getId().equals(user.getId())) {
                    setSeenMessages(u.getExpeditor());
                }
                updateUI(u);
                Log.d(TAG, "addMessageToList: message received from already added person " + user.getNume());
                break;
            }
        }
        if (!found) {
            Log.d(TAG, "addMessageToList: message received from unknown person: " + user.getNume());
            UserMessagesList newUser = new UserMessagesList(context, user, msg);
            friendsList.add(newUser);
            dbAdapter.insertReceived(msg);
            updateUI(newUser);
        }
    }

    private void updateUI(final UserMessagesList user) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setMessageAdapter(user);
                setUserAdapter();
            }
        });
    }

    private void addUserToList(User user, Context context) {
        //obtin lista de mesaje existente din db local
        ArrayList<Message> existentMessages = dbAdapter.getMessages(user.getId());
        Log.d(TAG, "addUserToList: mesajele existente pentru userul " + user.getNume() + " sunt: ");
        for (Message m : existentMessages) {
            Log.d(TAG, "msg: " + m.getMsg());
        }
        friendsList.add(new UserMessagesList(context, user, existentMessages));
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
        new Thread(new DisconnectFromSocket()).start();
        super.onDestroy();
        Log.d(TAG, "onDestroy: ON DESTROY");
    }

    @Override
    protected void onPostResume() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUserAdapter();
            }
        });
        super.onPostResume();
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
            dbAdapter.clearDB();
            for (UserMessagesList u : friendsList) {
                u.getMessagesList().clear();
            }
            new Thread(new DisconnectFromSocket()).start();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setSeenMessages(User u) {
        //face seen la mesaje si updateaza interfata
        new Thread(new SendSeenSignal(SEEN_CODE, u)).start();
        dbAdapter.setReadNewMessages(u);
        for (UserMessagesList userMessagesList : friendsList) {
            if (u.getId().equals(userMessagesList.getExpeditor().getId())) {
                for (Message m : userMessagesList.getMessagesList()) {
                    if (m.getType() == 1 && m.getRead().equals("N")) {
                        m.setRead("D");
                    }
                }
            }
        }
    }

    public void setSeenMessages(final UserMessagesList u, final ArrayList<Integer> messagesIndex) {
        //update interfata dupa ce am primit semnalul ca mesajele au fost citite
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Integer i : messagesIndex) {
                    u.getAdapter().notifyItemChanged(i);
                }
                setUserAdapter();
            }
        });
    }

    static class DisconnectFromSocket implements Runnable {
        @Override
        public void run() {
            try {
                Log.d(TAG, "run: sending disconnect message");
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

    static class AuthenticationThread implements Runnable {
        String msg;

        AuthenticationThread(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.print(msg);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class SendSeenSignal implements Runnable {
        String msg;
        User target;
        SendSeenSignal(String msg, User target) {
            //formatez mesajul pe care il trimit catre server
            this.msg = msg + " ~~~ " + target.getId() + " @@@ " + target.getNume();
            this.target = target;
        }
        @Override
        public void run() {
            try {
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.print(msg);
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class AsyncConnection extends AsyncTask {
        private Context context;
        public AsyncConnection(Context context) {
            this.context = context;
        }
        @Override
        protected Object doInBackground(Object[] objects) {
            String serverMessage = "";
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER);
                socket = new Socket(serverAddr, PORT);
                boolean authenticated = false;
                try {
                    //trimit userul cu care ma conectez - userul cu id 1
                    new Thread(new AuthenticationThread("1")).start();
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    int charsRead = 0;
                    char[] buffer = new char[MSG_SIZE];
                    if (!authenticated) {
                        //primul mesaj de la server e legat de autentificare
                        charsRead = in.read(buffer);
                        serverMessage = new String(buffer).substring(0, charsRead);
                        if (serverMessage.contains(AUTH_SUCCESS)) {
                            //user autentificat cu succes, obtin userul din mesaj
                            String[] msgUser = serverMessage.split("~~~");
                            User connectedUser = new User(msgUser[1].split("@@@")[0].trim(), msgUser[1].split("@@@")[1].trim());
                            Log.d(TAG, "run: succesfully authenticated with user " + connectedUser.getNume());
                            //obtin lista de utilizatori
                            charsRead = in.read(buffer);
                            serverMessage = new String(buffer).substring(0, charsRead);
                            if (serverMessage.equals(CONTACTS_START)) {
                                boolean finished = false;
                                char[] contactBuffer = new char[CONTACT_LIST_ITEM];
                                while (!finished) {
                                    charsRead = in.read(contactBuffer);
                                    serverMessage = new String(contactBuffer).substring(0, charsRead);
                                    Log.d(TAG, "doInBackground: RECEIVED MESSAGE: " + serverMessage);
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
                    while (true) {
                        charsRead = in.read(buffer);
                        serverMessage = new String(buffer).substring(0, charsRead);
                        if (serverMessage.contains(SEEN_CODE)) {
                            //update mesaje, interfata si db
                            String[] msgUser = serverMessage.split("~~~");
                            User user = new User(msgUser[1].split("@@@")[0].trim(), msgUser[1].split("@@@")[1].trim());
                            UserMessagesList userMessagesList = null;
                            ArrayList<Integer> changedReadStateIndex = new ArrayList<Integer>();
                            int counter = 0;
                            for (UserMessagesList u : friendsList) {
                                if (u.getExpeditor().getId().equals(user.getId())) {
                                    userMessagesList = u;
                                    for (Message m : u.getMessagesList()) {
                                        if (m.getType() == 0 && m.getRead().equals("N")) {
                                            m.setRead("D");
                                            changedReadStateIndex.add(counter);
                                        }
                                        counter++;
                                    }
                                    setSeenMessages(userMessagesList, changedReadStateIndex);
                                    dbAdapter.setReadSentMessages(user);
                                    break;
                                }
                            }
                            //nu inregistra mesajul de seen ca si mesaj nou
                            continue;
                        }
                        Log.d(TAG, "run: received a new message from server: " + serverMessage);
                        publishProgress(serverMessage);
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
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onProgressUpdate(Object[] values) {
            String[] msgUser = values[0].toString().split("~~~");
            String user = msgUser[1].split("@@@")[1].trim();
            String msg = msgUser[0].trim();
            super.onProgressUpdate(values);
            String CHANNEL_ID = "NOTIFICATION_CHANNEL";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("New message from " + user)
                    .setContentText(msg)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "nume canal", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            notificationManager.notify(0, builder.build());
        }
    }

}