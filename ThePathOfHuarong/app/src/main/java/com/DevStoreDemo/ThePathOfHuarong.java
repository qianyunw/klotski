package com.DevStoreDemo;

import android.app.Activity;
import android.os.Bundle;

public class ThePathOfHuarong extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ChessboardView(this));
    }
}