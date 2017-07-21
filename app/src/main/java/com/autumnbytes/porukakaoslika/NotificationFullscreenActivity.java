package com.autumnbytes.porukakaoslika;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class NotificationFullscreenActivity extends AppCompatActivity {

    int id;
    String url, editTextPoruka;
    ImageView imagePreview;
    TextView notTextView;
    Button btnClose;
    ProgressBar progressBar;
    FrameLayout imageContainer;
    Bitmap firebaseBitmap;
    Canvas canvas;
    FirebaseStorage storage;
    StorageReference editTextPorukeRef;
    ArrayList<String> urls;
    UrlDatabaseHelper urlDatabaseHelper;
    SQLiteDatabase sqLiteDatabase;
    Handler handler;
    byte [] data;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_fullscreen);
        setStatusBarTranslucent(true);

        imagePreview = (ImageView) findViewById(R.id.notification_image_preview);
        notTextView = (TextView) findViewById(R.id.notification_text_view);
        btnClose = (Button) findViewById(R.id.btn_close);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageContainer = (FrameLayout) findViewById(R.id.image_container);
        urlDatabaseHelper = new UrlDatabaseHelper(getApplicationContext());
        sqLiteDatabase = urlDatabaseHelper.getWritableDatabase();

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

        if (getIntent().hasExtra("porukaIznenadjenja")) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("porukaiznenadjenja");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            collectUrlsPorukaIznenadjenja((Map<String, Object>) dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }

        if(getIntent().hasExtra("mojaPoruka")) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("mojaporuka");
            ref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            collectUrlsMojaPoruka((Map<String, Object>) dataSnapshot.getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                btnClose.setVisibility(View.VISIBLE);
            }
        };

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PrimljenePorukeActivity.class);
                startActivity(intent);
                finish();
            }
        });
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

    private void collectUrlsPorukaIznenadjenja (Map<String, Object> porukaIznenadjenja) {
        urls = new ArrayList<>();

        for (Map.Entry<String, Object> entry : porukaIznenadjenja.entrySet()) {
            Map singleData = (Map) entry.getValue();
            urls.add((String) singleData.get("url"));
        }

        Random r = new Random();
        id = r.nextInt(urls.size());
        url = urls.get(id);

        Glide.with(this)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        Toast.makeText(getApplicationContext(), R.string.nema_internet_veze, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        progressBar.setVisibility(View.GONE);
                        imagePreview.setImageBitmap(resource);

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                urlDatabaseHelper.addUrl(url, sqLiteDatabase);
                                urlDatabaseHelper.close();
                                handler.sendEmptyMessage(1);
                            }
                        });
                        thread.start();
                        Toast.makeText(NotificationFullscreenActivity.this, "Slika se sprema u primljene poruke", Toast.LENGTH_LONG).show();
                    }
                });
        urls.clear();
        imagePreview.setImageResource(android.R.color.transparent);
    }

    private void collectUrlsMojaPoruka (Map<String, Object> mojaporuka) {
        urls = new ArrayList<>();

        for (Map.Entry<String, Object> entry : mojaporuka.entrySet()) {
            Map singleData = (Map) entry.getValue();
            urls.add((String) singleData.get("url"));
        }

        Random r = new Random();
        id = r.nextInt(urls.size());
        String urlMp = urls.get(id);

        Glide.with(this)
                .load(urlMp)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        Toast.makeText(getApplicationContext(), R.string.nema_internet_veze, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        progressBar.setVisibility(View.GONE);
                        notTextView.setVisibility(View.VISIBLE);
                        pokaziPoruku();
                        imagePreview.setImageBitmap(resource);
                        Toast.makeText(NotificationFullscreenActivity.this, "Slika se sprema u primljene poruke", Toast.LENGTH_LONG).show();

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                firebaseBitmap = Bitmap.createBitmap(imageContainer.getWidth(), imageContainer.getHeight(), Bitmap.Config.ARGB_8888);
                                canvas = new Canvas(firebaseBitmap);
                                imageContainer.draw(canvas);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                firebaseBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                imageContainer.setDrawingCacheEnabled(false);
                                data = baos.toByteArray();

                                storage = FirebaseStorage.getInstance();
                                final String path = "edit_text_poruke/" + UUID.randomUUID() + ".png";
                                editTextPorukeRef = storage.getReference(path);

                                StorageMetadata metadata = new StorageMetadata.Builder()
                                        .setCustomMetadata("editTextPoruka", editTextPoruka)
                                        .build();

                                UploadTask uploadTask = editTextPorukeRef.putBytes(data, metadata);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        @SuppressWarnings("VisibleForTests") final String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                                        editTextPorukeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                urlDatabaseHelper.addUrl(downloadUrl, sqLiteDatabase);
                                                urlDatabaseHelper.close();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                            }
                                        });
                                    }
                                });
                                handler.sendEmptyMessage(1);
                            }
                        });
                        thread.start();
                    }
                });
        urls.clear();
        imagePreview.setImageResource(android.R.color.transparent);
    }

    private void pokaziPoruku() {
        editTextPoruka = getIntent().getStringExtra("mojaPoruka");
        notTextView.setMovementMethod(new ScrollingMovementMethod());
        notTextView.setText(editTextPoruka);
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PrimljenePorukeActivity.class);
        startActivity(intent);
        finish();
    }
}
