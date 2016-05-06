package com.vistrav.ask;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.Log;

import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class Ask {

    private Context context;
    private String[] permissions;
    private String[] rationalMessages;
    private Map<String, Boolean> permissionGrantResults = new HashMap<>();
    private static final String TAG = Ask.class.getSimpleName();
    private static Permission permissionObj;
    private static Fragment fragment;
    private static Activity activity;
    private static int id = Constants.DEFAULT_ID;
    private static Map<String, Method> permissionMethodMap;
    private static boolean warn = true;

    private Ask(Context context) {
        this.context = context;
        permissionMethodMap = new HashMap<>();
    }

    public static Ask on(Activity lActivity) {
        activity = lActivity;
        return new Ask(lActivity);
    }

    public static Ask on(Fragment lFragment) {
        fragment = lFragment;
        return new Ask(lFragment.getActivity());
    }

    public Ask forPermissions(@NonNull @Size(min = 1) String... permissions) {
        if (permissions.length == 0) {
            throw new IllegalArgumentException("The permissions to request are missing");
        }
        this.permissions = permissions;
        return this;
    }

    public Ask withRationales(@NonNull String... rationalMessages) {
        this.rationalMessages = rationalMessages;
        return this;
    }

    public Ask warn(boolean lWarn) {
        warn = lWarn;
        return this;
    }

    public Ask id(int lId) {
        id = lId;
        return this;
    }

    public void go() {
        getAnnotatedMethod();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (permissionObj != null) {
                permissionObj.granted(Arrays.asList(permissions));
                permissionObj.denied(new ArrayList<String>());
            }
            for (String permission : permissions) {
                invokeMethod(permission, true);
            }
        } else {
            Intent intent = new Intent(context, AskActivity.class);
            intent.putExtra(Constants.PERMISSIONS, permissions);
            intent.putExtra(Constants.RATIONAL_MESSAGES, rationalMessages);
            context.startActivity(intent);
        }
    }

    public Ask when(@Nullable Permission permission) {
        permissionObj = permission;
        return this;
    }

    public interface Permission {
        void granted(List<String> permissions);

        void denied(List<String> permissions);
    }

    public static class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context lContext, Intent intent) {
            String[] permissions = intent.getStringArrayExtra(Constants.PERMISSIONS);
            int[] grantResults = intent.getIntArrayExtra(Constants.GRANT_RESULTS);
            Map<String, Boolean> permissionGrantResults = new HashMap<>();
            List<String> grantedPermissions = new ArrayList<>();
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                boolean isGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                invokeMethod(permissions[i], isGranted);
                if (isGranted) {
                    grantedPermissions.add(permissions[i]);
                } else {
                    deniedPermissions.add(permissions[i]);
                }
            }
            if (permissionObj != null) {
                permissionObj.denied(deniedPermissions);
                permissionObj.granted(grantedPermissions);
            }
        }
    }


    private void getAnnotatedMethod() {
        permissionMethodMap.clear();
        Method[] methods = fragment != null ? fragment.getClass().getMethods() : activity.getClass().getMethods();
        for (Method method : methods) {
            AskDenied askDenied = method.getAnnotation(AskDenied.class);
            AskGranted askGranted = method.getAnnotation(AskGranted.class);
            if (askDenied != null) {
                permissionMethodMap.put(false + "_" + askDenied.value() + "_" + askDenied.id(), method);
            }
            if (askGranted != null) {
                permissionMethodMap.put(true + "_" + askGranted.value() + "_" + askGranted.id(), method);
            }
        }
    }

    private static void invokeMethod(String permission, boolean isGranted) {
        String key = isGranted + "_" + permission + "_" + id;
        String val = isGranted ? "Granted" : "Denied";
        try {
            if (permissionMethodMap.containsKey(key)) {
                permissionMethodMap.get(key).invoke(fragment != null ? fragment : activity);
            } else if (warn) {
                Log.w(TAG, "No method found to handle the " + permission + " " + val + " case. Please check for the detail here https://github.com/00ec454/Ask");
            }
        } catch (Exception e) {
            if (warn)
                Log.e(TAG, e.getMessage(), e);
        }
    }
}
