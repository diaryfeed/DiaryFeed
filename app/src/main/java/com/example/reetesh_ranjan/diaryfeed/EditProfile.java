package com.example.reetesh_ranjan.diaryfeed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class EditProfile extends AppCompatActivity {
    private ImageView iv_back;
    private  ImageView iv_go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        iv_back=(ImageView)findViewById(R.id.back_see_profile);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(EditProfile.this,SeeProfile.class);
                startActivity(intent);
            }
        });
       iv_go=(ImageView)findViewById(R.id.update_profile);
       iv_go.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(EditProfile.this,HomeActivity.class);
               startActivity(intent);
           }
       });
    }
}
