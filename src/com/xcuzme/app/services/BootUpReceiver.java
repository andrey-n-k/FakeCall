package com.xcuzme.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xcuzme.app.manager.DataManager;

/**
 * Created by andrei on 27.03.15.
 */
public class BootUpReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        /***** For start Service  ****/
        if (DataManager.isStandBy(context))
        {
            context.startService(new Intent(context, XcuzmeService.class));
        }
    }

}