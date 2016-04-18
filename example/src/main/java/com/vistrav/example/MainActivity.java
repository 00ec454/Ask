package com.vistrav.example;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vistrav.ask.Ask;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Ask.on(this)
                .forPermissions(Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withRationales("Please Grant location access", "Application needs this to write files")
                .when(new Ask.Permission() {
                    @Override
                    public void granted(List<String> permissions) {
                        Log.i(TAG, "granted :: " + permissions);
                    }

                    @Override
                    public void denied(List<String> permissions) {
                        Log.i(TAG, "denied :: " + permissions);
                    }
                }).go();
    }
}
