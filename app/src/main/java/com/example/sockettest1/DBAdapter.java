package com.example.sockettest1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class DBAdapter {
    myDbHelper myhelper;

    public DBAdapter(Context context) {
        myhelper = new myDbHelper(context);
    }

    public long insertReceived(Message message) {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.USER_ID, Integer.parseInt(message.getUser().getId()));
        contentValues.put(myDbHelper.USER_NAME, message.getUser().getNume());
        contentValues.put(myDbHelper.MESSAGE, message.getMsg());
        contentValues.put(myDbHelper.DATE, message.getDate());
        contentValues.put(myDbHelper.TYPE, "1");
        contentValues.put(myDbHelper.READ, "N");
        long id = dbb.insert(myDbHelper.RECEIVED_TABLE_NAME, null, contentValues);
        return id;
    }

    public long insertSent(Message message) {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.USER_ID, Integer.parseInt(message.getUser().getId()));
        contentValues.put(myDbHelper.USER_NAME, message.getUser().getNume());
        contentValues.put(myDbHelper.MESSAGE, message.getMsg());
        contentValues.put(myDbHelper.DATE, message.getDate());
        contentValues.put(myDbHelper.TYPE, "0");
        contentValues.put(myDbHelper.READ, "N");
        long id = dbb.insert(myDbHelper.SENT_TABLE_NAME, null, contentValues);
        return id;
    }

    public void setReadNewMessages(User user) {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("READ", "D");
        dbb.update(myDbHelper.RECEIVED_TABLE_NAME, cv, "user_id = ?", new String[]{user.getId()});
    }

    public void setReadSentMessages(User user) {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("READ", "D");
        dbb.update(myDbHelper.SENT_TABLE_NAME, cv, "user_id = ?", new String[]{user.getId()});
    }

    public ArrayList<Message> getMessages(String userID) {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        Cursor c = db.rawQuery("select x.* from (\n" +
                "select * from sent where user_id = ?\n" +
                "UNION\n" +
                "select * from RECEIVED where user_id = ?\n" +
                ") x\n" +
                "order by x.date", new String[]{userID, userID});
        ArrayList<Message> messages = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                String mID = c.getString(c.getColumnIndex("ID"));
                String uID = c.getString(c.getColumnIndex("USER_ID"));
                String uName = c.getString(c.getColumnIndex("USER_NAME"));
                String msg = c.getString(c.getColumnIndex("MESSAGE"));
                String date = c.getString(c.getColumnIndex("DATE"));
                String type = c.getString(c.getColumnIndex("TYPE"));
                String read = c.getString(c.getColumnIndex("READ"));
                messages.add(new Message(new User(uID, uName), msg, date, Integer.parseInt(type), read));
            } while (c.moveToNext());
        }
        c.close();
        return messages;
    }

    static class myDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "myDB";
        private static final int DATABASE_Version = 1;

        private static final String RECEIVED_TABLE_NAME = "RECEIVED";
        private static final String ID = "ID";
        private static final String USER_ID = "USER_ID";
        private static final String USER_NAME = "USER_NAME";
        private static final String MESSAGE = "MESSAGE";
        private static final String DATE = "DATE";
        private static final String TYPE = "TYPE";
        private static final String READ = "READ";
        private static final String RECEIVED_CREATE_TABLE = "CREATE TABLE " + RECEIVED_TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                USER_ID + " INTEGER NOT NULL," +
                USER_NAME + " TEXT NOT NULL," +
                MESSAGE + " TEXT NOT NULL," +
                DATE + " DATE NOT NULL," +
                TYPE + " TEXT NOT NULL," +
                READ + " TEXT NOT NULL" +
                ");";
        private static final String DROP_RECEIVED_TABLE = "DROP TABLE IF EXISTS " + RECEIVED_TABLE_NAME;

        private static final String SENT_TABLE_NAME = "SENT";
        private static final String SENT_CREATE_TABLE = "CREATE TABLE " + SENT_TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                USER_ID + " INTEGER NOT NULL," +
                USER_NAME + " TEXT NOT NULL," +
                MESSAGE + " TEXT NOT NULL," +
                DATE + " DATE NOT NULL," +
                TYPE + " TEXT NOT NULL," +
                READ + " TEXT NOT NULL" +
                ");";
        private static final String DROP_SENT_TABLE = "DROP TABLE IF EXISTS " + SENT_TABLE_NAME;


        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                Log.d(TAG, "onCreate: create tables statement has been called !!!! ");
                db.execSQL(RECEIVED_CREATE_TABLE);
                db.execSQL(SENT_CREATE_TABLE);
            } catch (Exception e) {
                //Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_RECEIVED_TABLE);
                db.execSQL(DROP_SENT_TABLE);
                onCreate(db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}