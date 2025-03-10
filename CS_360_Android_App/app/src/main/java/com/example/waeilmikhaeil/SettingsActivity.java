package com.example.waeilmikhaeil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    private SwitchCompat switchSmsNotifications;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        switchSmsNotifications = findViewById(R.id.switch_sms_notifications);
        boolean currentSetting = sharedPreferences.getBoolean("SMS_NOTIFICATIONS_ENABLED", false);
        switchSmsNotifications.setChecked(currentSetting);
        Log.d(TAG, "SMS Notifications currently " + (currentSetting ? "enabled" : "disabled"));
    }

    private void setupListeners() {
        switchSmsNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "SMS Notifications " + (isChecked ? "enabled" : "disabled"));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("SMS_NOTIFICATIONS_ENABLED", isChecked);
            editor.apply();

            String message = isChecked ? "SMS notifications enabled" : "SMS notifications disabled";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "SettingsActivity paused, settings saved");
    }
}