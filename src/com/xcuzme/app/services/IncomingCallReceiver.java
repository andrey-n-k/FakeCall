package com.xcuzme.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xcuzme.app.ui.CallActivity;

/**
 * Created by andrei on 21.02.15.
 */
public class IncomingCallReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent scheduledIntent = new Intent(context, CallActivity.class);
        scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(scheduledIntent);
    }

}