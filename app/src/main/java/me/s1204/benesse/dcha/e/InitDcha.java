package me.s1204.benesse.dcha.e;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings.Global;
import android.widget.Toast;

import android.os.BenesseExtension;
import jp.co.benesse.dcha.dchaservice.IDchaService;

public class InitDcha extends Activity {
    IDchaService mDchaService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //noinspection ResultOfMethodCallIgnored
            BenesseExtension.setForcedDisplaySize(1280, 800);
            Global.putInt(getContentResolver(), Global.ADB_ENABLED, 1);
        } catch (SecurityException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            Toast.makeText(this, "WRITE_SECURE_SETTINGS を付与してください", Toast.LENGTH_LONG).show();
            finish();
        }
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
        }, Context.BIND_AUTO_CREATE);
        finishAndRemoveTask();
        Toast.makeText(this, "デジチャレモード を起動しています...", Toast.LENGTH_SHORT).show();
        try {
            startActivity(new Intent("android.intent.action.MAIN").setClassName("jp.co.benesse.touch.allgrade.b003.touchhomelauncher", "jp.co.benesse.touch.allgrade.b003.touchhomelauncher.HomeLauncherActivity"));
        } catch (ActivityNotFoundException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            Toast.makeText(this, "ホームランチャーを起動できませんでした", Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
