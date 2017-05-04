package com.vistrav.example;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

@SuppressWarnings("unused")
public class AnotherActivity extends AppCompatActivity {

    private static final String TAG = AnotherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);
        Ask.on(this).debug(true)
                .forPermissions(Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE).id(22222)
                .withRationales("Location permission need for map to work properly",
                        "In order to save file you will need to grant storage permission") //optional
                .go();
    }

    //optional
    @AskGranted(value=Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void fileAccessGranted() {
        Log.i(TAG, "FILE  GRANTED");
    }

    //optional
    @AskDenied(value=Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void fileAccessDenied() {
        Log.i(TAG, "FILE  DENiED");
    }

    //optional
    @AskGranted(value=Manifest.permission.ACCESS_COARSE_LOCATION)
    public void mapAccessGranted(int id) {
        Log.i(TAG, "MAP GRANTED====>"+id);
    }

    //optional
    @AskDenied(value=Manifest.permission.ACCESS_COARSE_LOCATION)
    public void mapAccessDenied() {
        Log.i(TAG, "MAP DENIED");
    }
}
