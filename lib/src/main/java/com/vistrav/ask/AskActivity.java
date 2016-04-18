package com.vistrav.ask;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.vistrav.pop.Pop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AskActivity extends AppCompatActivity {

    private Intent intent;
    private static final int PERMISSION_REQUEST = 100;
    @SuppressWarnings("unused")
    private static final String TAG = AskActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        intent = getIntent();
        getPermissions();
    }

    private void getPermissions() {
        String[] permissions = intent.getStringArrayExtra(Constants.PERMISSIONS);
        String[] rationalMessages = intent.getStringArrayExtra(Constants.RATIONAL_MESSAGES);

        Map<String, List<String>> map = separatePermissions(permissions, rationalMessages);
        List<String> neededPermissions = map.get(NEEDED_PERMISSIONS);
        final List<String> showRationaleFor = map.get(SHOW_RATIONAL_FOR);
        List<String> rationalMessagesToShow = map.get(RATIONALE_MESSAGES_TO_SHOW);

        if (showRationaleFor.size() > 0 && rationalMessagesToShow != null && rationalMessagesToShow.size() > 0) {
            Pop.on(this)
                    .cancelable(false)
                    .body(buildRationalMessage(rationalMessagesToShow))
                    .when(new Pop.Yah() {
                        @Override
                        public void clicked(DialogInterface dialog, @Nullable View view) {
                            ActivityCompat.requestPermissions(AskActivity.this, showRationaleFor.toArray(new String[showRationaleFor.size()]), PERMISSION_REQUEST);
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else if (neededPermissions.size() > 0) {
            ActivityCompat.requestPermissions(this, neededPermissions.toArray(new String[showRationaleFor.size()]), PERMISSION_REQUEST);
        } else {
            int[] result = new int[permissions.length];
            Arrays.fill(result, PackageManager.PERMISSION_GRANTED);
            broadcast(permissions, result);
            finish();
        }
    }

    private static final String NEEDED_PERMISSIONS = "needed_permissions";
    private static final String SHOW_RATIONAL_FOR = "show_rational_for";
    private static final String RATIONALE_MESSAGES_TO_SHOW = "rational_messages";

    private Map<String, List<String>> separatePermissions(String[] permissions, String[] rationalMessages) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> neededPermissions = new ArrayList<>();
        List<String> showRationalsFor = new ArrayList<>();
        List<String> neededRationalMessages = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showRationalsFor.add(permission);
                // if multiple rational message corresponding to each permission
                if (rationalMessages != null && rationalMessages.length == permissions.length) {
                    neededRationalMessages.add(rationalMessages[i]);
                }
            }
        }
        // if rational message is only one
        if (rationalMessages != null && rationalMessages.length == 1) {
            neededRationalMessages.add(rationalMessages[0]);
        }
        map.put(NEEDED_PERMISSIONS, neededPermissions);
        map.put(SHOW_RATIONAL_FOR, showRationalsFor);
        map.put(RATIONALE_MESSAGES_TO_SHOW, neededRationalMessages);
        return map;
    }

    @NonNull
    private String buildRationalMessage(@NonNull List<String> messages) {
        StringBuilder sb = new StringBuilder();
        for (String msg : messages) {
            sb.append("\u2022").append("\u0009").append(msg).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                broadcast(permissions, grantResults);
                finish();
            }
        }
    }

    private void broadcast(String[] permissions, int[] grantResults) {
        if (grantResults.length > 0) {
            Intent intent = new Intent();
            intent.setAction("com.vistrav.ask.PERMISSION_RESULT_INTENT");
            intent.putExtra(Constants.PERMISSIONS, permissions);
            intent.putExtra(Constants.GRANT_RESULTS, grantResults);
            sendBroadcast(intent);
        }
    }
}
