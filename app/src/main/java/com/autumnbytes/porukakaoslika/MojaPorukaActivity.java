package com.autumnbytes.porukakaoslika;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MojaPorukaActivity extends AppCompatActivity {

    TextView cvNaslov, uvodMojaPoruka;
    EditText cvPoruka;
    FloatingActionButton fab;
    TimePickerMojaPoruka tpf;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moja_poruka);

        initWidgets();
        setupCustomFont();
        setupFabListener();

        firebaseAuth = FirebaseAuth.getInstance();
        // Active listen to user logged in or not
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("AUTH OPERATIONS", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("AUTH OPERATIONS", "onAuthStateChanged:signed_out");
                }
            }
        };
        signInAnonymously();
    }

    private void initWidgets() {
        uvodMojaPoruka = (TextView) findViewById(R.id.tv_uvod_moja_poruka);
        cvNaslov = (TextView) findViewById(R.id.cv_naslov);
        cvPoruka = (EditText) findViewById(R.id.cv_poruka);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void setupCustomFont() {
        Typeface myCustomFontPoiret = Typeface.createFromAsset(getAssets(), "fonts/PoiretOne-Regular.ttf");
        Typeface myCustomFontLobster = Typeface.createFromAsset(getAssets(), "fonts/Lobster-Regular.ttf");
        cvNaslov.setTypeface(myCustomFontLobster);
        uvodMojaPoruka.setTypeface(myCustomFontPoiret);
    }

    // Add Auth state listener in onStart
    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    // Release listener in onStop
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }

    private void signInAnonymously() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("AUTH OPERATIONS", "Completed : " +task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.d("AUTH OPERATIONS", "Failed : ", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = new Intent(getApplicationContext(), CustomPreferenceActivity.class);
        startActivity(intent);

        //noinspection SimplifiableIfStatement
        if (id == R.id.appbar_menu_postavke) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void setupFabListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cvPoruka.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Napiši svoju poruku", Toast.LENGTH_LONG).show();
                } else {
                    tpf = new TimePickerMojaPoruka();
                    tpf.show(getFragmentManager(), "Time picker");
                }
            }
        });
    }
}