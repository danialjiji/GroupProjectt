package com.example.groupproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ExpandableListView expandableListView;
    List<String> names;
    Map<String, List<String>> details;
    MyAdapter myAdapter;
    DbHelper db;
    int userid;
    int currentUserId = -1;
    String username = "";

    DrawerLayout friendlist;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        // Get data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        currentUserId = intent.getIntExtra("userID", -1);
        userid = currentUserId;


        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer setup
        friendlist = findViewById(R.id.friends_list);
        navigationView = findViewById(R.id.navigation_view);
        drawerToggle = new ActionBarDrawerToggle(this, friendlist,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        friendlist.addDrawerListener(drawerToggle);
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
        expandableListView = findViewById(R.id.expandable_list);

      //  userid = getIntent().getIntExtra("userid", 1); // default to 1

        getData(userid);

        myAdapter = new MyAdapter(this, names, details);
        expandableListView.setAdapter(myAdapter);

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String name = names.get(groupPosition);
            List<String> friendDetails = details.get(name);
            String clickedItem = friendDetails.get(childPosition);

            if (clickedItem.contains("Birthday")) {
                handleBirthdayClick(groupPosition);
                return true;

            } else if (clickedItem.equals("Update")) {
                handleUpdateClick(groupPosition);
                return true;

            } else if (clickedItem.equals("Delete")) {
                handleDeleteClick(groupPosition);
                return true;
            }

            return false;
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData(userid);
        myAdapter = new MyAdapter(this, names, details);
        expandableListView.setAdapter(myAdapter);
    }

    private void getData(int userID) {
        names = new ArrayList<>();
        details = new HashMap<>();

        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM FRIENDS WHERE userID = ?", new String[]{String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            do {
                int friendID = cursor.getInt(cursor.getColumnIndexOrThrow("friendsID"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("fname"));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("fnumber"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("femail"));
                String age = cursor.getString(cursor.getColumnIndexOrThrow("fage"));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow("fgender"));
                String dob = cursor.getString(cursor.getColumnIndexOrThrow("fdob"));

                names.add(name);

                List<String> friendDetails = new ArrayList<>();
                friendDetails.add("Phone: " + phone);
                friendDetails.add("Email: " + email);
                friendDetails.add("Age: " + age);
                friendDetails.add("Gender: " + gender);
                friendDetails.add("Birthday: " + dob); // keep for display only, not for button
                friendDetails.add("Actions"); // all 3 buttons shown here
                friendDetails.add(String.valueOf(friendID));


                details.put(name, friendDetails);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
    }


    private void showDeleteDialog(String name, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Friend")
                .setMessage("Are you sure you want to delete " + name + "?")
                .setPositiveButton("Yes", (dialog, which) -> deleteFriend(name, position))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteFriend(String name, int position) {
        SQLiteDatabase database = db.getWritableDatabase();
        int deletedRows = database.delete("FRIENDS", "fname=?", new String[]{name});
        database.close();

        if (deletedRows > 0) {
            names.remove(position);
            details.remove(name);
            myAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Friend deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleUpdateClick(int groupPosition) {
        String name = names.get(groupPosition);
        List<String> friendDetails = details.get(name);

        int friendID = Integer.parseInt(friendDetails.get(6)); // ID is at index 7

        Intent intent = new Intent(FriendList.this, UpdateFriend.class);
        intent.putExtra("friendsID", friendID);
        intent.putExtra("fname", name);
        intent.putExtra("fnumber", friendDetails.get(0).replace("Phone: ", ""));
        intent.putExtra("femail", friendDetails.get(1).replace("Email: ", ""));
        intent.putExtra("fage", friendDetails.get(2).replace("Age: ", ""));
        intent.putExtra("fgender", friendDetails.get(3).replace("Gender: ", ""));
        intent.putExtra("fdob", friendDetails.get(4).replace("Birthday: ", ""));
        intent.putExtra("userid", userid);
        startActivity(intent);
    }

    public void handleDeleteClick(int groupPosition) {
        String name = names.get(groupPosition);
        showDeleteDialog(name, groupPosition);
    }

    public void handleBirthdayClick(int groupPosition) {
        String name = names.get(groupPosition);
        List<String> friendDetails = details.get(name);
        String phone = friendDetails.get(0).replace("Phone: ", "").replaceAll("[^\\d]", ""); // digits only
        String message = "Happy Birthday, " + name + "! 🎉 Wishing you a wonderful year ahead!";

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://wa.me/6" + phone + "?text=" + Uri.encode(message)));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp not found on this device.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Intent i = null;

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
            i.putExtra("username", username);
            i.putExtra("userID", currentUserId);
            startActivity(i);
        }

        friendlist.closeDrawers();
        return true;
    }
}
