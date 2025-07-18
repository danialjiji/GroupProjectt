package com.example.groupproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.Friend;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    EditText searchName;
    Button btnSearch;
    RecyclerView recyclerFriends;

    DbHelper db;
    List<Friend> friendList;
    FriendAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchName = findViewById(R.id.search_name);
        btnSearch = findViewById(R.id.btn_search);
        recyclerFriends = findViewById(R.id.recycler_friends);

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
}