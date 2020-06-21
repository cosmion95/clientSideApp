package com.example.sockettest1;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionWorker extends Worker {

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
    public static final String SEEN_CODE = "50kX4OBkxdnYwMTAa3md8OODKGnKSm5D7vrb";


    public ConnectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER);
            MainActivity.socket = new Socket(serverAddr, PORT);
            boolean authenticated = false;
            /*try {
                //trimit userul cu care ma conectez - userul cu id 1
                new Thread(new MainActivity.AuthenticationThread("1")).start();
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
                        connectedUser = new User(msgUser[1].split("@@@")[0].trim(), msgUser[1].split("@@@")[1].trim());
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
                        for (UserMessagesList u : friendsList) {
                            if (u.getExpeditor().getId().equals(user.getId())) {
                                for (Message m : u.getMessagesList()) {
                                    if (m.getType() == 0) {
                                        m.setRead("D");
                                    }
                                }
                                updateUI(u.getAdapter(), userAdapter);
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
        }
            catch (Exception e){

            }*/
        } catch (Exception e) {

        }

        return null;
    }
}
