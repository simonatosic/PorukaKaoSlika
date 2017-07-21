package com.autumnbytes.porukakaoslika;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class GalleryFullscreenActivity extends AppCompatActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    int position;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    ArrayList<PozadineFirebase> pozadine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);
        setStatusBarTranslucent(true);

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

        pozadine = new ArrayList<>();
        pozadine = getIntent().getParcelableArrayListExtra("pozadine");
        position = getIntent().getIntExtra("position", 0);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), pozadine);
        viewPager = (ViewPager) findViewById(R.id.view_pager_container);
        viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
            }
        });

        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setStatusBarTranslucent(true);
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        ArrayList<PozadineFirebase> pozadine;
        public SectionsPagerAdapter(FragmentManager fm, ArrayList<PozadineFirebase> pozadine) {
            super(fm);
            this.pozadine = pozadine;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position, pozadine.get(position).getUrl());
        }

        @Override
        public int getCount() {
            if(pozadine == null) {
                return 0;
            }
            return pozadine.size();
        }
    }

    public static class PlaceholderFragment extends Fragment {
        String url;
        int position;
        ProgressBar progressBar2;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_IMG_URL = "image_url";

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            this.position = args.getInt(ARG_SECTION_NUMBER);
            this.url = args.getString(ARG_IMG_URL);
        }

        public static PlaceholderFragment newInstance(int sectionNumber, String url) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_IMG_URL, url);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.gallery_image_fullscreen, container, false);
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.gallery_image_fullscreen_preview);
            progressBar2 = (ProgressBar) rootView.findViewById(R.id.progressBar2);

            Glide.with(getActivity())
                    .load(url)
                    .dontTransform()
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar2.setVisibility(View.GONE);
                            Toast.makeText(getContext(), R.string.nema_internet_veze, Toast.LENGTH_LONG).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar2.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
            return rootView;
        }
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}