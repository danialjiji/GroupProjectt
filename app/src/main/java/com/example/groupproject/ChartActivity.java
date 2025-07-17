package com.example.groupproject;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ChartActivity extends AppCompatActivity {

    BarChart barChart;
    PieChart pieChart;
    Button downloadPdfBtn;
    DbHelper db;
    int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

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
            int month = Integer.parseInt(cursor.getString(0)) - 1;
            int count = cursor.getInt(1);
            monthCount[month] = count;
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
            Bitmap barBitmap = getChartBitmap(barChart);
            Bitmap pieBitmap = getChartBitmap(pieChart);

            int pageWidth = Math.max(barBitmap.getWidth(), pieBitmap.getWidth());
            int pageHeight = barBitmap.getHeight() + pieBitmap.getHeight() + 100;

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(barBitmap, 0f, 0f, null);
            canvas.drawBitmap(pieBitmap, 0f, barBitmap.getHeight() + 50f, null);

            document.finishPage(page);

            File downloadsDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

            File chartsDir = new File(downloadsDir, "Charts");
            if (!chartsDir.exists()) chartsDir.mkdirs();

            File file = new File(chartsDir, "charts_output.pdf");
            FileOutputStream out = new FileOutputStream(file);
            document.writeTo(out);
            document.close();
            out.close();

            android.media.MediaScannerConnection.scanFile(
                    this,
                    new String[]{file.getAbsolutePath()},
                    new String[]{"application/pdf"},
                    (path, uri) -> {
                        Toast.makeText(this, "PDF saved: " + path, Toast.LENGTH_LONG).show();
                    }
            );

        } catch (Exception e) {
            Toast.makeText(this, "Failed to create PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private Bitmap getChartBitmap(View chart) {
        Bitmap returnedBitmap = Bitmap.createBitmap(chart.getWidth(), chart.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        chart.draw(canvas);
        return returnedBitmap;
    }
}
