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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.example.reetesh_ranjan.diaryfeed.Model.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private CallbackManager callbackManager;
    private GoogleSignInButton signInButton;
    private Button loginButton;
    private TextView tv_register;
    private Button Btn_login;
    private ProgressBar progressBar;

    private EditText user_name;
    private EditText user_password;

    private FirebaseAuth mAuth;
    private FirebaseFirestore uStore;
    private FirebaseUser user;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        user_name=(EditText)findViewById(R.id.userId_eT);
        user_password=(EditText)findViewById(R.id.userPass_eT);
        tv_register=(TextView)findViewById(R.id.goRegister_tV);
        Btn_login=(Button)findViewById(R.id.login_btn);//Login Page Button
        progressBar=(ProgressBar)findViewById(R.id.progressbar);
        Circle wave=new Circle();
        progressBar.setIndeterminateDrawable(wave);

        signInButton = findViewById(R.id.sign_in_button);//Google SignIn Button
        loginButton = (Button)findViewById(R.id.login_button);//Facebook Button
        // If you are using in a fragment, call loginButton.setFragment(this);

        mAuth=FirebaseAuth.getInstance();
        uStore=FirebaseFirestore.getInstance();

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignUp.class);
                startActivity(intent);
            }
        });

        Btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
                //Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                //startActivity(intent);
            }
        });
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("TAG", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("TAG", "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("TAG", "facebook:onError", error);
                        // ...
                    }
                });
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient=GoogleSignIn.getClient(this,gso);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                //Toast.makeText(LoginActivity.this,"This is google sign in",Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d("TAG", "handleFacebookAccessToken:" + accessToken);
        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed."+task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //progressBar.setVisibility(View.VISIBLE);
        user=FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            sendToHome();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account!=null){
                    firebaseAuthWithGoogle(account);
                }

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                progressBar.setVisibility(View.GONE);
                Log.w("TAG", "Google sign in failed", e);
                Toast.makeText(LoginActivity.this,"Google sign in failed "+e,Toast.LENGTH_SHORT).show();
                // ...
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,"Authentication Failed.",Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user!=null){
            String name=user.getDisplayName();
            String email=user.getEmail();
            String photoUrl=String.valueOf(user.getPhotoUrl());
            String mobile=user.getPhoneNumber();
            String uid=user.getUid();
            Log.d("Tag","Name"+name);
            Log.d("Tag","Email:"+email);
            Log.d("Tag","photo:"+photoUrl);
            Log.d("Tag","Mobile:"+mobile);

            UserInfo userInfo=new UserInfo(uid,name,email,mobile,photoUrl);
            uStore.collection("Users").document(uid).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful()){
                       sendToHome();
                   }
                   else {
                       progressBar.setVisibility(View.GONE);
                       Toast.makeText(LoginActivity.this,"Data Storing Failed"+""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                   }
                }
            }) ;

        }
        else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this,"Authentication Failed.",Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void userLogin() {
        String username=user_name.getText().toString();
        String password=user_password.getText().toString();

        if(username.isEmpty()){
            user_name.setError("Field can't be empty");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            user_name.setError("Plz enter valid email...");
            //Toast.makeText(getApplicationContext(),"Plz Enter Valid Email...",Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()){
            user_password.setError("Field can't be empty");
            //Toast.makeText(getApplicationContext(),"Password Required...",Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length()<8){
            user_password.setError("Password too short");
            return;
        }

        Toast.makeText(this,"okk",Toast.LENGTH_LONG).show();

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    sendToHome();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void sendToHome() {
        progressBar.setVisibility(View.GONE);
        Intent home=new Intent(LoginActivity.this,HomeActivity.class);
        startActivity(home);
        finish();
    }
}
