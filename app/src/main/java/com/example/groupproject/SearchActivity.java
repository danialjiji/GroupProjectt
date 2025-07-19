package com.example.groupproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.Friend;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText searchName;
    Button btnSearch;
    RecyclerView recyclerFriends;

    DbHelper db;
    List<Friend> friendList;
    FriendAdapter adapter;

    int currentUserId = -1;
    String username = "";

    DrawerLayout searchfriend;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchName = findViewById(R.id.search_name);
        btnSearch = findViewById(R.id.btn_search);
        recyclerFriends = findViewById(R.id.recycler_friends);

        // Get data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        currentUserId = intent.getIntExtra("userID", -1); // ✔️ This is correct

        // use userId, not userid
        if ( currentUserId == -1) {
            Toast.makeText(this, "Error: User not recognized", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer setup
        searchfriend = findViewById(R.id.friend_search);
        navigationView = findViewById(R.id.navigation_view);
        drawerToggle = new ActionBarDrawerToggle(this, searchfriend,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        searchfriend.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);

        // Update nav header with username
        if (username != null && !username.isEmpty()) {
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = headerView.findViewById(R.id.nav_username);
            navUsername.setText(username);
        }

        db = new DbHelper(this);
        friendList = new ArrayList<>();
        adapter = new FriendAdapter(friendList);

        recyclerFriends.setLayoutManager(new LinearLayoutManager(this));
        recyclerFriends.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {
            String searchQuery = searchName.getText().toString().trim();

            if (searchQuery.isEmpty()) {
                Toast.makeText(this, "Please enter a name to search.", Toast.LENGTH_SHORT).show();
                return;
            }

            friendList.clear();
            SQLiteDatabase database = db.getReadableDatabase();

            String query = "SELECT * FROM FRIENDS WHERE LOWER(fname) LIKE LOWER(?)";
            Cursor cursor = database.rawQuery(query, new String[]{"%" + searchQuery + "%"});

            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("fname"));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow("fnumber"));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow("femail"));
                    String age = cursor.getString(cursor.getColumnIndexOrThrow("fage"));
                    String gender = cursor.getString(cursor.getColumnIndexOrThrow("fgender"));
                    String dob = cursor.getString(cursor.getColumnIndexOrThrow("fdob"));

                    friendList.add(new Friend(name, phone, email, age, gender, dob));
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "No friends found.", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
            database.close();

            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i = null;
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            i = new Intent(this, dashboard.class);
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
        }

        if (i != null) {
            i.putExtra("userID", currentUserId);
            i.putExtra("username", username);
            startActivity(i);
        }

        searchfriend.closeDrawers();
        return true;
    }
}