package com.example.groupproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.*;
import com.example.groupproject.NavigationHelper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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

    TextView countfriend, reminder, textGreeting;
    EditText editTextTodo;
    Button btnAddTodo;
    ListView listViewTodo;

    DbHelper dbHelper;
    int currentUserId = -1;
    String username = "";

    ArrayList<String> todoList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @SuppressLint({"RestrictedApi", "SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //declare ui
        countfriend = findViewById(R.id.countfriend);
        reminder = findViewById(R.id.reminder);
        editTextTodo = findViewById(R.id.editTextTodo);
        btnAddTodo = findViewById(R.id.btnAddTodo);
        listViewTodo = findViewById(R.id.listViewTodo);
        textGreeting = findViewById(R.id.textGreeting);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        currentUserId = intent.getIntExtra("userID", -1);

        if (username == null || currentUserId == -1) {
            Toast.makeText(this, "User not recognized", Toast.LENGTH_SHORT).show();
            finish();  // Prevent issues
            return;
        }

        if (username != null) {
            textGreeting.setText("Hi, " + username + "!");
        } else {
            textGreeting.setText("Hi there!");
        }

        //Navigation part
        dashboard = findViewById(R.id.dashboard);
        navigationView = findViewById(R.id.navigation_view);
        setSupportActionBar(findViewById(R.id.toolbar));

        //setup drawer
        drawerToggle = new ActionBarDrawerToggle(this, dashboard,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dashboard.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        navigationView.setNavigationItemSelectedListener(this);

        //user navigation part
        if (username != null && !username.isEmpty()) {
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = headerView.findViewById(R.id.nav_username);
            navUsername.setText(username);
        }

        dbHelper = new DbHelper(this);

        int totalFriends = getTotalCount();
        countfriend.setText(String.valueOf(totalFriends));
        reminder.setText(getBirthdayReminder());
        loadTodoList();

        listViewTodo.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTask = todoList.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?\n\n" + selectedTask)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteTodo(selectedTask);
                        loadTodoList();
                    })
                    .setNegativeButton("No", null)
                    .show();
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

    private void deleteTodo(String task) {
        dbHelper.getWritableDatabase().delete("TODOLIST", "todo_text = ? AND userid = ?", new String[]{task, String.valueOf(currentUserId)});
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Intent i = null;

        if (id == R.id.nav_dashboard) {
            // Already on dashboard
        } else if (id == R.id.nav_friends) {
            i = new Intent(this, FriendList.class);
        } else if (id == R.id.nav_search) {
            i = new Intent(this, SearchActivity.class);
        } else if (id == R.id.nav_addfriend) {
            i = new Intent(this, AddFriend.class);
        } else if (id == R.id.nav_chart) {
            i = new Intent(this, ChartActivity.class);
        } else if (id == R.id.nav_wheel) {
            i = new Intent(this, WheelActivity.class);
        }else if (id == R.id.nav_logout) {
            Intent logoutIntent = new Intent(this, Login.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(logoutIntent);
            finish(); // optional but good
            return true;
        }

        if (i != null) {
            i.putExtra("username", username);
            i.putExtra("userID", currentUserId); // ✅ FIXED here
            startActivity(i);
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

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT fname, fdob FROM FRIENDS WHERE userid = ?",
                new String[]{String.valueOf(currentUserId)}
        );

        ArrayList<String> birthdayMessages = new ArrayList<>();

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String dob = cursor.getString(1);

            if (dob == null || dob.isEmpty()) continue;

            // Normalize delimiters
            dob = dob.replace('/', '-');

            String[] parts = dob.split("-");

            int day, month;

            try {
                if (parts.length != 3) continue;

                if (parts[0].length() == 4) {
                    // Format: YYYY-MM-DD
                    month = Integer.parseInt(parts[1]) - 1;
                    day = Integer.parseInt(parts[2]);
                } else if (parts[2].length() == 4) {
                    // Format: DD-MM-YYYY
                    day = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]) - 1;
                } else {
                    continue; // Unknown format
                }

                Calendar birthday = Calendar.getInstance();
                birthday.set(Calendar.MONTH, month);
                birthday.set(Calendar.DAY_OF_MONTH, day);
                birthday.set(Calendar.YEAR, today.get(Calendar.YEAR));

                if (birthday.before(today)) {
                    birthday.add(Calendar.YEAR, 1);
                }

                long diffMillis = birthday.getTimeInMillis() - today.getTimeInMillis();
                long diffDays = diffMillis / (24 * 60 * 60 * 1000);

                if (diffDays == 0) {
                    birthdayMessages.add(name + "'s birthday is today!");
                } else if (diffDays <= 10) {
                    birthdayMessages.add(name + "'s birthday is in " + diffDays + " day(s)!");
                }

            } catch (Exception e) {
                Log.e("BIRTHDAY_PARSER", "Error parsing dob: " + dob, e);
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
            return sb.toString().trim();
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

    }


    // Insert a new to-do item into DB
    private void insertTodo(String task) {
        ContentValues values = new ContentValues();
        values.put("todo_date", ""); // Optional field
        values.put("todo_text", task);
        values.put("userid", currentUserId);
        dbHelper.getWritableDatabase().insert("TODOLIST", null, values);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh friend count
        int totalFriends = getTotalCount();
        countfriend.setText(String.valueOf(totalFriends));

        // Refresh birthday reminder
        reminder.setText(getBirthdayReminder());


        // Refresh to-do list
        loadTodoList();
    }
}