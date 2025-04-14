package com.example.waeilmikhaeil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Initialize views using IDs from settings.xml
        SwitchCompat switchSmsNotifications = findViewById(R.id.switch_sms_notifications);
        SwitchCompat switchDarkMode = findViewById(R.id.switch_dark_mode);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Load saved preferences
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        switchSmsNotifications.setChecked(sharedPreferences.getBoolean("sms_notifications", false));
        switchDarkMode.setChecked(isDarkMode);

        // Set up listeners
        switchSmsNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("sms_notifications", isChecked);
            editor.apply();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "Dark mode switch toggled to: " + isChecked);
            if (isChecked != sharedPreferences.getBoolean("dark_mode", false)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("dark_mode", isChecked);
                editor.apply();
                // Restart the app to apply the theme to all activities
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnLogout.setOnClickListener(v -> {
            // Clear login state
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("is_logged_in", false);
            editor.apply();

            // Navigate to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        // Bottom Navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_add) {
                startActivity(new Intent(this, AddEvents.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.nav_view) {
                startActivity(new Intent(this, AllEventsActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.nav_search) {
                startActivity(new Intent(this, SearchEvents.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true;
            } else if (itemId == R.id.nav_help) {
                startActivity(new Intent(this, HelpActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            return false;
        });
    }
}