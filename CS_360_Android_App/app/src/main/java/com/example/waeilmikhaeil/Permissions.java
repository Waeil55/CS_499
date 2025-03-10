package com.example.waeilmikhaeil;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permissions);

        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        Button btnGrantPermission = findViewById(R.id.btn_grant_permission);
        Button btnDenyPermission = findViewById(R.id.btn_deny_permission);

        btnGrantPermission.setOnClickListener(v -> requestSmsPermission());
        btnDenyPermission.setOnClickListener(v -> denyPermissionAndProceed());
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE);
        } else {
            proceedToMainActivity(true);
        }
    }

    private void denyPermissionAndProceed() {
        proceedToMainActivity(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            proceedToMainActivity(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    private void proceedToMainActivity(boolean smsPermissionGranted) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("SMS_PERMISSION_GRANTED", smsPermissionGranted);
        editor.putBoolean("PERMISSION_CHECKED", true);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}