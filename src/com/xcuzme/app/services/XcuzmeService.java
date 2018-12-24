package com.xcuzme.app.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.xcuzme.app.R;
import com.xcuzme.app.XcusemAlgorithm;
import com.xcuzme.app.ui.MainActivity;

/**
 * Created by andrei on 05.03.15.
 */
public class XcuzmeService extends Service implements XcusemAlgorithm.XcusemCallBack
{
    private PowerManager.WakeLock mWakeLock, mWakeScreen;
    private PowerManager mPowerManager;
    private Vibrator mVibrator;
    private SensorManager mSensorManager;
    private XcusemAlgorithm mXcusemAlgorithm;

    @Override
    public void onCreate()
    {
        super.onCreate();
        initialize();
    }

    private void initialize()
    {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mXcusemAlgorithm = new XcusemAlgorithm(this);

        acquireWakeLock();
        registerSensors();
    }

    private void acquireWakeLock()
    {
        try
        {
            if (mPowerManager == null)
            {
                return;
            }
            if (mWakeLock == null)
            {
                mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Xcusem");
            }
            if (!mWakeLock.isHeld())
            {
                mWakeLock.acquire();
            }
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
        }
    }

    private void registerSensors()
    {
        mSensorManager.registerListener(mSensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_FASTEST);
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            switch (event.sensor.getType())
            {
                case Sensor.TYPE_PROXIMITY:
                    mXcusemAlgorithm.setProximityPoint(event.values[0], System.currentTimeMillis());
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
    public int onStartCommand(Intent intent, int flag, int startId)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.excuseme_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tap_mode));
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("Run", true);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
        mBuilder.setContentIntent(resultPendingIntent);
        startForeground(123, mBuilder.build());
        return super.onStartCommand(intent, flag, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mSensorManager.unregisterListener(mSensorEventListener);
        releaseWakeLock();
        stopForeground(false);
    }

    private void releaseWakeLock()
    {
        if (mWakeLock != null && mWakeLock.isHeld())
        {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void doAction()
    {
        Log.d("Xcusem", "doAction");
        mVibrator.vibrate(new long[]{0, 50, 100, 50}, -1);
        Intent myIntent = new Intent(getBaseContext(), IncomingCallReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);
    }
}
