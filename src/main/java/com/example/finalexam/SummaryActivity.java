package com.example.finalexam;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    ListView enrolledSubjectsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        dbHelper = new DatabaseHelper(this);
        enrolledSubjectsListView = findViewById(R.id.enrolled_subjects_list_view);
        loadEnrolledSubjects();
    }

    private void loadEnrolledSubjects() {
        ArrayList<String> enrolledSubjects = new ArrayList<>();

        Cursor cursor = dbHelper.getEnrolledSubjects(1);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                enrolledSubjects.add(cursor.getString(cursor.getColumnIndex("subject_name")));
            } while (cursor.moveToNext());
            cursor.close();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, enrolledSubjects);
        enrolledSubjectsListView.setAdapter(adapter);
    }
}