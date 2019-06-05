package com.example.reetesh_ranjan.diaryfeed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

public class SignUp extends AppCompatActivity {
      Button btn_register;
      TextView tv_login;
      ImageView iV_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tv_login=(TextView)findViewById(R.id.goLogin_tV);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUp.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_register=(Button)findViewById(R.id.register_btn);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUp.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        iV_back=(ImageView)findViewById(R.id.backRegister_iV);
        iV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUp.this,LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
}
