package com.example.reetesh_ranjan.diaryfeed;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class CreatePost extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 100;
    private FirebaseFirestore postStore;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseUser user;

    private String user_id;
    private String title;
    private  String desc;

    private EditText post_title;
    private EditText post_desc;
    private Button post_btn;
    private ImageView post_image;
    private ProgressBar progressBar;
    private Toolbar newPostToolbar;

    private Uri uriPostImage=null;
    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        storageReference=FirebaseStorage.getInstance().getReference("post_images");
        postStore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        user_id=firebaseAuth.getCurrentUser().getUid();

        newPostToolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        newPostToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToHomeActivity();
            }
        });

        progressBar=(ProgressBar)findViewById(R.id.new_post_progress);
        post_title=(EditText)findViewById(R.id.eT_title);
        post_desc=(EditText)findViewById(R.id.eT_desc);
        post_btn=(Button)findViewById(R.id.post_btn);
        post_image=(ImageView)findViewById(R.id.new_post_image);

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 createPost();
            }
        });
    }

    private void createPost() {
        title=post_title.getText().toString();
        desc=post_desc.getText().toString();
        Log.d("TAG","title:"+title);
        Log.d("TAG","desc:"+desc);
        Log.d("TAG","uri:"+uriPostImage);
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && uriPostImage!=null){
            progressBar.setVisibility(View.VISIBLE);
            final String randomName=UUID.randomUUID().toString();
            File newImageFile = new File(uriPostImage.getPath());
            try {

                compressedImageFile = new Compressor(CreatePost.this)
                        .setMaxHeight(720)
                        .setMaxWidth(720)
                        .setQuality(50)
                        .compressToBitmap(newImageFile);
                Log.d("TAG","compress:"+compressedImageFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
             Log.d("TAG","compress:"+compressedImageFile);
             byte[] imageData = baos.toByteArray();

            //Upload Post Image
            storageReference = FirebaseStorage.getInstance().getReference("post_images/").child(randomName + "."+getFileExtension(uriPostImage));
            storageReference.putBytes(imageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                uploadThumbnail(download_url,randomName);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CreatePost.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CreatePost.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }
        else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(CreatePost.this,"Field can't be empty",Toast.LENGTH_SHORT).show();

        }
    }


    private void uploadThumbnail(final String downloadUrl, String randomName) {
        File newThumbFile = new File(uriPostImage.getPath());
        try {

            compressedImageFile = new Compressor(CreatePost.this)
                    .setMaxHeight(100)
                    .setMaxWidth(100)
                    .setQuality(1)
                    .compressToBitmap(newThumbFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbData = baos.toByteArray();
        storageReference = FirebaseStorage.getInstance().getReference("post_images/").child("thumb").child(randomName + "."+getFileExtension(uriPostImage));
        storageReference.putBytes(thumbData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_thumb_url=uri.toString();
                            storePost(downloadUrl,download_thumb_url);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CreatePost.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreatePost.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void storePost(String downloadUrl,String download_thumb_url) {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("image_url", downloadUrl);
            postMap.put("image_thumb", download_thumb_url);
            postMap.put("title", desc);
            postMap.put("description", desc);
            postMap.put("user_id", user_id);
            postMap.put("timestamp", FieldValue.serverTimestamp());

            postStore.collection("posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CreatePost.this, "Post was added", Toast.LENGTH_LONG).show();
                        sendToHomeActivity();

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CreatePost.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreatePost.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void sendToHomeActivity() {
        Intent mainIntent = new Intent(CreatePost.this, HomeActivity.class);
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
                .start(CreatePost.this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendToHomeActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                uriPostImage = result.getUri();
                post_image.setImageURI(uriPostImage);

                //isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this,"Error: " + error,Toast.LENGTH_SHORT).show();

            }
        }
    }

}
