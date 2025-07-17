package com.example.groupproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;


public class WheelActivity extends AppCompatActivity {

    WheelView wheelView;
    Button btnAdd, btnSpin, btnDelete;
    EditText textInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);

        wheelView = findViewById(R.id.wheelView);
        btnAdd = findViewById(R.id.btn_add);
        btnSpin = findViewById(R.id.btn_spin);
        textInput = findViewById(R.id.inputIdea);
        btnDelete = findViewById(R.id.btn_delete);

        ArrayList<String> items = new ArrayList<>();

        //List<String> dummyItems = A
        //Arrays.asList("Leeseo", "Wonyoung", "Liz", "Gaeul", "Yujin", "Rei");
        //wheelView.setItems(dummyItems);

        btnDelete.setOnClickListener(v -> {
            items.clear();
            wheelView.setItems(new ArrayList<>());
        });

        btnAdd.setOnClickListener(v -> {
            items.add(textInput.getText().toString());
            wheelView.setItems(items);
            textInput.setText("");
        });

        btnSpin.setOnClickListener(v -> {
            int randomIndex = (int)(Math.random() * items.size());
            wheelView.spinToIndex(randomIndex);

            wheelView.postDelayed(()->{
                Toast.makeText(this, "Winner: " + items.get(randomIndex).toString(), Toast.LENGTH_LONG).show();
            },3500);

        });

    }
}