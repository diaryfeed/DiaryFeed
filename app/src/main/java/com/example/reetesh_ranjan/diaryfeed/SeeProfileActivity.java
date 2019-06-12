package com.example.reetesh_ranjan.diaryfeed;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class SeeProfileActivity extends AppCompatActivity {
    private Button editProfile;
    private ImageView userImage;
    private TextView user_name;

    private String name;
    private String email;
    private String phone;
    private String image;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null){
            uid = bundle.getString("uid");
            name = bundle.getString("name");
            email = bundle.getString("email");
            phone = bundle.getString("phone");
            image  = bundle.getString("userProfileImage");
        }
        requestWindowFeature(1);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_see_profile);

        userImage=(ImageView)findViewById(R.id.user_img);
        RequestOptions placeHolder=new RequestOptions();
        placeHolder.placeholder(R.drawable.profilepic);
        Glide.with(this).applyDefaultRequestOptions(placeHolder).load(image).into(userImage);

        user_name=(TextView)findViewById(R.id.tv_user_name);
        user_name.setText(name);

        editProfile=(Button)findViewById(R.id.btn_edit_profile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeeProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                intent.putExtra("phone",phone);
                intent.putExtra("userProfileImage",image);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(SeeProfileActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
