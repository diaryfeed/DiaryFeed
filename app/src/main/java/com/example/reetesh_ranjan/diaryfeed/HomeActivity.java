package com.example.reetesh_ranjan.diaryfeed;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private TextView seeprofile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview=navigationView.getHeaderView(0);
        seeprofile =(TextView)headerview.findViewById(R.id.tv_seeprofile);

        seeprofile.setOnClickListener(new View.OnClickListener() {
            @Override    public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,SeeProfile.class);
                startActivity(intent);
                }
        });

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth=FirebaseAuth.getInstance();

        navigationView=(NavigationView)findViewById(R.id.nav_view);
        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


         if(savedInstanceState==null) {
             getSupportFragmentManager().beginTransaction().
                     replace(R.id.fragment_container, new HomeFragment())
                     .commit();
             navigationView.setCheckedItem(R.id.home_fragment);
         }


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user==null){
            sendToLogin();
        }
    }

    private void sendToLogin() {
        Intent sendToLogin=new Intent(HomeActivity.this,LoginActivity.class);
        startActivity(sendToLogin);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment selectedFragment=null;
        switch (menuItem.getItemId()) {
            case R.id.create_post:
                Intent intent=new Intent(getApplicationContext(),CreatePost.class);
                startActivity(intent);
                break;
            case R.id.home_fragment:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container,new HomeFragment())
                        .commit();
                break;

            case R.id.logout:
                firebaseAuth.signOut();
                sendToLogin();
                break;


            default:
                //Toast.makeText(MainActivity.this,"Default Page",Toast.LENGTH_LONG).show();
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }
}
