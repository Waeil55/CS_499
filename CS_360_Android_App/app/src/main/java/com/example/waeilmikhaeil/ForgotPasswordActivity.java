package com.example.waeilmikhaeil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends BaseActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private TextInputEditText etUsername, etNewPassword;
    private MaterialButton btnResetPassword;
    private EventsDatabase eventsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        // Initialize views
        etUsername = findViewById(R.id.et_username);
        etNewPassword = findViewById(R.id.et_new_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        // Initialize database
        eventsDatabase = new EventsDatabase(this);

        // Set up reset button listener
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String newPassword = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";

        if (username.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please enter username and new password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Basic password strength check
        if (newPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Check if username exists
            if (eventsDatabase.userExists(username)) {
                // Update the password
                boolean updated = eventsDatabase.updatePassword(username, newPassword);
                if (updated) {
                    Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resetting password", e);
            Toast.makeText(this, "Error resetting password", Toast.LENGTH_SHORT).show();
        }
    }
}