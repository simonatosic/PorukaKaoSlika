package com.autumnbytes.porukakaoslika;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class NaslovnicaActivity extends AppCompatActivity {

    TextView tvNaslov, tvLinkMojaPoruka, tvLinkPorukaIznenadjenja, tvLinkPrimljenePoruke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naslovnica);
        initWidgets();
        setupCustomFont();
        setStatusBarTranslucent(true);
        setupListeners();
    }

    private void initWidgets(){
        tvNaslov = (TextView) findViewById(R.id.tv_naslov);
        tvLinkMojaPoruka = (TextView) findViewById(R.id.tv_link_moja_poruka);
        tvLinkPorukaIznenadjenja = (TextView) findViewById(R.id.tv_link_poruka_iznenadjenja);
        tvLinkPrimljenePoruke = (TextView) findViewById(R.id.tv_link_primljene_poruke);
    }

    private void setupCustomFont(){
        Typeface myCustomFontLobster = Typeface.createFromAsset(getAssets(), "fonts/Lobster-Regular.ttf");
        tvNaslov.setTypeface(myCustomFontLobster);

        Typeface myCustomFontPoiret = Typeface.createFromAsset(getAssets(), "fonts/PoiretOne-Regular.ttf");
        tvLinkMojaPoruka.setTypeface(myCustomFontPoiret);
        tvLinkPorukaIznenadjenja.setTypeface(myCustomFontPoiret);
        tvLinkPrimljenePoruke.setTypeface(myCustomFontPoiret);
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void setupListeners(){
        tvLinkMojaPoruka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MojaPorukaActivity.class);
                startActivity(intent);
            }
        });

        tvLinkPorukaIznenadjenja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PorukaIznenadjenjaActivity.class);
                startActivity(intent);
            }
        });

        tvLinkPrimljenePoruke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PrimljenePorukeActivity.class);
                startActivity(intent);
            }
        });
    }
}