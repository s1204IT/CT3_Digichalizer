package me.s1204.benesse.dcha.e;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.widget.Toast;

import jp.co.benesse.dcha.dchaservice.IDchaService;
import jp.co.benesse.dcha.dchautilservice.IDchaUtilService;

public class InitDcha extends Activity {
    protected static final String ACCESS_SYSTEM = "jp.co.benesse.dcha.permission.ACCESS_SYSTEM";
    protected static final String DCHA_PFX = "jp.co.benesse.dcha";
    protected static final String DCHA_PKG = DCHA_PFX + ".dchaservice";
    protected static final String DCHA_SRV = DCHA_PKG + ".DchaService";
    protected static final String UTIL_PKG = DCHA_PFX + ".dchautilservice";
    protected static final String UTIL_SRV = UTIL_PKG + ".DchaUtilService";
    protected static final String BACK_NOVA = ".BackNova";
    protected static final String NOVA_PKG = "com.teslacoilsw.launcher";
    protected static final String NovaLauncher = NOVA_PKG + ".NovaLauncher";
    protected static final String TouchHomeLauncher = "jp.co.benesse.touch.allgrade.b003.touchhomelauncher";
    protected static final String HomeLauncherActivity = TouchHomeLauncher + ".HomeLauncherActivity";

    protected static IDchaService mDchaService = null;
    protected static IDchaUtilService mUtilService = null;

    protected static Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPermission(this)) return;

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
                    mDchaService.setSetupStatus(3);
                    mDchaService.hideNavigationBar(true);
                    mDchaService.clearDefaultPreferredApp(NOVA_PKG);
                    mDchaService.setDefaultPreferredHomeApp(TouchHomeLauncher);
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

        makeText(this, R.string.start_dcha);

        try {
            startActivity(new Intent(Intent.ACTION_MAIN).setClassName(TouchHomeLauncher, HomeLauncherActivity));
        } catch (ActivityNotFoundException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            makeText(this, R.string.fail_dcha);
        }
        finish();
    }

    protected static boolean checkPermission(Context context) {
        if (context instanceof Activity activity) {
            //activity.startService(new Intent(activity, AccessibilityService.class));
            try {
                if (!(Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)).contains(activity.getPackageName())) {
                    makeText(activity, R.string.enable_service);
                    activity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    activity.finish();
                    return true;
                }
            } catch (NullPointerException ignored) {
                makeText(activity, R.string.enable_service);
                activity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                activity.finish();
                return true;
            }
            if (activity.checkSelfPermission(ACCESS_SYSTEM) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, R.string.grant_permission, Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName())));
                activity.requestPermissions(new String[]{ACCESS_SYSTEM}, 0);
                activity.finish();
                return true;
            }
            return false;
        }
        return true;
    }

    protected static void makeText(Context context, int resId) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT);
        toast.show();
    }
}