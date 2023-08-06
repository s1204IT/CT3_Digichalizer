package me.s1204.benesse.dcha.e;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings.Global;
import android.widget.Toast;

import jp.co.benesse.dcha.dchaservice.IDchaService;
import jp.co.benesse.dcha.dchautilservice.IDchaUtilService;

public class BackNova extends Activity {
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
        }, Context.BIND_AUTO_CREATE);
        Global.putInt(getContentResolver(), Global.ADB_ENABLED, 1);
        bindService(new Intent("jp.co.benesse.dcha.dchaservice.DchaService").setPackage("jp.co.benesse.dcha.dchaservice"), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mDchaService = IDchaService.Stub.asInterface(iBinder);
                try {
                    mDchaService.setSetupStatus(0);
                    mDchaService.hideNavigationBar(false);
                    mDchaService.clearDefaultPreferredApp("jp.co.benesse.touch.allgrade.b003.touchhomelauncher");
                    mDchaService.setDefaultPreferredHomeApp("com.teslacoilsw.launcher");
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
        Toast.makeText(this, "Nova Launcher を起動しました", Toast.LENGTH_LONG).show();
        startActivity(new Intent("android.intent.action.MAIN").setClassName("com.teslacoilsw.launcher", "com.teslacoilsw.launcher.NovaLauncher"));
    }
}
