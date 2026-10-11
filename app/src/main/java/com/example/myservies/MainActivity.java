package com.example.myservies;



import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText editDescription;
    private EditText editLocation;
    private EditText editReference;

    private TextView txtTrackResult;
    private TextView txtRequests;

    private RequestDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new RequestDB(this);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        editDescription = findViewById(R.id.editDescription);
        editLocation = findViewById(R.id.editLocation);
        editReference = findViewById(R.id.editReference);

        txtTrackResult = findViewById(R.id.txtTrackResult);
        txtRequests = findViewById(R.id.txtRequests);

        Button btnSubmit = findViewById(R.id.btnSubmit);
        Button btnTrack = findViewById(R.id.btnTrack);
        Button btnViewRequests = findViewById(R.id.btnViewRequests);

        btnSubmit.setOnClickListener(v -> submitComplaint());
        btnTrack.setOnClickListener(v -> trackComplaint());
        btnViewRequests.setOnClickListener(v -> viewComplaints());
        Button btnProfile = findViewById(R.id.btnProfile);

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    Profile.class
            );
            startActivity(intent);
        });

        // Show previously saved complaints when the app opens.
        viewComplaints();
    }

    private void submitComplaint() {
        String category = spinnerCategory.getSelectedItem().toString();
        String description = editDescription.getText().toString().trim();
        String location = editLocation.getText().toString().trim();

        if (description.isEmpty()) {
            editDescription.setError("Please describe the problem");
            editDescription.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            editLocation.setError("Please enter the location");
            editLocation.requestFocus();
            return;
        }

        String reference = database.addRequest(
                category,
                description,
                location
        );

        if (reference == null) {
            Toast.makeText(
                    this,
                    "Could not save complaint. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        Toast.makeText(
                this,
                "Complaint submitted successfully!",
                Toast.LENGTH_LONG
        ).show();

        txtTrackResult.setText(
                "Complaint submitted successfully!\n\n" +
                        "Reference number: " + reference + "\n" +
                        "Status: Submitted\n\n" +
                        "Keep your reference number to track your complaint."
        );

        editDescription.setText("");
        editLocation.setText("");
        editReference.setText("");

        viewComplaints();
    }

    private void trackComplaint() {
        String reference = editReference.getText()
                .toString()
                .trim();

        if (reference.isEmpty()) {
            editReference.setError("Enter your reference number");
            editReference.requestFocus();
            return;
        }

        Cursor cursor = database.findByReference(reference);

        try {
            if (cursor.moveToFirst()) {
                String savedReference =
                        cursor.getString(cursor.getColumnIndexOrThrow("reference"));

                String category =
                        cursor.getString(cursor.getColumnIndexOrThrow("category"));

                String description =
                        cursor.getString(cursor.getColumnIndexOrThrow("description"));

                String location =
                        cursor.getString(cursor.getColumnIndexOrThrow("location"));

                String status =
                        cursor.getString(cursor.getColumnIndexOrThrow("status"));

                String date =
                        cursor.getString(cursor.getColumnIndexOrThrow("createdAt"));

                txtTrackResult.setText(
                        "COMPLAINT DETAILS\n\n" +
                                "Reference: " + savedReference + "\n" +
                                "Category: " + category + "\n" +
                                "Description: " + description + "\n" +
                                "Location: " + location + "\n" +
                                "Date submitted: " + date + "\n" +
                                "Current status: " + status
                );

            } else {
                txtTrackResult.setText(
                        "No complaint was found with that reference number. " +
                                "Please check the number and try again."
                );
            }
        } finally {
            cursor.close();
        }
    }

    private void viewComplaints() {
        Cursor cursor = database.getAllRequests();
        StringBuilder complaints = new StringBuilder();

        try {
            if (cursor.moveToFirst()) {
                do {
                    String reference =
                            cursor.getString(cursor.getColumnIndexOrThrow("reference"));

                    String category =
                            cursor.getString(cursor.getColumnIndexOrThrow("category"));

                    String description =
                            cursor.getString(cursor.getColumnIndexOrThrow("description"));

                    String location =
                            cursor.getString(cursor.getColumnIndexOrThrow("location"));

                    String status =
                            cursor.getString(cursor.getColumnIndexOrThrow("status"));

                    String date =
                            cursor.getString(cursor.getColumnIndexOrThrow("createdAt"));

                    complaints.append("Reference: ")
                            .append(reference)
                            .append("\nCategory: ")
                            .append(category)
                            .append("\nDescription: ")
                            .append(description)
                            .append("\nLocation: ")
                            .append(location)
                            .append("\nStatus: ")
                            .append(status)
                            .append("\nDate: ")
                            .append(date)
                            .append("\n\n-------------------------\n\n");

                } while (cursor.moveToNext());

                txtRequests.setText(complaints.toString());

            } else {
                txtRequests.setText(
                        "You have not submitted any complaints yet."
                );
            }
        } finally {
            cursor.close();
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
