package com.example.groupproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {


    //declares db as SQLiteDatabase
    SQLiteDatabase db;

    public static final String DATABASE_NAME = "friendsapp.db";
    public static final int DATABASE_VERSION = 1;

    // USER table
    public static final String TABLE_USER = "USER";
    public static final String COL_USER_ID = "userID";
    public static final String COL_USERNAME = "username";
    public static final String COL_NAME = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_GENDER = "gender";

    // FRIENDS table
    public static final String TABLE_FRIENDS = "FRIENDS";
    public static final String COL_FRIEND_ID = "friendsID";
    public static final String COL_FNAME = "fname";
    public static final String COL_FNUMBER = "fnumber";
    public static final String COL_FEMAIL = "femail";
    public static final String COL_FAGE = "fage";
    public static final String COL_FDOB = "fdob";
    public static final String COL_FGENDER = "fgender";

    // MESSAGE table
    public static final String TABLE_MESSAGE = "MESSAGE";
    public static final String COL_MESSAGE_ID = "messageID";
    public static final String COL_MESSAGE_TEXT = "messagetext";
    public static final String COL_MESSAGE_DATE = "messagedate";

    // TODOLIST table
    public static final String TABLE_TODO = "TODOLIST";
    public static final String COL_TODO_ID = "todoID";
    public static final String COL_TODO_DATE = "todo_date";
    public static final String COL_TODO_TEXT = "todo_text";
    public static final String COL_TODO_STATUS = "todo_status";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_NAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_GENDER + " TEXT)";

        String createFriendsTable = "CREATE TABLE " + TABLE_FRIENDS + " (" +
                COL_FRIEND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FNAME + " TEXT, " +
                COL_FNUMBER + " TEXT, " +
                COL_FEMAIL + " TEXT, " +
                COL_FAGE + " INTEGER, " +
                COL_FDOB + " TEXT, " +
                COL_FGENDER + " TEXT, " +
                COL_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COL_USER_ID + "))";

        String createMessageTable = "CREATE TABLE " + TABLE_MESSAGE + " (" +
                COL_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MESSAGE_TEXT + " TEXT, " +
                COL_MESSAGE_DATE + " TEXT, " +
                COL_USER_ID + " INTEGER, " +
                COL_FRIEND_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_FRIEND_ID + ") REFERENCES " + TABLE_FRIENDS + "(" + COL_FRIEND_ID + "))";

        String createTodoTable = "CREATE TABLE " + TABLE_TODO + " (" +
                COL_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TODO_DATE + " TEXT, " +
                COL_TODO_TEXT + " TEXT, " +
                COL_TODO_STATUS + " TEXT, " +
                COL_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COL_USER_ID + "))";

        db.execSQL(createUserTable);
        db.execSQL(createFriendsTable);
        db.execSQL(createMessageTable);
        db.execSQL(createTodoTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // INSERT USER
    public void insertUser(String username, String name, String email, String password, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_GENDER, gender);
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    // INSERT FRIEND
    public void insertFriend(String fname, String fnumber, String femail, int fage, String fdob, String fgender, int userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FNAME, fname);
        values.put(COL_FNUMBER, fnumber);
        values.put(COL_FEMAIL, femail);
        values.put(COL_FAGE, fage);
        values.put(COL_FDOB, fdob);
        values.put(COL_FGENDER, fgender);
        values.put(COL_USER_ID, userID);
        db.insert(TABLE_FRIENDS, null, values);
        db.close();
    }

    // INSERT MESSAGE
    public void insertMessage(String messagetext, String messagedate, int userID, int friendsID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MESSAGE_TEXT, messagetext);
        values.put(COL_MESSAGE_DATE, messagedate);
        values.put(COL_USER_ID, userID);
        values.put(COL_FRIEND_ID, friendsID);
        db.insert(TABLE_MESSAGE, null, values);
        db.close();
    }

    // INSERT TODO
    public void insertTodo(String todoDate, String todoText, String todoStatus, int userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TODO_DATE, todoDate);
        values.put(COL_TODO_TEXT, todoText);
        values.put(COL_TODO_STATUS, todoStatus);
        values.put(COL_USER_ID, userID);
        db.insert(TABLE_TODO, null, values);
        db.close();
    }

    public String getUsername(long userID) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query("USER", new String[]{"username"}, "userID=?", new String[]{String.valueOf(userID)}, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    public String getName(long userID) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query("USER", new String[]{"name"}, "userID=?", new String[]{String.valueOf(userID)}, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    public String getEmail(long userID) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query("USER", new String[]{"email"}, "userID=?", new String[]{String.valueOf(userID)}, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    public String getPassword(long userID) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query("USER", new String[]{"password"}, "userID=?", new String[]{String.valueOf(userID)}, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    public String getGender(long userID) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query("USER", new String[]{"gender"}, "userID=?", new String[]{String.valueOf(userID)}, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    public String getFname(long friendsID) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query("FRIENDS", new String[]{"fname"}, "friendsID=?", new String[]{String.valueOf(friendsID)}, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    public String getFnumber(long friendsID) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query("FRIENDS", new String[]{"fnumber"}, "friendsID=?", new String[]{String.valueOf(friendsID)}, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    public String getFemail(long friendsID) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query("FRIENDS", new String[]{"femail"}, "friendsID=?", new String[]{String.valueOf(friendsID)}, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    public String getFage(long friendsID) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query("FRIENDS", new String[]{"fage"}, "friendsID=?", new String[]{String.valueOf(friendsID)}, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    public String getFdob(long friendsID) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query("FRIENDS", new String[]{"fdob"}, "friendsID=?", new String[]{String.valueOf(friendsID)}, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    public String getFgender(long friendsID) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query("FRIENDS", new String[]{"fgender"}, "friendsID=?", new String[]{String.valueOf(friendsID)}, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        db.close();
        return result;
    }

    //code untuk number of laki and girlssss
    public Cursor getGenderCountData(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT fgender, COUNT(*) FROM " + TABLE_FRIENDS +
                " WHERE " + COL_USER_ID + " = ?" +
                " GROUP BY fgender";
        return db.rawQuery(query, new String[]{String.valueOf(userID)});
    }


    //code untuk birthday ikut bulan
    public Cursor getBirthdayCountPerMonth(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT strftime('%m', " + COL_FDOB + ") AS month, COUNT(*) " +
                "FROM " + TABLE_FRIENDS +
                " WHERE " + COL_USER_ID + " = ?" +
                " GROUP BY month ORDER BY month";
        return db.rawQuery(query, new String[]{String.valueOf(userID)});
    }

    public int updateFriend(int friendID, String fname, String fnumber, String femail, int fage, String fdob, String fgender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FNAME, fname);
        values.put(COL_FNUMBER, fnumber);
        values.put(COL_FEMAIL, femail);
        values.put(COL_FAGE, fage);
        values.put(COL_FDOB, fdob);
        values.put(COL_FGENDER, fgender);

        return db.update(TABLE_FRIENDS, values, COL_FRIEND_ID + "=?", new String[]{String.valueOf(friendID)});
    }



}
