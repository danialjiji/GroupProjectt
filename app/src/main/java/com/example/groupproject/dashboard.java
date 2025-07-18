package com.example.groupproject;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout dashboard;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    TextView countfriend, reminder;
    EditText editTextTodo;
    Button btnAddTodo;
    ListView listViewTodo;

    DbHelper dbHelper;
    int currentUserId = 1;

    ArrayList<String> todoList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Set the toolbar as the action bar
        setSupportActionBar(findViewById(R.id.toolbar));

        //setup drawer
        dashboard = findViewById(R.id.dashboard);
        navigationView = findViewById(R.id.navigation_view);

        drawerToggle = new ActionBarDrawerToggle(this, dashboard,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        dashboard.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        navigationView.setNavigationItemSelectedListener(this);

        //declare ui
        countfriend = findViewById(R.id.countfriend);
        reminder = findViewById(R.id.reminder);
        editTextTodo = findViewById(R.id.editTextTodo);
        btnAddTodo = findViewById(R.id.btnAddTodo);
        listViewTodo = findViewById(R.id.listViewTodo);

        dbHelper = new DbHelper(this);

        int totalFriends = getTotalCount();
        countfriend.setText("Number of Friends: " + totalFriends);
       /* int birthdayThisWeek = getBirthdayCountForThisWeek();
        reminder.setText(birthdayThisWeek > 0
                ? "Reminder: " + birthdayThisWeek + " friend(s) have birthdays this week!"
                : "Reminder: No upcoming birthdays");*/
        reminder.setText(getBirthdayReminder());


        loadTodoList();

        // STEP 1: Force insert a test friend (only once)
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor testCursor = db.rawQuery("SELECT COUNT(*) FROM FRIENDS WHERE userid = 1", null);
        int existingCount = 0;
        if (testCursor.moveToFirst()) {
            existingCount = testCursor.getInt(0);
        }
        testCursor.close();

        if (existingCount == 0) {
            ContentValues values = new ContentValues();
            values.put("fname", "sha");
            values.put("fnumber", "0123456789");
            values.put("femail", "test@example.com");
            values.put("fage", 25);
            values.put("fdob", "2000-07-18");
            values.put("fgender", "Female");
            values.put("userid", 1);
            long insertResult = db.insert("FRIENDS", null, values);
            Log.d("DB_INSERT", "Inserted test friend. Result: " + insertResult);
        } else {
            Log.d("DB_INSERT", "Test friend already exists, not inserting.");
        }

// STEP 2: Print out all friends for userID = 1
        Cursor cursor = db.rawQuery("SELECT * FROM FRIENDS WHERE userid = 1", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("fname"));
            String dob = cursor.getString(cursor.getColumnIndexOrThrow("fdob"));
            Log.d("DB_CHECK", "Friend: " + name + ", DOB: " + dob);
        }
        cursor.close();


        listViewTodo.setOnItemLongClickListener((parent, view, position, id) -> {
            String taskToDelete = todoList.get(position);

            // Delete from database
            dbHelper.getWritableDatabase().delete(
                    "TODOLIST",
                    "todo_text = ? AND userid = ?",
                    new String[]{taskToDelete, String.valueOf(currentUserId)}
            );

            // Remove from list and refresh UI
            todoList.remove(position);
            arrayAdapter.notifyDataSetChanged();

            Toast.makeText(dashboard.this, "Task deleted", Toast.LENGTH_SHORT).show();
            return true;
        });


        btnAddTodo.setOnClickListener(v -> {
            String task = editTextTodo.getText().toString().trim();
            if (!task.isEmpty()) {
                insertTodo(task);
                editTextTodo.setText("");
                loadTodoList();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

       if (id == R.id.nav_dashboard) {
            // Already here
        } else if (id == R.id.nav_friends) {
            startActivity(new Intent(this, ChartActivity.class));
        }

        dashboard.closeDrawers();
        return true;
    }

    // start friends info
    private int getTotalCount() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM FRIENDS WHERE userid = ?",
                new String[]{String.valueOf(currentUserId)}
        );

        int count = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
                Log.d("DEBUG_FRIENDS", "Friend count for userID 1: " + count);
            } else {
                Log.d("DEBUG_FRIENDS", "Cursor is empty");
            }
            cursor.close();
        } else {
            Log.d("DEBUG_FRIENDS", "Cursor is null");
        }

        return count;
    }

    private String getBirthdayReminder() {
        Calendar today = Calendar.getInstance();
        int thisMonth = today.get(Calendar.MONTH) + 1;
        int todayDay = today.get(Calendar.DAY_OF_MONTH);

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT fname, fdob FROM FRIENDS WHERE userid = ?",
                new String[]{String.valueOf(currentUserId)}
        );

        ArrayList<String> birthdayMessages = new ArrayList<>();

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String dob = cursor.getString(1);
            if (dob != null && dob.length() >= 10) {
                int dobMonth = Integer.parseInt(dob.substring(5, 7));
                int dobDay = Integer.parseInt(dob.substring(8, 10));

                if (dobMonth == thisMonth) {
                    if (dobDay == todayDay) {
                        birthdayMessages.add(name + "'s birthday today!");
                    } else if (dobDay > todayDay && dobDay - todayDay <= 7) {
                        birthdayMessages.add(name + "'s birthday on " + dobDay + "!");
                    }
                }
            }
        }
        cursor.close();

        if (birthdayMessages.isEmpty()) {
            return "Reminder: No upcoming birthdays";
        } else {
            StringBuilder sb = new StringBuilder("Reminder:\n");
            for (String msg : birthdayMessages) {
                sb.append(msg).append("\n");
            }
            return sb.toString().trim(); // remove last newline
        }
    }


    private void loadTodoList() {
        todoList.clear();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT todo_text FROM TODOLIST WHERE userid = ?",
                new String[]{String.valueOf(currentUserId)}
        );

        while (cursor.moveToNext()) {
            String text = cursor.getString(0);
            todoList.add(text);
        }
        cursor.close();

        if (arrayAdapter == null) {
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todoList);
            listViewTodo.setAdapter(arrayAdapter);
        } else {
            arrayAdapter.notifyDataSetChanged(); // 💡 This line refreshes the list
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todoList);
        listViewTodo.setAdapter(arrayAdapter);

        listViewTodo.setOnItemLongClickListener((adapterView, view, position, id) -> {
            String item = todoList.get(position);

            // Delete from DB
            dbHelper.getWritableDatabase().delete(
                    "TODOLIST",
                    "todo_text = ? AND userid = ?",
                    new String[]{item, String.valueOf(currentUserId)}
            );

            // Refresh list
            loadTodoList();
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    // Insert a new to-do item into DB
    private void insertTodo(String task) {
        ContentValues values = new ContentValues();
        values.put("todo_date", ""); // Optional field
        values.put("todo_text", task);
        values.put("userid", currentUserId);
        dbHelper.getWritableDatabase().insert("TODOLIST", null, values);
    }
}