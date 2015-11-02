/*
Copyright (c) 2013 Joel Andrews
Distributed under the MIT License: http://opensource.org/licenses/MIT
 */

package com.ntt.sleeptimer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Schedule the sleep timer.
 *
 * @author Joel Andrews
 */
public class SetTimerActivity extends Activity {

    private static final String LOG_TAG = SetTimerActivity.class.getName();

    private static final String HOURS_KEY = MainActivity.class.getName() + ".hours";
    private static final String MINUTES_KEY = MainActivity.class.getName() + ".minutes";

    private TimerManager timerManager;
    private SharedPreferences sharedPreferences;
    private CountdownNotifier countdownNotifier;
    private PauseMusicNotifier pauseMusicNotifier;

    private SeekArc mSeekArc;
    private TextView mSeekArcProgress;


    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_timer);
        mSeekArc = (SeekArc) findViewById(R.id.seekArc);
        mSeekArcProgress = (TextView) findViewById(R.id.seekArcProgress);

        mSeekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                mSeekArcProgress.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });
        // Prevent the soft keyboard from appearing until explicitly launched by the user
        // Source: http://stackoverflow.com/a/2059394
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        this.timerManager = TimerManager.get(this);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.countdownNotifier = CountdownNotifier.get(this);
        this.pauseMusicNotifier = PauseMusicNotifier.get(this);

    }

    /**
     * Starts a countdown timer based on the current settings.
     *
     * @param view The view that triggered this action
     */
    public void startTimer(View view) {
        Log.d(LOG_TAG, "Sleep timer started by view " + view.getId());

        // The currently selected hour and minute values should become the new defaults
//        setDefaultTimerLength(hours, minutes);

        // It is possible that a previous music paused notification is still active; remove it
        pauseMusicNotifier.cancelNotification();

        timerManager.setTimer(Integer.parseInt(mSeekArcProgress.getText().toString()));

        countdownNotifier.postNotification(timerManager.getScheduledTime());

        Toast.makeText(this, R.string.timer_started, Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK);
        finish();
    }

    /**
     * Sets the default timer length to the specified number of hours and minutes.
     *
     * @param hours The number of hours
     * @param minutes The number of minutes
     */
    private void setDefaultTimerLength(int hours, int minutes) {
        sharedPreferences.edit()
                .putInt(HOURS_KEY, hours)
                .putInt(MINUTES_KEY, minutes)
                .apply();
    }

}
