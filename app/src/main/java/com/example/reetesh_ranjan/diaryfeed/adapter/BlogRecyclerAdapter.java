package com.example.reetesh_ranjan.diaryfeed.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.reetesh_ranjan.diaryfeed.BlogCommentsActivity;
import com.example.reetesh_ranjan.diaryfeed.CommentPopUpWindow;
import com.example.reetesh_ranjan.diaryfeed.Model.BlogPost;
import com.example.reetesh_ranjan.diaryfeed.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

class BlogItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

    public TextView blog_title;
    public TextView blog_desc;
    public TextView blog_time;
    public ImageView blog_image;
    public TextView blog_user_name;
    public CircleImageView blog_user_image;

    public ImageView btn_likes;
    public TextView likes_count;

    public ImageView btn_comments;
    public TextView comment_count;

    public ImageView blog_bookmarks;

    public BlogItemViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnCreateContextMenuListener(this);

        blog_desc=(TextView)itemView.findViewById(R.id.blog_desc);
        blog_time=(TextView)itemView.findViewById(R.id.blog_date);
        blog_image=(ImageView) itemView.findViewById(R.id.blog_image);
        blog_user_name=(TextView)itemView.findViewById(R.id.blog_user_name);
        blog_user_image=(CircleImageView) itemView.findViewById(R.id.blog_user_image);

        btn_likes=(ImageView) itemView.findViewById(R.id.blog_like_btn);
        likes_count=(TextView) itemView.findViewById(R.id.blog_like_count);

        btn_comments=(ImageView) itemView.findViewById(R.id.blog_comment_icon);
        comment_count=(TextView) itemView.findViewById(R.id.blog_comment_count);

        blog_bookmarks=(ImageView) itemView.findViewById(R.id.blog_bookmarks);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    public void setDescText(String desc) {
        blog_desc.setText(desc);
    }

    public void setBlogImage(String imageUrl, String thumbUrl) {
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(R.drawable.image_placeholder);
        Glide.with(itemView.getContext()).applyDefaultRequestOptions(requestOptions).load(imageUrl).into(blog_image);
    }

    public void setDate(String date) {
        blog_time.setText(date);
    }

    public void setUserData(String name,String image) {
        blog_user_name.setText(name);
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(R.drawable.profile_placeholder);
        Glide.with(itemView.getContext()).applyDefaultRequestOptions(requestOptions).load(image).into(blog_user_image);

    }

    public void updateLikesCount(int count) {
        likes_count.setText(count + " Likes");

    }
}

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogItemViewHolder> {

    public List<BlogPost> blogPostList;
    public Context context;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;

    public BlogRecyclerAdapter(List<BlogPost> blogPostList, Context context) {
        this.blogPostList = blogPostList;
        //this.context = context;
    }

    @NonNull
    @Override
    public BlogItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
        View view=inflater.inflate(R.layout.blog_list_item,viewGroup,false);
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        currentUserId=firebaseAuth.getCurrentUser().getUid();
        context=viewGroup.getContext();
        return new BlogItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BlogItemViewHolder blogItemViewHolder, int i) {
        blogItemViewHolder.setIsRecyclable(false);

        final String blogPostId=blogPostList.get(i).blogPostId;
        String desc=blogPostList.get(i).getDescription();
        blogItemViewHolder.setDescText(desc);
        String title=blogPostList.get(i).getTitle();
       // blogItemViewHolder.setDescText(desc);
        final String userId=blogPostList.get(i).getUser_id();
        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String userName=task.getResult().getString("name");
                    String userImageUrl=task.getResult().getString("profilePicUrl");
                    blogItemViewHolder.setUserData(userName,userImageUrl);
                }
                else {
                    //Error handling
                    Toast.makeText(context,"Data Retrieve Failed: "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        String imageUrl=blogPostList.get(i).getImage_url();
        String thumbUrl=blogPostList.get(i).getImage_thumb();
        blogItemViewHolder.setBlogImage(imageUrl,thumbUrl);

        long milliseconds=blogPostList.get(i).getTimestamp().getTime();
        String date=DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
        blogItemViewHolder.setDate(date);
        //Get Likes Count
        //Get Likes Count
        firestore.collection("posts/" + blogPostId + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    blogItemViewHolder.updateLikesCount(count);

                } else {

                    blogItemViewHolder.updateLikesCount(0);

                }

            }
        });



        //Get Likes
        firestore.collection("posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    Drawable drawable=context.getResources().getDrawable(R.drawable.action_like_accent);
                    blogItemViewHolder.btn_likes.setImageDrawable(drawable);
                }
                else {
                    Drawable drawable=context.getResources().getDrawable(R.drawable.action_like_gray);
                    blogItemViewHolder.btn_likes.setImageDrawable(drawable);
                }
            }
        });

        //Likes Features
       blogItemViewHolder.btn_likes.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               firestore.collection("posts/" + blogPostId + "/Likes").document(currentUserId).get()
                       .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                               if (!task.getResult().exists()){
                                   saveLikes(blogPostId);
                               }else {
                                   firestore.collection("posts/" + blogPostId + "/Likes").document(currentUserId).delete();
                               }
                           }
                       });
           }
       });
       blogItemViewHolder.btn_comments.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent commentIntent=new Intent(context,BlogCommentsActivity.class);
               commentIntent.putExtra("blog_post_id",blogPostId);
               context.startActivity(commentIntent);
           }
       });
       blogItemViewHolder.blog_bookmarks.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               fetchPost(blogPostId);
           }
       });

    }

    private void fetchPost(final String blogPostId) {
        firestore.collection("posts/").document(blogPostId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                                Map<String,Object> saveMap=new HashMap<>();
                                saveMap.put("image_url", task.getResult().getString("image_url"));
                                saveMap.put("image_thumb", task.getResult().getString("image_thumb"));
                                saveMap.put("title", task.getResult().getString("title"));
                                saveMap.put("description", task.getResult().getString("description"));
                                saveMap.put("user_id", task.getResult().getString("user_id"));
                                saveMap.put("timestamp", task.getResult().getDate("timestamp"));

                                savePost(saveMap,blogPostId);

                        }else{
                            Toast.makeText(context,"Save error:"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void savePost(Map<String,Object> saveMap, String blogPostId) {
        firestore.collection("Users").document(currentUserId).collection("SavePosts")
                .document(blogPostId)
                .set(saveMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context,"Saved..",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context,"Error:"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void saveLikes(String blogPostId) {
        Map<String,Object> likesMap=new HashMap<>();
        likesMap.put("userId",currentUserId);
        likesMap.put("timestamp",FieldValue.serverTimestamp());

        firestore.collection("posts/" + blogPostId + "/Likes").document(currentUserId)
                .set(likesMap);
    }

    @Override
    public int getItemCount() {
        return blogPostList.size();
    }
}
