package com.lapshov.iv.timer3;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class timer extends Activity {

    private TextView loopNumber;
    private int minutes;
    private int seconds;
    private double progress = 0;
    private long newTime = 0;
    private long oldTime = 0;
    CountDownTimerWithPause timer;
    private long loops = 0;
    private static final long[] vibrationPattern = {0, 500, 50, 300};
    private static final int indexInPatternToRepeat = -1;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        minutes = getIntent().getIntExtra(MainActivity.MINUTES_KEY, 0);
        seconds = getIntent().getIntExtra(MainActivity.SECONDS_KEY, 0);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                loopNumber = (TextView) stub.findViewById(R.id.textView);
                final ProgressWheel bar = (ProgressWheel) stub.findViewById(R.id.pw_spinner);
                setSettings(bar);


                final int totalSec = minutes * 60 + seconds;
                timer = getNewTimer(totalSec, bar);
                timer.create();
                bar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!timer.isPaused()) {
                            timer.pause();
                        } else {
                            timer.resume();
                        }
                    }
                });
            }
        });
    }

    private CountDownTimerWithPause getNewTimer(final int totalSec, final ProgressWheel bar) {
        return new CountDownTimerWithPause(totalSec * 1000 + 999, 1000, true) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished - 1000 > 1000) {
                    newTime = totalSec * 1000 - (millisUntilFinished - 1000);
                    double step = (360 * (double) (newTime - oldTime)) / (1000 * totalSec);
                    progress += step;
                    bar.setProgress((int) progress);
                    setTime(millisUntilFinished - 1000, bar);

                    //Log.v("bl", step + " " + newTime + " " + oldTime + " " + (newTime - oldTime));
                    oldTime = newTime;
                } else {
                    bar.setProgress(360);
                    setTime(0, bar);
                }

            }

            @Override
            public void onFinish() {
                cancel();
                oldTime = 0;
                progress = 0;
                timer = getNewTimer(totalSec, bar);
                timer.create();
                loops++;
                loopNumber.setText(loops + " loop(s)");

                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
            }
        };
    }

    private void setTime(long l, ProgressWheel wheel) {

        long minutes = TimeUnit.MILLISECONDS.toMinutes(l);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(l);
        seconds = seconds - minutes * 60;
        String result = String.format("%02d:%02d", minutes, seconds);
        wheel.setText(result);
        //Log.v("setTime", l + " " + result);
    }

    private void setSettings(ProgressWheel bar) {
        bar.setText(String.format("%02d:%02d", minutes, seconds));
        bar.setTextSize(50);
        bar.setTextColor(Color.WHITE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        timer.pause();
    }
}
