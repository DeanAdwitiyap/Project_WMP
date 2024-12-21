package com.example.waterreminderapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private TextView tvTarget;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("WaterPrefs", MODE_PRIVATE);
        tvTarget = findViewById(R.id.tvTarget);
        Button btnSetTarget = findViewById(R.id.btnSetTarget);
        Button btnLogDrink = findViewById(R.id.btnLogDrink);

        dbHelper = new DatabaseHelper(this);

        // Display daily target
        int target = preferences.getInt("dailyTarget", 0);
        tvTarget.setText("Target Harian: " + target + " Liter");

        // Set new target
        btnSetTarget.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("dailyTarget", 3); // Example: set target to 3 liters
            editor.apply();
            tvTarget.setText("Target Harian: 3 Liter");
        });

        // Log water intake
        btnLogDrink.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("INSERT INTO water_log (date, amount) VALUES ('2024-12-21', 250)");
        });

        // Schedule notifications
        scheduleNotification();
    }

    public void scheduleNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        long interval = AlarmManager.INTERVAL_HOUR; // Reminder every hour
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }
}