package com.xcuzme.app.ui;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.xcuzme.app.R;
import com.xcuzme.app.data.ProfileInfo;
import com.xcuzme.app.manager.DataManager;
import com.xcuzme.app.utils.BookUtils;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by andrei on 17.02.15.
 */
public class CallActivity extends Activity implements View.OnClickListener, MediaPlayer.OnCompletionListener
{
    private View mAnswerBtn;
    private View mDeclineBtn;
    private Ringtone mRingtone;

    private PowerManager.WakeLock mWakeLock;
    private PowerManager mPowerManager;
    private MediaPlayer mMediaPlayer;

    private boolean isPlay;
    private Uri mUri;
    private boolean mIsSpain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.call);
        DataManager.setTime(this, -1);
        initialize();

    }



    private void initialize()
    {
        mAnswerBtn = findViewById(R.id.answer);
        mAnswerBtn.setOnClickListener(this);
        mDeclineBtn = findViewById(R.id.decline);
        mDeclineBtn.setOnClickListener(this);

        mIsSpain = Locale.getDefault().getLanguage().equals("es");

        fillInfo();

        setMediaPlayer();
    }

    private void  setMediaPlayer()
    {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mRingtone = RingtoneManager.getRingtone(getApplicationContext(), notification);


        String path = "android.resource://com.xcuzme.app/raw/";

        switch (DataManager.getVoiceId(this))
        {
            case 0:
                path += mIsSpain ? "aeropuerto" : "janeoffice";
                break;
            case 1:
                path += mIsSpain ? "oficina" : "manoffice";
                break;
            case 2:
                path += mIsSpain ? "amiga" : "friend_female";
                break;
            case 3:
                path += mIsSpain ? "bomberos" : "friend_male";
                break;
        }
        mUri= Uri.parse(path);

        mMediaPlayer = new MediaPlayer();
        try
        {
            mMediaPlayer.setDataSource(this, mUri);
            //            mMediaPlayer.prepare();
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
        catch (IOException e)
        {
            mMediaPlayer = null;
            e.printStackTrace();
        }

//        try
//        {
//            mMediaPlayer.prepare();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
    }

    private void fillInfo()
    {
        ProfileInfo profileInfo = DataManager.getProfileInfo(this);
        ((TextView) findViewById(R.id.name_text)).setText(profileInfo.getName() + " " + profileInfo.getSecondName());
        ((TextView) findViewById(R.id.phone_text)).setText(profileInfo.getPhone());

        String imageId = profileInfo.getImageId();
        if (!imageId.equals(""))
        {
            try
            {
                ((ImageView) findViewById(R.id.person_image)).setImageURI(BookUtils.getPersonImage(this, DataManager.getProfileInfo(this).getImageId()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mRingtone.play();
//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.reset();
//        mMediaPlayer.release();

        acquireWakeLock();
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
                mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Xcusem");
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

    @Override
    protected void onPause()
    {
        super.onPause();
        releaseWakeLock();
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
    protected void onDestroy()
    {
        super.onDestroy();
        mRingtone.stop();
        if (mMediaPlayer != null)
        {
            //            if (mMediaPlayer.isPlaying())
            //            {
            //                mMediaPlayer.stop();
            //            }
            mMediaPlayer.release();
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.answer:
                if (!isPlay)
                {
                    isPlay = true;
                    mRingtone.stop();
                    if (mMediaPlayer != null)
                    {
//                        if (!mMediaPlayer.isPlaying())
//                        {
//                            mMediaPlayer.stop();
//                        }
//                        mMediaPlayer.start();
                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
                        {
                            public void onPrepared(MediaPlayer mp)
                            {
                                mp.start();
                            }
                        });
                        mMediaPlayer.prepareAsync();
                    }
                }
                break;
            case R.id.decline:
                finish();
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    return;
                }
                finish();
            }
        }).start();
    }
}
