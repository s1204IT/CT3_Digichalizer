package me.s1204.benesse.dcha.e;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import jp.co.benesse.dcha.dchaservice.IDchaService;

import static me.s1204.benesse.dcha.e.InitDcha.*;

public class Reboot extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitDcha.checkPermission(this);

        if (!bindService(new Intent(DCHA_SRV).setPackage(DCHA_PKG), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mDchaService = IDchaService.Stub.asInterface(iBinder);
                makeText(getApplicationContext(), R.string.reboot_message);
                startActivity(new Intent(Intent.ACTION_MAIN).setClassName(getPackageName(), getPackageName() + BACK_NOVA));
                try {
                    mDchaService.rebootPad(0, null);
                } catch (RemoteException ignored) {
                }
                unbindService(this);
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                unbindService(this);
            }
        }, Context.BIND_AUTO_CREATE)) {
            makeText(this, R.string.fail_dcha_connect);
        }
        finish();
    }
}
