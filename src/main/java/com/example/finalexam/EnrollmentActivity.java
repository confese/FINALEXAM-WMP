package com.example.finalexam;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EnrollmentActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    ListView subjectsListView;
    Button enrollButton;
    ArrayList<String> subjects;
    ArrayList<Integer> subjectIds; // To keep track of subject IDs
    int selectedSubjectId = -1; // To keep track of the selected subject

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        dbHelper = new DatabaseHelper(this);
        subjectsListView = findViewById(R.id.subjects_list_view);
        enrollButton = findViewById(R.id.enroll_button);
        subjects = new ArrayList<>();
        subjectIds = new ArrayList<>();

        loadSubjects();

        subjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSubjectId = position;
                Toast.makeText(EnrollmentActivity.this, "Selected: " + subjects.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSubjectId != -1) {
                    int subjectId = subjectIds.get(selectedSubjectId); // Get the actual subject ID
                    String subject = subjects.get(selectedSubjectId);

                    if (dbHelper.enrollInSubject(1, subjectId)) { // Assuming student ID is 1
                        Toast.makeText(EnrollmentActivity.this, "Enrolled in " + subject, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EnrollmentActivity.this, SummaryActivity.class);
                        String userName = "John Doe"; // Replace with actual user name retrieval logic
                        intent.putExtra("USER_NAME", userName);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EnrollmentActivity.this, "Already enrolled in " + subject, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EnrollmentActivity.this, "Please select a subject", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadSubjects() {
        Cursor cursor = dbHelper.getAllSubjects();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int subjectId = cursor.getInt(cursor.getColumnIndex("subject_id"));
                String subjectName = cursor.getString(cursor.getColumnIndex("subject_name"));
                subjects.add(subjectName);
                subjectIds.add(subjectId); // Store the subject ID
            } while (cursor.moveToNext());
            cursor.close();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, subjects);
        subjectsListView.setAdapter(adapter);
    }
}