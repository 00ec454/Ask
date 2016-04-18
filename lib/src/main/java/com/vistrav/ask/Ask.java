package com.vistrav.ask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.ArrayList;
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

    private Ask(Context context) {
        this.context = context;
    }

    public static Ask on(Context context) {
        return new Ask(context);
    }

    public Ask forPermissions(@NonNull @Size(min=1) String... permissions) {
        if (permissions.length == 0) {
            throw new IllegalArgumentException("The permissions missing");
        }
        this.permissions = permissions;
        return this;
    }

    public Ask withRationales(@NonNull String... rationalMessages) {
        this.rationalMessages = rationalMessages;
        return this;
    }

    public void go() {
        Intent intent = new Intent(context, AskActivity.class);
        intent.putExtra(Constants.PERMISSIONS, permissions);
        intent.putExtra(Constants.RATIONAL_MESSAGES, rationalMessages);
        context.startActivity(intent);
    }

    public Ask when(@NonNull Permission permission) {
        permissionObj = permission;
        return this;
    }

    public interface Permission {
        void granted(List<String> permissions);

        void denied(List<String> permissions);
    }

    public static class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String[] permissions = intent.getStringArrayExtra(Constants.PERMISSIONS);
            int[] grantResults = intent.getIntArrayExtra(Constants.GRANT_RESULTS);
            Map<String, Boolean> permissionGrantResults = new HashMap<>();
            List<String> grantedPermissions = new ArrayList<>();
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    grantedPermissions.add(permissions[i]);
                } else {
                    deniedPermissions.add(permissions[i]);
                }
            }
            permissionObj.denied(deniedPermissions);
            permissionObj.granted(grantedPermissions);
        }
    }
}
