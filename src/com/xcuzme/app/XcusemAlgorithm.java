package com.xcuzme.app;

import android.util.Log;

/**
 * Created by andrei on 12.02.15.
 */
public class XcusemAlgorithm
{
    public interface XcusemCallBack
    {
        void doAction();
    }

    private long mStartTime;
    private int mDownCount;
    private int mUpCount;
    private XcusemCallBack mXcusemCallBack;

    public XcusemAlgorithm(XcusemCallBack fakeCallCallBack)
    {
        mXcusemCallBack = fakeCallCallBack;
        reset();
    }

    private void reset()
    {
        mStartTime = 0;
        mUpCount = 0;
        mDownCount = 0;
    }

    public void setProximityPoint(double x, long time)
    {

        if (mUpCount == 0 && mDownCount == 0)
        {
            if (x <= 1)
            {
                mDownCount++;
                mStartTime = time;
            }
        }
        else
        {
            if (time - mStartTime > 3000)
            {
                reset();
                return;
            }
            if (x <= 0.5)
            {
                mDownCount ++;
            }
            else
            {
                mUpCount ++;
            }
            Log.d("Xcusem", "mDownCount = " + mDownCount + " mUpCount = " + mUpCount);
            if (mDownCount == 3 && mUpCount == 3)
            {
                if (time - mStartTime <= 3000)
                {
                    mXcusemCallBack.doAction();
                }
                reset();
            }
        }
    }
}
