package com.autumnbytes.porukakaoslika;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class PrimljenePorukeActivity extends AppCompatActivity {

    GalleryAdapter galleryAdapter;
    RecyclerView recyclerView;
    SQLiteDatabase sqLiteDatabase;
    UrlDatabaseHelper urlDatabaseHelper;
    Cursor cursor;
    Handler handler;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    ArrayList<PozadineFirebase> pozadine = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primljene_poruke);

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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                prepareData();
                handler.sendEmptyMessage(1);
            }
        });
        thread.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("DATABASE OPERATIONS", "Primljene poruke učitane");
            }
        };

        recyclerView = (RecyclerView) findViewById(R.id.image_gallery);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        galleryAdapter = new GalleryAdapter(getApplicationContext(), pozadine);
        recyclerView.setAdapter(galleryAdapter);

        try {
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                    new RecyclerItemClickListener.OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getApplicationContext(), GalleryFullscreenActivity.class);
                            intent.putParcelableArrayListExtra("pozadine", pozadine);
                            intent.putExtra("position", position);
                            startActivity(intent);
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private ArrayList prepareData(){
        urlDatabaseHelper = new UrlDatabaseHelper(getApplicationContext());
        sqLiteDatabase = urlDatabaseHelper.getReadableDatabase();
        cursor = urlDatabaseHelper.getUrl(sqLiteDatabase);
        if (cursor.moveToFirst()){
            do{
                String url = cursor.getString(0);
                PozadineFirebase pozadineFirebase = new PozadineFirebase();
                pozadineFirebase.setUrl(url);
                pozadine.add(pozadineFirebase);
            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        return pozadine;
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

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, NaslovnicaActivity.class);
        startActivity(intent);
    }
}
