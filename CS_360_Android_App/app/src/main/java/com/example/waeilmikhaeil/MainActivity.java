package com.example.waeilmikhaeil;

import android.content.Intent;
import android.content.SharedPreferences; // Add this import
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private EditText usernameEditText, passwordEditText;
    private Button loginButton, createAccountButton;
    private TextView forgotPasswordTextView;
    private EventsDatabase eventsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean permissionChecked = sharedPreferences.getBoolean("PERMISSION_CHECKED", false);

        // Check if already logged in
        if (sharedPreferences.getBoolean("is_logged_in", false)) {
            openViewEvents();
            return;
        }

        if (!permissionChecked) {
            startActivity(new Intent(this, Permissions.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        initializeViews();
        if (!initializeDatabase()) {
            Toast.makeText(this, "Unable to initialize database. Please try again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setupListeners();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.et_username);
        passwordEditText = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);
        createAccountButton = findViewById(R.id.btn_create_account);
        forgotPasswordTextView = findViewById(R.id.tv_forgot_password);
    }

    private boolean initializeDatabase() {
        try {
            eventsDatabase = new EventsDatabase(this);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to create database", e);
            return false;
        }
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        createAccountButton.setOnClickListener(v -> attemptCreateAccount());
        forgotPasswordTextView.setOnClickListener(v -> handleForgotPassword());
    }

    private void attemptLogin() {
        String username = usernameEditText.getText() != null ? usernameEditText.getText().toString().trim() : "";
        String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (eventsDatabase.validateUser(username, password)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_logged_in", true);
                editor.apply();
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                openViewEvents();
            } else {
                Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during login", e);
            Toast.makeText(this, "Login error", Toast.LENGTH_SHORT).show();
        }
    }

    private void attemptCreateAccount() {
        String username = usernameEditText.getText() != null ? usernameEditText.getText().toString().trim() : "";
        String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (eventsDatabase.userExists(username)) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            if (eventsDatabase.insertUser(username, password)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_logged_in", true);
                editor.apply();
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                usernameEditText.setText("");
                passwordEditText.setText("");
                openViewEvents();
            } else {
                Toast.makeText(this, "Failed to create account!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating account", e);
            Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show();
        }
    }

    private void openViewEvents() {
        Intent intent = new Intent(this, AllEventsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void handleForgotPassword() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }
}