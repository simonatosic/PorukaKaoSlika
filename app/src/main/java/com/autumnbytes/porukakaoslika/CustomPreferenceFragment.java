package com.autumnbytes.porukakaoslika;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Simona Tošić on 22-May-17.
 */

public class CustomPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}