package com.heshmat.mydietwatcher.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.heshmat.mydietwatcher.DatabaseInstance;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.StaticFields;
import com.heshmat.mydietwatcher.fragments.AddDietFragment;
import com.heshmat.mydietwatcher.fragments.AllMyDietFragment;
import com.heshmat.mydietwatcher.fragments.ProfileFragment;
import com.heshmat.mydietwatcher.fragments.StatsticsFragment;
import com.heshmat.mydietwatcher.fragments.TodayPlanFragment;
import com.heshmat.mydietwatcher.intro.IntroActivity;
import com.heshmat.mydietwatcher.models.User;

import static com.heshmat.mydietwatcher.activities.LoginActivity.gso;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.nested)
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView drawerUserNameTv;
    ImageView userIv;
    Class fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (fragment != null) {
                    showFragment(fragment);
                    fragment=null;


                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        View headerView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.drawer_header, navigationView);
        drawerUserNameTv = headerView.findViewById(R.id.drawerUserNameTv);
        userIv = headerView.findViewById(R.id.userIv);
        initializeUserUiInfo();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, AllMyDietFragment.newInstance())
                    .commit();
        }
        DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(User.currentUser.getId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value!=null&&value.exists()){
                            User.currentUser=value.toObject(User.class);
                            if (User.currentUser!=null&&User.currentUser.getImgUrl()!=null){
                                Glide.with(HomeActivity.this).load(User.currentUser.getImgUrl()).placeholder(R.drawable.ic_logo_wh).error(R.drawable.ic_logo_wh).circleCrop().into(userIv);

                            }
                        }

                    }
                });
    }

    private void initializeUserUiInfo() {
        if (User.currentUser != null) {
            if (User.currentUser.getName() != null)
                drawerUserNameTv.setText(User.currentUser.getName());

            if (User.currentUser.getImgUrl() != null) {

                Glide.with(getApplicationContext()).load(User.currentUser.getImgUrl()).placeholder(R.drawable.ic_logo_wh).error(R.drawable.ic_logo_wh).circleCrop().into(userIv);
            }
        }
    }

    public void showFragment(Class fragmentClass) {
        Fragment fragment = null;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (!currentFragment.getClass().getName().equals(fragmentClass.getName())) {
            fragmentManager.beginTransaction()
                    //  .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.fragmentContainer, fragment, fragmentClass.getName()).addToBackStack(null)
                    .commit();

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                fragment= ProfileFragment.class;
                break;
            case R.id.nav_my_diet:
                fragment = AllMyDietFragment.class;
                break;
            case R.id.nav_today_plan:
                fragment= TodayPlanFragment.class;
                break;
            case R.id.nav_statistics:
                fragment= StatsticsFragment.class;
                break;
            case R.id.nav_tutorial:
                startActivity(new Intent(HomeActivity.this, IntroActivity.class));
                break;
            case R.id.nav_logout:
                LoginActivity.Disconnect_google(HomeActivity.this, HomeActivity.this);
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;

        }
        closeDrawer();

        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();

        }
    }

    private void closeDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

}
