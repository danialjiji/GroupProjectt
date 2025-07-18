package com.example.groupproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class SearchActivity extends AppCompatActivity {

    EditText searchName;
    TextView friendsList;
    Button btnSearch;

    DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchName = findViewById(R.id.search_name);
        friendsList = findViewById(R.id.friends_list);
        btnSearch = findViewById(R.id.btn_search);

        db = new DbHelper(this);

        btnSearch.setOnClickListener(v -> {
            // Get the search name from the EditText and trim any extra spaces
            String searchQuery = searchName.getText().toString().trim();

            // Check if the search query is empty
            if (searchQuery.isEmpty()) {
                Toast.makeText(SearchActivity.this, "Please enter a name to search.", Toast.LENGTH_SHORT).show();
                return; // Exit if search input is empty
            }

            // Get the readable database
            SQLiteDatabase database = db.getReadableDatabase();

            // Define the query with LIKE for partial matching and case insensitivity
            String query = "SELECT * FROM FRIENDS WHERE LOWER(fname) = LOWER(?)";
            Cursor cursor = database.rawQuery(query, new String[]{searchQuery});

            // Create a StringBuilder to hold the search results
            StringBuilder result = new StringBuilder();

            // If cursor has data, process it
            if (cursor.moveToFirst()) {
                do {
                    // Get the friend's details from the cursor
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("fname"));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow("fnumber"));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow("femail"));
                    String age = cursor.getString(cursor.getColumnIndexOrThrow("fage"));
                    String gender = cursor.getString(cursor.getColumnIndexOrThrow("fgender"));
                    String dob = cursor.getString(cursor.getColumnIndexOrThrow("fdob"));

                    // Append details for each friend
                    result.append("Name: ").append(name)
                            .append("\nPhone: ").append(phone)
                            .append("\nEmail: ").append(email)
                            .append("\nAge: ").append(age)
                            .append("\nGender: ").append(gender)
                            .append("\nBirthday: ").append(dob)
                            .append("\n\n");

                } while (cursor.moveToNext());
            } else {
                // If no friends found, append a message
                result.append("No friends found with that name.");
            }

            // Close the cursor and database to avoid memory leaks
            cursor.close();
            database.close();

            // Set the search result to a TextView (or you can use a ListView/RecyclerView instead)
            friendsList.setText(result.toString());
        });
    }
}