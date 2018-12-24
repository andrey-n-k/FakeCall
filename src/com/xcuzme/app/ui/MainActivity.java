package com.xcuzme.app.ui;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import com.revmob.RevMob;
import com.xcuzme.app.R;
import com.xcuzme.app.data.ProfileInfo;
import com.xcuzme.app.manager.DataManager;
import com.xcuzme.app.services.IncomingCallReceiver;
import com.xcuzme.app.services.XcuzmeService;
import com.xcuzme.app.ui.uihelper.TimePickerFragment;
import com.xcuzme.app.utils.BookUtils;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener,
        TimePickerFragment.TimePickerListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener
{
    private View mPersonBtn, mTimeArrowView, mDeleteTimeBtn;
    private ViewGroup mTimeBtn;
    private ViewGroup mVoiceGroup;
    private ImageView mPersonImage;

    private View mFooterView;
    private ToggleButton mStartButton;

    private SeekBar mSwitchCustom;

    public static final int CONTACT_PICKER_RESULT = 0;

    private int mHours;
    private int mMinutes;

    private RevMob mRevmob;

    private static final int VOICE_NORMAL[] = {R.drawable.english_female, R.drawable.english_male,
            R.drawable.spanish_female, R.drawable.spanish_male};

    private static final int VOICE_PRESS[] = {R.drawable.english_female_press_1, R.drawable.english_male_press_1,
            R.drawable.spanish_female_press_1, R.drawable.spanish_male_press_1};

    private boolean mIsSpain;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initialize();
    }

    private void initialize()
    {
        mPersonBtn = findViewById(R.id.select_person_btn);
        mPersonBtn.setOnClickListener(this);
        mTimeBtn = (ViewGroup) findViewById(R.id.time_btn);
        mTimeBtn.setOnClickListener(this);
        mVoiceGroup = (ViewGroup) findViewById(R.id.voice_group);
        findViewById(R.id.voice_1).setOnClickListener(this);
        findViewById(R.id.voice_2).setOnClickListener(this);
        findViewById(R.id.voice_3).setOnClickListener(this);
        findViewById(R.id.voice_4).setOnClickListener(this);
        findViewById(R.id.standby_btn).setOnClickListener(this);
        findViewById(R.id.info_button).setOnClickListener(this);
        mPersonImage = (ImageView) findViewById(R.id.person_image);
        mTimeArrowView = findViewById(R.id.time_arrow_image);
        mDeleteTimeBtn = findViewById(R.id.delete_time_btn);
        mDeleteTimeBtn.setOnClickListener(this);

        mSwitchCustom = (SeekBar) findViewById(R.id.switch_custom);
        mSwitchCustom.setOnClickListener(this);

        mFooterView = findViewById(R.id.footer);
        mStartButton = (ToggleButton) findViewById(R.id.start_btn);

        mRevmob = RevMob.start(this);
        mRevmob.showFullscreen(this);

        mIsSpain = Locale.getDefault().getLanguage().equals("es");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        fillData();
    }

    private void fillData()
    {
        int voiceId = DataManager.getVoiceId(this);
        if (voiceId != -1)
        {
            setVoice(voiceId);
        }
//        String personId = DataManager.getPersonId(this);
//        if (personId != null && DataManager.getProfileInfo(this) != null)
//        {
//            ;
//        }
        fillInfo();
        boolean isStandBy = DataManager.isStandBy(this);

        mSwitchCustom.setProgress(isStandBy ? 100 : 0);
        mSwitchCustom.setOnSeekBarChangeListener(this);
        if (isStandBy)
        {
            findViewById(R.id.seek_bar_background).setBackgroundResource(R.drawable._switch_bcg);
            startXcusemService();
        }
        else
        {
            findViewById(R.id.seek_bar_background).setBackgroundResource(R.drawable._switch_bcg_1);
            stopXcusemService();
        }

        if (DataManager.getTime(this) == -1)
        {
            ((TextView) ((ViewGroup) mTimeBtn.getChildAt(0)).getChildAt(1)).setText(R.string.time_text);
            mTimeArrowView.setVisibility(View.VISIBLE);
            mDeleteTimeBtn.setVisibility(View.GONE);
            mFooterView.setVisibility(View.GONE);
            mStartButton.setChecked(false);
        }
        else
        {
            long minutes = DataManager.getTime(this) / 60;
            int hour = (int) minutes / 60;
            int minut = (int) minutes % 60;
            ((TextView) ((ViewGroup) mTimeBtn.getChildAt(0)).getChildAt(1)).setText((hour < 10 ? ("0" + hour) : hour) + ":" + (minut < 10 ? ("0" + minut) : minut));
            mTimeArrowView.setVisibility(View.GONE);
            mDeleteTimeBtn.setVisibility(View.VISIBLE);
            mFooterView.setVisibility(View.VISIBLE);
            mStartButton.setChecked(DataManager.isStartAlarm(this));
        }
        mStartButton.setOnCheckedChangeListener(this);
    }

    private void setVoice(int voiceId)
    {
        DataManager.setVoiceId(this, voiceId);
        for (int i = 0; i < 4; ++i)
        {
            ((CheckBox) ((ViewGroup) ((ViewGroup) mVoiceGroup.getChildAt(i)).getChildAt(0)).getChildAt(0)).setChecked(false);

            ((CheckBox) ((ViewGroup) ((ViewGroup) mVoiceGroup.getChildAt(i))
                    .getChildAt(0)).getChildAt(0)).setBackgroundResource(VOICE_NORMAL[i]);
            ((TextView) ((ViewGroup) ((ViewGroup) mVoiceGroup.getChildAt(i)).getChildAt(0)).getChildAt(1)).setTextColor(0xFFffffff);
        }
        ((CheckBox) ((ViewGroup) ((ViewGroup) mVoiceGroup.getChildAt(voiceId)).getChildAt(0)).getChildAt(0)).setChecked(true);

        ((CheckBox) ((ViewGroup) ((ViewGroup) mVoiceGroup.getChildAt(voiceId))
                .getChildAt(0)).getChildAt(0)).setBackgroundResource(VOICE_PRESS[voiceId]);
        ((TextView) ((ViewGroup) ((ViewGroup) mVoiceGroup.getChildAt(voiceId)).getChildAt(0)).getChildAt(1)).setTextColor(0xFFffeb3b);
    }

    private void fillInfo()
    {
        ProfileInfo profileInfo = DataManager.getProfileInfo(this);
        if (profileInfo == null)
        {
            profileInfo = new ProfileInfo();
        }
        ((TextView) findViewById(R.id.name_text)).setText(profileInfo.getName() + " " + profileInfo.getSecondName());
        ((TextView) findViewById(R.id.phone_text)).setText(profileInfo.getPhone());

        String imageId = profileInfo.getImageId();
        if (!imageId.equals(""))
        {
            try
            {
                mPersonImage.setImageURI(BookUtils.getPersonImage(this, DataManager.getProfileInfo(this).getImageId()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mRevmob.releaseLoadedBanner();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.select_person_btn:
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
                break;
            case R.id.time_btn:
//                if (DataManager.getPersonId(this) == null)
//                {
//                    showErrorDialog(getString(R.string.select_person));
//                    return;
//                }
//                if (DataManager.getVoiceId(this) == -1)
//                {
//                    showErrorDialog(getString(R.string.select_voice));
//                    return;
//                }
                showTimeDialog();
                break;
            case R.id.voice_1:
                setVoice(0);
                break;
            case R.id.voice_2:
                setVoice(1);
                break;
            case R.id.voice_3:
                setVoice(2);
                break;
            case R.id.voice_4:
                setVoice(3);
                break;
            case R.id.delete_time_btn:
                stopXcusem();
                DataManager.setStartAlarm(this, false);
                break;
            case R.id.standby_btn:
            case R.id.switch_custom:
                int progress = mSwitchCustom.getProgress();
                boolean key = !(progress >= 50);
                DataManager.setStandBy(this, key);
                if (key)
                {
                    mSwitchCustom.setProgress(100);
                    findViewById(R.id.seek_bar_background).setBackgroundResource(R.drawable._switch_bcg);
//                    startXcusemService();
                }
                else
                {
                    mSwitchCustom.setProgress(0);
                    findViewById(R.id.seek_bar_background).setBackgroundResource(R.drawable._switch_bcg_1);
//                    stopXcusemService();
                }
                break;
            case R.id.info_button:
                startActivity(new Intent(this, TutorialActivity.class));
                break;
        }
    }

    private void showTimeDialog()
    {
        DialogFragment dialog = new TimePickerFragment();
        dialog.show(getFragmentManager(), "TimePickerFragment");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CONTACT_PICKER_RESULT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                DataManager.setPersonId(this, data.getData());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onSetTimePicker(int hour, int minute)
    {
        String time = (hour < 10 ? ("0" + hour) : hour) + ":" + (minute < 10 ? ("0" + minute) : minute);
        ((TextView) ((ViewGroup) mTimeBtn.getChildAt(0)).getChildAt(1)).setText(time);
        mTimeArrowView.setVisibility(View.GONE);
        mDeleteTimeBtn.setVisibility(View.VISIBLE);
        mFooterView.setVisibility(View.VISIBLE);
        mStartButton.setChecked(false);
        findViewById(R.id.scroll_view).post(new Runnable()
        {
            @Override
            public void run()
            {
                ((ScrollView) findViewById(R.id.scroll_view)).fullScroll(View.FOCUS_DOWN);
            }
        });

        mHours = hour;
        mMinutes = minute;
//        startXcusem(hour, minute);
    }

    private void startXcusem(int hourOfDay, int minute)
    {
        Intent myIntent = new Intent(getBaseContext(), IncomingCallReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void stopXcusem()
    {
        Intent myIntent = new Intent(getBaseContext(), IncomingCallReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        mFooterView.setVisibility(View.GONE);
        mDeleteTimeBtn.setVisibility(View.GONE);
        mTimeArrowView.setVisibility(View.VISIBLE);
        DataManager.setTime(this, -1);
        ((TextView) ((ViewGroup) mTimeBtn.getChildAt(0)).getChildAt(1)).setText(R.string.time_text);
    }

    private void startXcusemService()
    {
        startService(new Intent(this, XcuzmeService.class));
    }

    private void stopXcusemService()
    {
        stopService(new Intent(this, XcuzmeService.class));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b)
    {
        int progress = seekBar.getProgress();
        boolean key = progress >= 50;
        DataManager.setStandBy(this, key);
        if (key)
        {
            startXcusemService();
        }
        else
        {
            stopXcusemService();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        if (seekBar.getProgress() >= 50)
        {
            mSwitchCustom.setProgress(100);
            findViewById(R.id.seek_bar_background).setBackgroundResource(R.drawable._switch_bcg);
        }
        else
        {
            mSwitchCustom.setProgress(0);
            findViewById(R.id.seek_bar_background).setBackgroundResource(R.drawable._switch_bcg_1);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b)
    {
        if (b)
        {
            startXcusem(mHours, mMinutes);
            DataManager.setStartAlarm(this, true);
        }
        else
        {
            stopXcusem();
            DataManager.setStartAlarm(this, false);
        }
    }
}
