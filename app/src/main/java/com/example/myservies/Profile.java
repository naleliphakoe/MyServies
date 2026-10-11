package com.example.myservies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    private EditText editName, editEmail;
    private TextView txtComplaintCount, txtProfileComplaints;
    private RequestDB database;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = new RequestDB(this);

        preferences = getSharedPreferences(
                "MyServicesProfile",
                MODE_PRIVATE
        );

        editName = findViewById(R.id.editProfileName);
        editEmail = findViewById(R.id.editProfileEmail);
        txtComplaintCount = findViewById(R.id.txtComplaintCount);
        txtProfileComplaints = findViewById(R.id.txtProfileComplaints);

        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);
        Button btnBack = findViewById(R.id.btnProfileBack);

        // Load saved resident details.
        editName.setText(preferences.getString("name", ""));
        editEmail.setText(preferences.getString("email", ""));

        btnSaveProfile.setOnClickListener(v -> saveProfile());

        btnBack.setOnClickListener(v -> finish());

        displayComplaints();
    }

    private void saveProfile() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Enter your full name");
            editName.requestFocus();
            return;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches()) {
            editEmail.setError("Enter a valid email address");
            editEmail.requestFocus();
            return;
        }

        preferences.edit()
                .putString("name", name)
                .putString("email", email)
                .apply();

        Toast.makeText(
                this,
                "Profile saved successfully",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void displayComplaints() {
        Cursor cursor = database.getAllRequests();
        StringBuilder result = new StringBuilder();
        int count = 0;

        try {
            if (cursor.moveToFirst()) {
                do {
                    count++;

                    String reference = cursor.getString(
                            cursor.getColumnIndexOrThrow("reference"));

                    String category = cursor.getString(
                            cursor.getColumnIndexOrThrow("category"));

                    String description = cursor.getString(
                            cursor.getColumnIndexOrThrow("description"));

                    String location = cursor.getString(
                            cursor.getColumnIndexOrThrow("location"));

                    String status = cursor.getString(
                            cursor.getColumnIndexOrThrow("status"));

                    String date = cursor.getString(
                            cursor.getColumnIndexOrThrow("createdAt"));

                    result.append("Complaint ").append(count)
                            .append("\nReference: ").append(reference)
                            .append("\nCategory: ").append(category)
                            .append("\nDescription: ").append(description)
                            .append("\nLocation: ").append(location)
                            .append("\nDate reported: ").append(date)
                            .append("\nStatus: ").append(status)
                            .append("\n\n-------------------------\n\n");

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        txtComplaintCount.setText(
                "Total complaints reported: " + count
        );

        if (count == 0) {
            txtProfileComplaints.setText(
                    "You have not reported any complaints yet."
            );
        } else {
            txtProfileComplaints.setText(result.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (database != null && txtProfileComplaints != null) {
            displayComplaints();
        }
    }

    @Override
    protected void onDestroy() {
        if (database != null) {
            database.close();
        }
        super.onDestroy();
    }
}
