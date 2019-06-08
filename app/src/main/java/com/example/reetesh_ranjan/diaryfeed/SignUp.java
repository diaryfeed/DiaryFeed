package com.example.reetesh_ranjan.diaryfeed;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.reetesh_ranjan.diaryfeed.Model.UserInfo;

public class SignUp extends AppCompatActivity {
      private Button btn_register;
      private TextView tv_login;
      private ImageView iV_back;
      private ProgressBar progressBar;

      private EditText et_name;
      private EditText et_email;
      private EditText et_phone;
      private EditText et_password;

      private FirebaseAuth mAuth;
      private FirebaseFirestore db;

      private String name, email, phone, password, uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        tv_login=(TextView)findViewById(R.id.goLogin_tV);
        btn_register=(Button)findViewById(R.id.register_btn);
        iV_back=(ImageView)findViewById(R.id.backRegister_iV);
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        Circle wave=new Circle();
        progressBar.setIndeterminateDrawable(wave);


        et_name=(EditText)findViewById(R.id.name_register);
        et_email=(EditText)findViewById(R.id.email_register);
        et_phone=(EditText)findViewById(R.id.phone_register);
        et_password=(EditText)findViewById(R.id.password_register);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });

        iV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userRegistration();
            }
        });



    }

    private void userRegistration() {
        name=et_name.getText().toString();
        email=et_email.getText().toString();
        phone=et_phone.getText().toString();
        password=et_password.getText().toString();

        if(name.isEmpty()){
            et_name.setError("Field can't be empty");
            return;
        }
        if(email.isEmpty()){
            et_email.setError("Field can't be empty");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("Plz enter valid email...");
            //Toast.makeText(getApplicationContext(),"Plz Enter Valid Email...",Toast.LENGTH_SHORT).show();
            return;
        }
        if(phone.isEmpty()){
            et_phone.setError("Field can't be empty");
            return;
        }
        if(phone.length()!=10){
            et_phone.setError("Phone No. must be 10 digit");
            return;
        }
        if (password.isEmpty()){
            et_password.setError("Field can't be empty");
            //Toast.makeText(getApplicationContext(),"Password Required...",Toast.LENGTH_SHORT).show();
            return;
        }
        if(phone.isEmpty()){
            et_phone.setError("Field can't be empty");
            return;
        }

        if (password.length()<8){
            et_password.setError("Password too short");
            return;
        }
        //User Registration start
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    writeDataIntoDatabase();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void writeDataIntoDatabase() {
        uid=mAuth.getCurrentUser().getUid();
        Log.d("Tag","user id"+uid);

        UserInfo userInfo=new UserInfo(uid,name,email,phone,null);

        db.collection("Users").document(uid).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    sendToHomeActivity();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if (user!=null){
            sendToHomeActivity();
        }
    }

    private void sendToHomeActivity() {
        progressBar.setVisibility(View.GONE);
        Intent intent=new Intent(SignUp.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToLogin() {
        Intent intent=new Intent(SignUp.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
