package com.autumnbytes.porukakaoslika;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class CustomPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new CustomPreferenceFragment())
                .commit();
    }
}