package com.example.asus.chatbloom;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar mToolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TabsAccesorAdapter myTabsAccessorAdapter;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private DatabaseReference RootRef;
    private View navigation;
    private TextView nav_user_name, nav_user_status;
    private String currentUserId;
    private CircleImageView nav_profile_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("iChat");

        myViewPager = findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccesorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            SendUserToLoginActivity();
        }
        else{
            CheckUserExistence();
        }
    }

    private void CheckUserExistence() {
        RootRef = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
        currentUserId = mAuth.getCurrentUser().getUid();

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navigationId);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        navigation = navigationView.getHeaderView(0);

        nav_user_name = navigation.findViewById(R.id.nav_user_name);
        nav_user_status = navigation.findViewById(R.id.nav_user_status);
        nav_profile_image = navigation.findViewById(R.id.nav_profile_image);

        RetrieveUserInfo();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void RetrieveUserInfo() {
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))){
                    String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();

                    nav_user_name.setText(retrieveUserName);
                    nav_user_status.setText(retrieveStatus);
                    Glide.with(MainActivity.this).load(retrieveProfileImage).placeholder(R.drawable.profile_photo).into(nav_profile_image);
                }
                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                    String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();

                    nav_user_name.setText(retrieveUserName);
                    nav_user_status.setText(retrieveStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.getMenu().getItem(0).setChecked(true);

        if(currentUserId == null){
            Intent loginIntent = new Intent(this,LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        } else{
            VerifyUserExistance();
        }
    }

    private void VerifyUserExistance() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists())){
                    Toast.makeText(getApplicationContext(),"Welcome",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent settingsIntent = new Intent(getApplicationContext(),SettingsActivity.class);
                    startActivity(settingsIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder alertDialogBuilder;
            alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

            alertDialogBuilder.setTitle("Alert Title");
            alertDialogBuilder.setMessage("Do you want to exit?");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this,"You have clicked on cancel button",Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.main_home_option){

        }
        if(menuItem.getItemId() == R.id.main_find_friends_option){
            Intent findFriendsIntent = new Intent(this,FindFriendsActivity.class);
            startActivity(findFriendsIntent);
            finish();
        }

        if(menuItem.getItemId() == R.id.main_settings_option){
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }
        if(menuItem.getItemId() == R.id.main_feedback_option){
            Intent intent = new Intent(this,FeedbackActivity.class);
            startActivity(intent);
        }


        if(menuItem.getItemId() == R.id.main_logout_option){
            mAuth.signOut();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
        if(menuItem.getItemId() == R.id.main_share_option){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

            String subject = "iChat";
            String text = "This is a messaging app";

            intent.putExtra(Intent.EXTRA_SUBJECT,subject);
            intent.putExtra(Intent.EXTRA_TEXT,text);

            startActivity(Intent.createChooser(intent,"Share with"));
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
        //return false;
    }
}
