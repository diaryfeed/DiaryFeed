package com.example.reetesh_ranjan.diaryfeed;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.example.reetesh_ranjan.diaryfeed.Model.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditProfileActivity extends AppCompatActivity {
   /* private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String currentUserId;
    private Uri uriProfileImage;
    private Bitmap compressedImageFile;

    private ImageView iv_back;
    private ImageView iv_go;

    private CircleImageView img_profile;
    private TextView changeProfile;

    private ProgressBar setupProgress;
    private EditText et_user_name;
    private EditText et_email;
    private EditText et_phone;
    private EditText et_gender;

    private String name;
    private String email;
    private String phone;
    private String gender;
    private String image;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

       /* Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null){
            name = bundle.getString("name");
            email = bundle.getString("email");
            phone = bundle.getString("phone");
            image  = bundle.getString("userProfileImage");
        }

        storageReference=FirebaseStorage.getInstance().getReference("ProfilePics");
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        currentUserId=firebaseAuth.getCurrentUser().getUid();

        setupProgress=(ProgressBar)findViewById(R.id.setup_progress);
        img_profile =(CircleImageView) findViewById(R.id.iv_change_profile_img);
        RequestOptions options=new RequestOptions();
        options.placeholder(R.drawable.profile_placeholder);
        Glide.with(this).applyDefaultRequestOptions(options).load(image).into(img_profile);

        changeProfile=(TextView) findViewById(R.id.tv_change_profile_img);
        iv_go=(ImageView)findViewById(R.id.update_profile);
        iv_back=(ImageView)findViewById(R.id.back_see_profile);

        et_user_name=(EditText) findViewById(R.id.et_username);
        et_user_name.setText(name);
        et_email=(EditText) findViewById(R.id.et_email);
        et_email.setText(email);
        et_phone=(EditText) findViewById(R.id.et_phone);
        et_phone.setText(phone);
        et_gender=(EditText) findViewById(R.id.gender);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendToHomeActivity();
            }
        });

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        iv_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfileImage();
            }
        });
    }

    private void uploadProfileImage() {
        name=et_user_name.getText().toString();
        email=et_email.getText().toString();
        phone=et_phone.getText().toString();
        gender=et_gender.getText().toString();
        Log.d("Tag","Gender:"+gender);

        if (!TextUtils.isEmpty(gender) && uriProfileImage!=null){
            setupProgress.setVisibility(View.VISIBLE);
            File newImageFile = new File(uriProfileImage.getPath());
            try {

                compressedImageFile = new Compressor(EditProfileActivity.this)
                        .setMaxHeight(125)
                        .setMaxWidth(125)
                        .setQuality(50)
                        .compressToBitmap(newImageFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] thumbData = baos.toByteArray();

            storageReference = FirebaseStorage.getInstance().getReference("ProfilePics/").child(currentUserId + "."+getFileExtension(uriProfileImage));
            storageReference.putBytes(thumbData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("Tag","uri:"+task.getResult());
                        String s=task.getResult().getUploadSessionUri().toString();
                        Log.d("Tag","uri1:"+s);
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String download_url=uri.toString();
                                updateProfile(download_url);
                                //uploadThumbnail(download_url,randomName);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                setupProgress.setVisibility(View.GONE);
                                Toast.makeText(EditProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        setupProgress.setVisibility(View.GONE);
                        Toast.makeText(EditProfileActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
        else {
                setupProgress.setVisibility(View.GONE);
                Toast.makeText(this,"Gender field can't be empty",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile(String download_url) {

        UserInfo userInfo=new UserInfo(currentUserId,name,email,phone,download_url,gender);
        firestore.collection("Users").document(currentUserId).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    sendToHomeActivity();
                }
                else {
                    setupProgress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendToHomeActivity() {
        setupProgress.setVisibility(View.GONE);
        Intent mainIntent = new Intent(EditProfileActivity.this, HomeActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void showImageChooser() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(EditProfileActivity.this);

    }
    @Override
    public void onBackPressed() {
        sendToHomeActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                uriProfileImage = result.getUri();
                img_profile.setImageURI(uriProfileImage);

                //isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this,"Error: " + error,Toast.LENGTH_SHORT).show();

            }
        }*/
    }
}
