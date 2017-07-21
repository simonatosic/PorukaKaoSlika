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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PorukaIznenadjenjaActivity extends AppCompatActivity {

    TextView uvodPorukaIznenadjenja;
    FloatingActionButton fab2;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poruka_iznenadjenja);

        uvodPorukaIznenadjenja = (TextView) findViewById(R.id.tv_uvod_poruka_iznenadjenja);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        Typeface myCustomFontPoiret = Typeface.createFromAsset(getAssets(), "fonts/PoiretOne-Regular.ttf");
        uvodPorukaIznenadjenja.setTypeface(myCustomFontPoiret);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerPorukaIznenadjenja tpf = new TimePickerPorukaIznenadjenja();
                tpf.show (getFragmentManager(), "Time picker");
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("AUTH OPERATIONS", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d("AUTH OPERATIONS", "onAuthStateChanged:signed_out");
                }
            }
        };
        signInAnonymously();
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

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
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent (getApplicationContext(), CustomPreferenceActivity.class);
        startActivity(intent);

        if (id == R.id.appbar_menu_postavke) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
