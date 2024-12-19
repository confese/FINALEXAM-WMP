package com.example.finalexam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "enrollment.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_SUBJECTS = "subjects";
    private static final String TABLE_ENROLLMENTS = "enrollments";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createStudentsTable = "CREATE TABLE " + TABLE_STUDENTS + " ("
                + "student_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL)";
        db.execSQL(createStudentsTable);

        String createSubjectsTable = "CREATE TABLE " + TABLE_SUBJECTS + " ("
                + "subject_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "subject_name TEXT NOT NULL)";
        db.execSQL(createSubjectsTable);

        String createEnrollmentsTable = "CREATE TABLE " + TABLE_ENROLLMENTS + " ("
                + "student_id INTEGER, "
                + "subject_id INTEGER, "
                + "PRIMARY KEY (student_id, subject_id))";
        db.execSQL(createEnrollmentsTable);

        insertSampleSubjects(db);
    }

    private void insertSampleSubjects(SQLiteDatabase db) {
        String[] subjects = {"Math", "Science", "History", "English", "Art"};
        for (String subject : subjects) {
            ContentValues values = new ContentValues();
            values.put("subject_name", subject);
            db.insert(TABLE_SUBJECTS, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENROLLMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    public Cursor getAllSubjects() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_SUBJECTS, null, null, null, null, null, null);
    }

    public boolean enrollInSubject(int studentId, int subjectId) {
        if (isAlreadyEnrolled(studentId, subjectId)) {
            return false; // Already enrolled
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("subject_id", subjectId);
        long result = db.insert(TABLE_ENROLLMENTS, null, values);
        return result != -1; // Returns true if the insert was successful
    }

    private boolean isAlreadyEnrolled(int studentId, int subjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ENROLLMENTS, null, "student_id = ? AND subject_id = ?",
                new String[]{String.valueOf(studentId), String.valueOf(subjectId)},
                null, null, null);
        boolean isEnrolled = cursor.getCount() > 0;
        cursor.close();
        return isEnrolled;
    }

    public boolean registerStudent(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        long result = db.insert(TABLE_STUDENTS, null, values);
        return result != -1; // Returns true if the insert was successful
    }

    public boolean loginStudent(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS, null, "username = ? AND password = ?",
                new String[]{username, password}, null, null, null);

        boolean isValidLogin = cursor.getCount() > 0; // If count > 0, login is valid
        cursor.close();
        return isValidLogin;
    }

    public Cursor getEnrolledSubjects(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s.subject_name FROM " + TABLE_SUBJECTS + " s " +
                "INNER JOIN " + TABLE_ENROLLMENTS + " e ON s.subject_id = e.subject_id " +
                "WHERE e.student_id = ?";
        return db.rawQuery(query, new String[]{String.valueOf(studentId)});
    }
}