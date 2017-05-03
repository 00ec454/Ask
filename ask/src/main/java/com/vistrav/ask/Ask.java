package com.vistrav.ask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;
import com.vistrav.ask.annotations.AskGrantedAll;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("unused")
public class Ask {

    private String[] permissions;
    private String[] rationalMessages;
    private static final String TAG = Ask.class.getSimpleName();
    private static final String ALL_PERMISSIONS = "All";
    private static WeakReference<Permission> permissionObjRef;
    private static WeakReference<Fragment> fragmentRef;
    private static WeakReference<Activity> activityRef;
    private static WeakReference<Map<String, Method>> permissionMethodMapRef;
    private static int id;
    private static boolean debug = false;


    private Ask() {
        permissionMethodMapRef = new WeakReference<Map<String, Method>>(new HashMap<String, Method>());
        debug = false;
        permissionObjRef = null;
        Random rand = new Random();
        id = rand.nextInt();
    }

    public static Ask on(Activity lActivity) {
        if (lActivity == null) {
            throw new IllegalArgumentException("Null Fragment Reference");
        }
        activityRef = new WeakReference<>(lActivity);
        return new Ask();
    }

    public static Ask on(Fragment lFragment) {
        if (lFragment == null) {
            throw new IllegalArgumentException("Null Fragment Reference");
        }
        fragmentRef = new WeakReference<>(lFragment);
        return new Ask();
    }

    public Ask forPermissions(@NonNull @Size(min = 1) String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("The permissions to request are missing");
        }
        this.permissions = permissions;
        return this;
    }

    public Ask withRationales(@IntegerRes int... rationalMessages) {
        if (rationalMessages.length == 0) {
            throw new IllegalArgumentException("The Rationale Messages are missing");
        }
        String msges[] = new String[rationalMessages.length];
        for (int i = 0; i < rationalMessages.length; i++) {
            msges[i] = getActivity().getString(rationalMessages[i]);
        }
        this.rationalMessages = msges;
        return this;
    }


    public Ask debug(boolean lDebug) {
        debug = lDebug;
        return this;
    }

    public Ask id(int lId) {
        id = lId;
        return this;
    }

    private Activity getActivity() {
        return fragmentRef != null ? fragmentRef.get().getActivity() : activityRef.get();
    }

    public void go() {
        if (debug) {
            Log.d(TAG, "request id :: " + id);
        }
        getAnnotatedMethod();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (permissionObjRef != null && permissionObjRef.get() != null) {
                permissionObjRef.get().granted(Arrays.asList(permissions));
                permissionObjRef.get().denied(new ArrayList<String>());
            }
            for (String permission : permissions) {
                invokeMethod(permission, true);
            }
            invokeMethod("All", true);
        } else {
            Intent intent = new Intent(getActivity(), AskActivity.class);
            intent.putExtra(Constants.PERMISSIONS, permissions);
            intent.putExtra(Constants.RATIONAL_MESSAGES, rationalMessages);
            intent.putExtra(Constants.REQUEST_ID, id);
            getActivity().startActivity(intent);
        }
    }

    public Ask when(@Nullable Permission permission) {
        permissionObjRef = new WeakReference<>(permission);
        return this;
    }

    interface Permission {
        void granted(List<String> permissions);

        void denied(List<String> permissions);

        void grantedAll();
    }

    public static class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context lContext, Intent intent) {
            boolean grantedAll = true;
            int requestId = intent.getIntExtra(Constants.REQUEST_ID, 0);
            if (debug) {
                Log.d(TAG, "request id :: " + id + ",  received request id :: " + requestId);
            }

            if (id != requestId) {
                return;
            }
            String[] permissions = intent.getStringArrayExtra(Constants.PERMISSIONS);
            int[] grantResults = intent.getIntArrayExtra(Constants.GRANT_RESULTS);
            List<String> grantedPermissions = new ArrayList<>();
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                boolean isGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                invokeMethod(permissions[i], isGranted);
                if (isGranted) {
                    grantedPermissions.add(permissions[i]);
                } else {
                    deniedPermissions.add(permissions[i]);
                    grantedAll = false;
                }
            }
            //if all permissions are granted
            if (grantedAll) {
                invokeMethod(ALL_PERMISSIONS, true);
            }
            if (permissionObjRef != null && permissionObjRef.get() != null) {
                permissionObjRef.get().denied(deniedPermissions);
                permissionObjRef.get().granted(grantedPermissions);
                if (deniedPermissions.size() == 0)
                    permissionObjRef.get().grantedAll();
            }
        }
    }

    private static void getAnnotatedMethod() {

        permissionMethodMapRef.get().clear();
        Method[] methods = fragmentRef != null ? fragmentRef.get().getClass().getMethods() : activityRef.get().getClass().getMethods();
        for (Method method : methods) {
            AskDenied askDenied = method.getAnnotation(AskDenied.class);
            AskGranted askGranted = method.getAnnotation(AskGranted.class);
            AskGrantedAll askGrantedAll = method.getAnnotation(AskGrantedAll.class);
            if (askDenied != null) {
                int lId = askDenied.id() != -1 ? askDenied.id() : id;
                permissionMethodMapRef.get().put(false + "_" + askDenied.value() + "_" + lId, method);
            }
            if (askGranted != null) {
                int lId = askGranted.id() != -1 ? askGranted.id() : id;
                permissionMethodMapRef.get().put(true + "_" + askGranted.value() + "_" + lId, method);
            }
            if (askGrantedAll != null) {
                int lId = askGrantedAll.id() != -1 ? askGrantedAll.id() : id;
                permissionMethodMapRef.get().put(true + "_" + askGrantedAll.value() + "_" + lId, method);
            }
        }
        if (debug) {
            Log.d(TAG, "annotated methods map :: " + permissionMethodMapRef.get());
        }
    }

    private static void invokeMethod(String permission, boolean isGranted) {
        String key = isGranted + "_" + permission + "_" + id;
        String val = isGranted ? "Granted" : "Denied";
        try {
            if (debug) {
                Log.d(TAG, "invoke method for key :: " + key);
            }
            if (permissionMethodMapRef.get().containsKey(key)) {
                permissionMethodMapRef.get().get(key).invoke(fragmentRef != null ? fragmentRef.get() : activityRef.get(), id);
            } else if (debug) {
                Log.w(TAG, "No method found to handle the " + permission + " " + val + " case. Please check for the detail here https://github.com/00ec454/Ask");
            }
        } catch (Exception e) {
            if (debug)
                Log.e(TAG, e.getMessage(), e);
        } finally {
            clear(fragmentRef, activityRef, permissionMethodMapRef, permissionObjRef);
        }
    }

    private static void clear(WeakReference<? extends Object>... refs) {
        for (WeakReference<? extends Object> ref : refs) {
            if (ref != null) {
                ref.clear();
            }
        }
    }
}
