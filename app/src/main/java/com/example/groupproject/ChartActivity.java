package com.example.groupproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ChartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BarChart barChart;
    PieChart pieChart;
    Button downloadPdfBtn;
    DbHelper db;
    int currentUserId = -1;
    String username = "";

    DrawerLayout chartrep;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    //header line 63 sampai 100
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        // Get intent data
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        currentUserId = intent.getIntExtra("userID", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User not recognized", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation Drawer
        chartrep = findViewById(R.id.chart_report);
        navigationView = findViewById(R.id.navigation_view);
        drawerToggle = new ActionBarDrawerToggle(this, chartrep, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        chartrep.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        navigationView.setNavigationItemSelectedListener(this);

        // SAFELY update nav header
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView navUsername = headerView.findViewById(R.id.nav_username);
            if (navUsername != null && username != null && !username.isEmpty()) {
                navUsername.setText(username);
            }
        } else {
            Toast.makeText(this, "Navigation header not loaded", Toast.LENGTH_SHORT).show();
        }



        db = new DbHelper(this);
        barChart = findViewById(R.id.bar_chart);
        pieChart = findViewById(R.id.pie_chart);
        downloadPdfBtn = findViewById(R.id.downloadPdfBtn);

        int[] customColors = new int[]{
                Color.parseColor("#6D9773"),
                Color.parseColor("#0C3B2E"),
                Color.parseColor("#B46617"),
                Color.parseColor("#FFBA00")
        };

        setupBarChart(customColors);
        setupPieChart(customColors);

        downloadPdfBtn.setOnClickListener(v -> generatePdf());
    }

    private void setupBarChart(int[] colors) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> monthLabels = new ArrayList<>(
                Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));

        int[] monthCount = new int[12];
        Cursor cursor = db.getBirthdayCountPerMonth(currentUserId);
        while (cursor.moveToNext()) {
            String monthStr = cursor.getString(0);
            int count = cursor.getInt(1);

            if (monthStr != null && !monthStr.isEmpty()) {
                try {
                    int month = Integer.parseInt(monthStr) - 1;
                    if (month >= 0 && month < 12) {
                        monthCount[month] = count;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    // Optionally log this or show a toast
                }
            }
        }
        cursor.close();

        for (int i = 0; i < 12; i++) {
            barEntries.add(new BarEntry(i, monthCount[i]));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Birthdays per Month");
        barDataSet.setColors(colors);
        barChart.setData(new BarData(barDataSet));
        barChart.animateY(2000);
        barChart.getDescription().setText("Birthday Month Distribution");
        barChart.getDescription().setTextColor(Color.BLUE);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthLabels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
    }

    private void setupPieChart(int[] colors) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        Cursor cursor = db.getGenderCountData(currentUserId);

        while (cursor.moveToNext()) {
            String gender = cursor.getString(0);
            int count = cursor.getInt(1);
            pieEntries.add(new PieEntry(count, gender));
        }
        cursor.close();

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Gender Distribution");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(18f);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.animateY(2000);
        pieChart.getDescription().setText("Male vs Female Friends");
        pieChart.getDescription().setTextColor(Color.RED);
    }

    private void generatePdf() {
        try {
            // Prepare summary text
            StringBuilder summaryText = new StringBuilder();
            summaryText.append("Birthday Count per Month:\n");

            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            int[] monthCount = new int[12];
            Cursor monthCursor = db.getBirthdayCountPerMonth(currentUserId);
            while (monthCursor.moveToNext()) {
                int monthIndex = Integer.parseInt(monthCursor.getString(0)) - 1;
                int count = monthCursor.getInt(1);
                monthCount[monthIndex] = count;
            }
            monthCursor.close();

            for (int i = 0; i < 12; i++) {
                summaryText.append(months[i]).append(": ").append(monthCount[i]).append(" friends\n");
            }

            summaryText.append("\nGender Distribution:\n");
            Cursor genderCursor = db.getGenderCountData(currentUserId);
            while (genderCursor.moveToNext()) {
                String gender = genderCursor.getString(0);
                int count = genderCursor.getInt(1);
                summaryText.append(gender).append(": ").append(count).append("\n");
            }
            genderCursor.close();

            // Set PDF page size
            int pageWidth = 595;  // A4 size width in points
            int pageHeight = 842; // A4 size height in points

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            // Draw text
            Paint textPaint = new Paint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(28f);

            int x = 40;
            int y = 60;
            for (String line : summaryText.toString().split("\n")) {
                canvas.drawText(line, x, y, textPaint);
                y += 40;
            }

            document.finishPage(page);

            // Save file
            //File downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File chartsDir = new File(downloadsDir, "Charts");
            if (!chartsDir.exists()) chartsDir.mkdirs();

            File file = new File(chartsDir, "summary_only.pdf");
            FileOutputStream out = new FileOutputStream(file);
            document.writeTo(out);
            document.close();
            out.close();

            MediaScannerConnection.scanFile(
                    this,
                    new String[]{file.getAbsolutePath()},
                    new String[]{"application/pdf"},
                    (path, uri) -> Toast.makeText(this, "PDF saved: " + path, Toast.LENGTH_LONG).show()
            );

        } catch (Exception e) {
            Toast.makeText(this, "Failed to create PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    //test
    private Bitmap getChartBitmap(View chart) {
        chart.measure(
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY)
        );
        chart.layout(0, 0, chart.getMeasuredWidth(), chart.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(chart.getWidth(), chart.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        chart.draw(canvas);
        return bitmap;
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

        chartrep.closeDrawers();
        return true;
    }
}
