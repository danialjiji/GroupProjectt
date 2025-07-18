/*package com.example.groupproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendList extends AppCompatActivity {

    ExpandableListView expandableListView;
    List<String> names;
    Map<String, List<String>> details;
    MyAdapter myAdapter;
    DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        db = new DbHelper(this);
        expandableListView = findViewById(R.id.expandable_list);

        int userid = getIntent().getIntExtra("userid", 1); // default to 1

        getData(userid);

        myAdapter = new MyAdapter(this, names, details);
        expandableListView.setAdapter(myAdapter);

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String name = names.get(groupPosition);
            List<String> friendDetails = details.get(name);

            // Last two items are "Update" and "Delete"
            int size = friendDetails.size();
            String clickedItem = friendDetails.get(childPosition);

            if (clickedItem.equals("Update")) {
                Intent intent = new Intent(FriendList.this, UpdateFriend.class);
                intent.putExtra("fname", name);
                intent.putExtra("fnumber", friendDetails.get(0).replace("Phone: ", ""));
                intent.putExtra("femail", friendDetails.get(1).replace("Email: ", ""));
                intent.putExtra("fage", friendDetails.get(2).replace("Age: ", ""));
                intent.putExtra("fgender", friendDetails.get(3).replace("Gender: ", ""));
                intent.putExtra("fdob", friendDetails.get(4).replace("Birthday: ", ""));
                startActivity(intent);
                return true;
            } else if (clickedItem.equals("Delete")) {
                showDeleteDialog(name, groupPosition);
                return true;
            }

            return false;
        });
    }

    private void getData(int userID) {
        names = new ArrayList<>();
        details = new HashMap<>();

        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM FRIENDS WHERE userID = ?", new String[]{String.valueOf(userID)});

        if (cursor.moveToFirst()) {
            do {
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
                friendDetails.add("Birthday: " + dob);
                friendDetails.add("Update");
                friendDetails.add("Delete");

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
}
*/