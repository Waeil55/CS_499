package com.example.waeilmikhaeil;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsNotificationManager {
    private static final String TAG = "SmsNotificationManager";
    private Context context;
    private SharedPreferences sharedPreferences;

    public SmsNotificationManager(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
    }

    public void sendEventNotification(String phoneNumber, String message) {
        boolean smsPermissionGranted = sharedPreferences.getBoolean("SMS_PERMISSION_GRANTED", false);

        if (smsPermissionGranted) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Log.d(TAG, "SMS sent successfully");
            } catch (Exception e) {
                Log.e(TAG, "Failed to send SMS", e);
            }
        } else {
            Log.d(TAG, "SMS permission not granted. Notification not sent.");
        }
    }
}