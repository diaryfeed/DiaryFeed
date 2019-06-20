package com.example.reetesh_ranjan.diaryfeed;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.reetesh_ranjan.diaryfeed.Fragments.BookmarksFragment;
import com.example.reetesh_ranjan.diaryfeed.Fragments.HomeFragment;
import com.example.reetesh_ranjan.diaryfeed.Fragments.PostFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String userId;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private TextView seeprofile;
    private HomeFragment homeFragment;
    private PostFragment postFragment;
    private BookmarksFragment bookMarksFragment;

    private TextView userName;
    private TextView userEmail;
    private CircleImageView userImage;

    private String name;
    private String email;
    private String phone;
    private String image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        userId=firebaseAuth.getCurrentUser().getUid().toString();

        navigationView=(NavigationView)findViewById(R.id.nav_view);
        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView=navigationView.getHeaderView(0);
        userName=(TextView)headerView.findViewById(R.id.tv_user_name);
        userEmail=(TextView)headerView.findViewById(R.id.tv_user_email);
        userImage=(CircleImageView) headerView.findViewById(R.id.user_profile_image);
        seeprofile =(TextView)headerView.findViewById(R.id.tv_seeprofile);
        fab=(FloatingActionButton)findViewById(R.id.fabBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToCreatePostActivity();
            }
        });


        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        name=task.getResult().getString("name");
                        Log.d("Tag","name of user:"+name);
                        email=task.getResult().getString("email");
                        phone=task.getResult().getString("mobile_no");
                        image=task.getResult().getString("profilePicUrl");

                        setUserProfile();
                    }
                    else {
                        Toast.makeText(HomeActivity.this,"Data doesn't exits ",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(HomeActivity.this,"Data Retrieve Failed: "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        seeprofile.setOnClickListener(new View.OnClickListener() {
            @Override    public void onClick(View v) {

                startFullProfile();
            }
        });

        homeFragment=new HomeFragment();
        postFragment=new PostFragment();
        bookMarksFragment=new BookmarksFragment();

        replaceFragment(homeFragment);


    }

    private void startFullProfile() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        Intent intent = new Intent(HomeActivity.this,SeeProfileActivity.class);
        /*intent.putExtra("uid",userId);
        intent.putExtra("name",name);
        intent.putExtra("email",email);
        intent.putExtra("phone",phone);
        intent.putExtra("userProfileImage",image);*/
        startActivity(intent);
        finish();
    }

    private void setUserProfile() {
        String newString = null;
        String originalPieceOfUrl = "s96-c/photo.jpg";
        String newPieceOfUrlToAdd = "s400-c/photo.jpg";
        if (image!=null){
            newString = image.replace(originalPieceOfUrl, newPieceOfUrlToAdd);
        }


        RequestOptions placeHolder=new RequestOptions();
        placeHolder.placeholder(R.drawable.profile_placeholder);
        Glide.with(this).applyDefaultRequestOptions(placeHolder).load(newString).into(userImage);
        userName.setText(name);
        userEmail.setText(email);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
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
        switch (menuItem.getItemId()) {
            case R.id.create_post:
                sendToCreatePostActivity();
                break;
            case R.id.home:
                replaceFragment(homeFragment);
                break;

            case R.id.view_post:
                replaceFragment(postFragment);
                break;

            case R.id.bookmark:
                replaceFragment(bookMarksFragment);
                break;
            case R.id.logout:
                firebaseAuth.signOut();
                sendToLogin();
                break;


            default:
                return false;
            //Toast.makeText(MainActivity.this,"Default Page",Toast.LENGTH_LONG).show();
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendToCreatePostActivity() {
        Intent intent=new Intent(getApplicationContext(),CreatePost.class);
        startActivity(intent);
        finish();
    }
}