package com.xcuzme.app.ui.uihelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by andrei on 03.03.15.
 */
public class ErrorDialog extends DialogFragment implements DialogInterface.OnClickListener
{
    private String mMessage;

    public ErrorDialog()
    {

    }

    public ErrorDialog setMessage(String message)
    {
        mMessage = message;
        return this;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Warning").setPositiveButton("OK", this)
                .setMessage(mMessage == null ? "Error" : mMessage);
        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which)
    {
        switch (which)
        {
            case Dialog.BUTTON_POSITIVE:
                dialog.dismiss();
                break;
        }
    }

}