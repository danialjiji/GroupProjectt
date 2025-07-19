package com.example.groupproject;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class WheelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    WheelView wheelView;
    Button btnAdd, btnSpin, btnDelete;
    EditText textInput;

    TextView winnertext;

    int currentUserId = -1;
    String username = "";

    DrawerLayout wheel;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        wheelView = findViewById(R.id.wheelView);
        btnAdd = findViewById(R.id.btn_add);
        btnSpin = findViewById(R.id.btn_spin);
        textInput = findViewById(R.id.inputIdea);
        btnDelete = findViewById(R.id.btn_delete);
        winnertext = findViewById(R.id.winnertext);

        ArrayList<String> items = new ArrayList<>();

        //List<String> dummyItems = A
        //Arrays.asList("Leeseo", "Wonyoung", "Liz", "Gaeul", "Yujin", "Rei");
        //wheelView.setItems(dummyItems);

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
        wheel = findViewById(R.id.wheel_act);
        navigationView = findViewById(R.id.navigation_view);

        drawerToggle = new ActionBarDrawerToggle(this, wheel,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        wheel.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        navigationView.setNavigationItemSelectedListener(this);

        // Update nav header with username
        if (username != null && !username.isEmpty()) {
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = headerView.findViewById(R.id.nav_username);
            navUsername.setText(username);
        }


        btnDelete.setOnClickListener(v -> {
            items.clear();
            wheelView.setItems(new ArrayList<>());
            Toast.makeText(WheelActivity.this, "All items are deleted", Toast.LENGTH_SHORT).show();
        });

        btnAdd.setOnClickListener(v -> {
            items.add(textInput.getText().toString());
            wheelView.setItems(items);
            textInput.setText("");
        });

        btnSpin.setOnClickListener(v -> {

            if(textInput.getText().toString().isEmpty() && items.isEmpty()){
                Toast.makeText(WheelActivity.this, "Please enter any input", Toast.LENGTH_SHORT).show();
            }else{
                int randomIndex = (int)(Math.random() * items.size());
                wheelView.spinToIndex(randomIndex);

                wheelView.postDelayed(()->{
                    winnertext.setText("Winner: " + items.get(randomIndex).toString());
                    Toast.makeText(this, "Winner: " + items.get(randomIndex).toString(), Toast.LENGTH_LONG).show();
                },3500);
            }
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
            i.putExtra("userID", currentUserId);     // ✅ consistent key name
            i.putExtra("username", username); // ✅ also add username
            startActivity(i);
        }

        wheel.closeDrawers();
        return true;
    }

}