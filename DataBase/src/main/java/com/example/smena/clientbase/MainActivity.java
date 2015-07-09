package com.example.smena.clientbase;

import android.app.Activity;
import android.os.Bundle;

import com.example.smena.clientbase.procedures.Sessions;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Sessions sessions = new Sessions(MainActivity.this);
        sessions.getSessionsByTime("2015-06-07 15:15", "2015-06-07 15:55");

    }
}
