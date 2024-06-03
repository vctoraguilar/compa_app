package com.rasgo.compa_app.feature.homepage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.rasgo.compa_app.R;
import com.rasgo.compa_app.feature.homepage.chat.ChatFragment;
import com.rasgo.compa_app.feature.homepage.compas.CompasFragment;
import com.rasgo.compa_app.feature.homepage.home.HomeFragment;
import com.rasgo.compa_app.feature.homepage.notifications.NotificationFragment;
import com.rasgo.compa_app.feature.homepage.settings.SettingsFragment;
import com.rasgo.compa_app.feature.profile.ProfileActivity;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView userProfileImageView = findViewById(R.id.profile_button);

        userProfileImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class).putExtra("uid", FirebaseAuth.getInstance().getUid());
                startActivity(intent);
            }
        });

        bottomNavigationView = findViewById(R.id.navigation);
        setBottomNavigationView(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.homeFragment) {
                    setFragment(new HomeFragment());
                    return true;
                } else if (id == R.id.chatFragment) {
                    setFragment(new ChatFragment());
                    return true;
                }
                else if (id == R.id.notificationFragment) {
                    setFragment(new NotificationFragment());
                    return true;
                }
                else if (id == R.id.settingsFragment) {
                    setFragment(new SettingsFragment());
                    return true;
                }
                return false;
            }
        });

        // Load the default fragment
        setFragment(new HomeFragment());
    }

    private void setBottomNavigationView(OnItemSelectedListener listener) {
        bottomNavigationView.setOnItemSelectedListener(listener);
    }

//    private void setBottomNavigationView() {
//
//        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
//
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.homeFragment:
//                        setFragment(homeFragment);
//                        break;
//
//                    case R.id.chatFragment:
//                        setFragment(chatFragment);
//                        break;
//                }
//                return true;
//            }
//        });
//    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment).commit();
    }
}
