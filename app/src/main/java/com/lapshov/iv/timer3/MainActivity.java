package com.lapshov.iv.timer3;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends Activity {

    private TextView mTextView;
    private static String[] mins = new String[]{"00", "01", "05", "10", "15", "20", "25", "30", "35", "40", "45", "55"};
    private static String[] secs = new String[]{"00", "01", "05", "10", "15", "20", "25", "30", "35", "40", "45", "55"};

    private int minutesRes = 0;
    private int secondsRes = 0;
    private Button addAlarm;
    public static String MINUTES_KEY = "minutes";
    public static String SECONDS_KEY = "seconds";
    private AlarmManager mAlarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                NumberPicker minutes = (NumberPicker) stub.findViewById(R.id.minute);
                NumberPicker seconds = (NumberPicker) stub.findViewById(R.id.seconds);
                seconds.setDisplayedValues(secs);
                minutes.setDisplayedValues(mins);
                //seconds.setMinValue(0);
                seconds.setMaxValue(secs.length - 1);
                minutes.setMaxValue(mins.length - 1);

                minutesRes = Integer.parseInt(mins[minutes.getValue()]);
                secondsRes = Integer.parseInt(secs[seconds.getValue()]);

                minutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        minutesRes = Integer.parseInt(mins[newVal]);
                    }
                });

                seconds.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        secondsRes = Integer.parseInt(secs[newVal]);
                    }
                });
                addAlarm = (Button) stub.findViewById(R.id.button);
                addAlarm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, timer.class);
                        intent.putExtra(MINUTES_KEY, minutesRes);
                        intent.putExtra(SECONDS_KEY, secondsRes);
                        startActivity(intent);
                    }
                });

            }
        });
        Calendar now = Calendar.getInstance();
        int nowMinutes = now.get(Calendar.MINUTE);
        int delay = (60 - nowMinutes) * 60 * 1000;
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis() + delay, AlarmManager.INTERVAL_HOUR, pendingIntent);
    }
}
