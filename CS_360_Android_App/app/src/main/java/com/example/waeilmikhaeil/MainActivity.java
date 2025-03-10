package com.example.waeilmikhaeil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private EditText usernameEditText, passwordEditText;
    private Button loginButton, createAccountButton;
    private ImageButton settingsButton;
    private TextView forgotPasswordTextView;
    private EventsDatabase eventsDatabase;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean permissionChecked = sharedPreferences.getBoolean("PERMISSION_CHECKED", false);

        if (!permissionChecked) {
            Intent permissionIntent = new Intent(this, Permissions.class);
            startActivity(permissionIntent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        initializeViews();
        initializeDatabase();
        setupListeners();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.et_username);
        passwordEditText = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);
        createAccountButton = findViewById(R.id.btn_create_account);
        settingsButton = findViewById(R.id.btn_settings);
        forgotPasswordTextView = findViewById(R.id.tv_forgot_password);
    }

    private void initializeDatabase() {
        try {
            eventsDatabase = new EventsDatabase(this);
        } catch (Exception e) {
            Log.e(TAG, "Failed to create database", e);
            Toast.makeText(this, "Failed to create database: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        createAccountButton.setOnClickListener(v -> attemptCreateAccount());
        settingsButton.setOnClickListener(v -> openSettings());
        forgotPasswordTextView.setOnClickListener(v -> handleForgotPassword());
    }

    private void attemptLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (eventsDatabase.validateUser(username, password)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                openViewEvents();
            } else {
                Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Login failed", e);
            Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptCreateAccount() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (eventsDatabase.insertUser(username, password)) {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to create account!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Account creation failed", e);
            Toast.makeText(this, "Account creation failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openViewEvents() {
        Intent intent = new Intent(this, ViewEvents.class);
        startActivity(intent);
        finish(); // Close the login activity
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void handleForgotPassword() {
        // TODO: Implement forgot password functionality
        Toast.makeText(this, "Forgot password functionality not implemented yet", Toast.LENGTH_SHORT).show();
    }
}