package me.s1204.benesse.dcha.e;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings.Global;

import jp.co.benesse.dcha.dchaservice.IDchaService;
import jp.co.benesse.dcha.dchautilservice.IDchaUtilService;

public class InitDcha extends Activity {
    IDchaService mDchaService;
    IDchaUtilService mUtilService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindService(new Intent("jp.co.benesse.dcha.dchautilservice.DchaUtilService").setPackage("jp.co.benesse.dcha.dchautilservice"), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mUtilService = IDchaUtilService.Stub.asInterface(iBinder);
                try {
                    mUtilService.setForcedDisplaySize(1280, 800);
                } catch (RemoteException ignored) {
                }
                unbindService(this);
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                unbindService(this);
            }
        }, 1);
        Global.putInt(getContentResolver(), Global.ADB_ENABLED, 1);
        bindService(new Intent("jp.co.benesse.dcha.dchaservice.DchaService").setPackage("jp.co.benesse.dcha.dchaservice"), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mDchaService = IDchaService.Stub.asInterface(iBinder);
                try {
                    mDchaService.setSetupStatus(3);
                    mDchaService.hideNavigationBar(true);
                    mDchaService.clearDefaultPreferredApp("com.teslacoilsw.launcher");
                    mDchaService.setDefaultPreferredHomeApp("jp.co.benesse.touch.allgrade.b003.touchhomelauncher");
                    mDchaService.removeTask(null);
                } catch (RemoteException ignored) {
                }
                unbindService(this);
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                unbindService(this);
            }
        }, 1);
        finishAndRemoveTask();
        startActivity(new Intent("android.intent.action.MAIN").setClassName("jp.co.benesse.touch.allgrade.b003.touchhomelauncher", "jp.co.benesse.touch.allgrade.b003.touchhomelauncher.HomeLauncherActivity"));
    }
}
