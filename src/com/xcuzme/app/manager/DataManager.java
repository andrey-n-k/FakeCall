package com.xcuzme.app.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import com.xcuzme.app.data.ProfileInfo;
import com.xcuzme.app.utils.BookUtils;

/**
 * Created by andrei on 13.02.15.
 */
public class DataManager
{
    private static final String PREFERENCES = "XcusemPref";
    private static final String PERSON_ID = "person_id";
    private static final String VOICE_ID = "voice_id";
    private static final String TIME = "time";
    private static final String STAND_BY = "stand_by";
    private static final String PROFILE = "profile";

    private static final String START_ALARM = "start_alarm";


    public static void setPersonId(Context context, Uri contactData)
    {
        String id = null;
        Cursor c =  context.getContentResolver().query(contactData, null, null, null, null);
        if (c.moveToFirst())
        {
            id = c.getString(c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
        }
        SharedPreferences.Editor prefEditor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        if (prefEditor != null)
        {
            prefEditor.putString(PERSON_ID, id);
            prefEditor.apply();
        }
        String imageId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
        ProfileInfo profileInfo = ProfileInfo.build(id, BookUtils.getName(context, id),
                BookUtils.getSecondName(context, id), BookUtils.getPhoneByID(context ,id), imageId);

        DataManager.setProfileInfo(context, profileInfo);
        c.close();
    }

    public static String getPersonId(Context context)
    {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(PERSON_ID, null);
    }

    public static void setVoiceId(Context context, int id)
    {
        SharedPreferences.Editor prefEditor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        if (prefEditor != null)
        {
            prefEditor.putInt(VOICE_ID, id);
            prefEditor.apply();
        }
    }

    public static int getVoiceId(Context context)
    {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getInt(VOICE_ID, 1);
    }


    public static void setTime(Context context, int time)
    {
        SharedPreferences.Editor prefEditor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        if (prefEditor != null)
        {
            prefEditor.putInt(TIME, time);
            prefEditor.apply();
        }
    }

    public static boolean isStartAlarm(Context context)
    {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getBoolean(START_ALARM, false);
    }


    public static void setStartAlarm(Context context, boolean key)
    {
        SharedPreferences.Editor prefEditor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        if (prefEditor != null)
        {
            prefEditor.putBoolean(START_ALARM, key);
            prefEditor.apply();
        }
    }

    public static int getTime(Context context)
    {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getInt(TIME, -1);
    }

    public static void setStandBy(Context context, boolean key)
    {
        SharedPreferences.Editor prefEditor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        if (prefEditor != null)
        {
            prefEditor.putBoolean(STAND_BY, key);
            prefEditor.apply();
        }
    }

    public static boolean isStandBy(Context context)
    {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getBoolean(STAND_BY, false);
    }

    public static void setProfileInfo(Context context, ProfileInfo data)
    {
        SharedPreferences.Editor prefEditor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        if (prefEditor != null)
        {
            prefEditor.putString(PROFILE, data.toString());
            prefEditor.apply();
        }
    }

    public static ProfileInfo getProfileInfo(Context context)
    {
        return ProfileInfo.build(context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(PROFILE, null));
    }
}
