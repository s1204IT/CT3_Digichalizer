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

import jp.co.benesse.dcha.dchaservice.IDchaService;
import jp.co.benesse.dcha.dchautilservice.IDchaUtilService;

import static me.s1204.benesse.dcha.e.InitDcha.*;

public class BackNova extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (InitDcha.checkPermission(this)) return;

        if (!bindService(new Intent(UTIL_SRV).setPackage(UTIL_PKG), new ServiceConnection() {
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
        }, Context.BIND_AUTO_CREATE)) {
            makeText(this, R.string.fail_util_connect);
            finish();
            return;
        }

        if (!bindService(new Intent(DCHA_SRV).setPackage(DCHA_PKG), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mDchaService = IDchaService.Stub.asInterface(iBinder);
                try {
                    mDchaService.setSetupStatus(0);
                    mDchaService.hideNavigationBar(false);
                    mDchaService.clearDefaultPreferredApp(TouchHomeLauncher);
                    mDchaService.setDefaultPreferredHomeApp(NOVA_PKG);
                    mDchaService.removeTask(null);
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
            finish();
            return;
        }

        makeText(this, R.string.start_nova);

        try {
            startActivity(new Intent(Intent.ACTION_MAIN).setClassName(NOVA_PKG, NovaLauncher));
        } catch (ActivityNotFoundException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            makeText(this, R.string.fail_nova);
        }
        finish();
    }

}
