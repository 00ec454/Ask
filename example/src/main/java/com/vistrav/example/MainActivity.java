package com.vistrav.example;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Ask.on(this)
                .debug(true)
                .id(123)
                .forPermissions(Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withRationales("Location permission need for map to work properly",
                        "In order to save file you will need to grant storage permission") //optional
                .go();
    }

    //optional
    @AskGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void fileAccessGranted() {
        Log.i(TAG, "FILE  GRANTED");
    }

    //optional
    @AskDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void fileAccessDenied() {
        Log.i(TAG, "FILE  DENiED");
    }

    //optional
    @AskGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
    public void mapAccessGranted(int id) {
        Toast.makeText(getBaseContext(), "Permission for :: " + id, Toast.LENGTH_LONG).show();
        Log.i(TAG, "MAP GRANTED ===>" + id);
    }

    //optional
    @AskDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
    public void mapAccessDenied(int id) {
        Log.i(TAG, "MAP DENIED" + id);
    }

    public void jumpToAnotherActivity(View view) {
        startActivity(new Intent(this, AnotherActivity.class));
    }
}
