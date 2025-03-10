package com.example.waeilmikhaeil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class Settings extends AppCompatActivity {
    private static final String TAG = "Settings";

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
        switchSmsNotifications.setChecked(sharedPreferences.getBoolean("SMS_NOTIFICATIONS_ENABLED", false));
    }

    private void setupListeners() {
        switchSmsNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "SMS Notifications " + (isChecked ? "enabled" : "disabled"));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("SMS_NOTIFICATIONS_ENABLED", isChecked);
            editor.apply();
        });
    }
}