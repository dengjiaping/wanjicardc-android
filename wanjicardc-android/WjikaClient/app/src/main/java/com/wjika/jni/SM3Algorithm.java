package com.wjika.jni;

import android.util.Log;

/**
 * Created by jacktian on 15/11/5.
 */
public class SM3Algorithm {

    String key;
    long timestemp;

    static {
        System.loadLibrary("JniSM3Algorithm");

    }


    public int getSM3TOTP(long timestemp, String key){
        this.key = key;
        this.timestemp = timestemp;
        return getTokenWithSM3TOTP(timestemp);
    }

    public native int getTokenWithSM3TOTP(long l);
}
