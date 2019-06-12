package com.example.reetesh_ranjan.diaryfeed.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.reetesh_ranjan.diaryfeed.Model.Comments;
import com.example.reetesh_ranjan.diaryfeed.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
    public TextView comment_msg;
    public CircleImageView comment_user_image;
    public TextView comment_user_name;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        comment_msg=(TextView)itemView.findViewById(R.id.comment_message);
        comment_user_name=(TextView)itemView.findViewById(R.id.comment_username);
        comment_user_image=(CircleImageView)itemView.findViewById(R.id.comment_image);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    public void setComments(String comment_message) {
        comment_msg.setText(comment_message);
    }

    public void setUserData(String userName, String userImageUrl) {
        comment_user_name.setText(userName);
        RequestOptions options=new RequestOptions();
        options.placeholder(R.drawable.profile_placeholder);
        Glide.with(itemView.getContext()).applyDefaultRequestOptions(options).load(userImageUrl).into(comment_user_image);
    }
}
public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentViewHolder>{
    public List<Comments> commentsList;
    public Context context;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;

    public CommentRecyclerAdapter(List<Comments> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
        View view=inflater.inflate(R.layout.blog_comments_list,viewGroup,false);
        context=viewGroup.getContext();
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        currentUserId=firebaseAuth.getCurrentUser().getUid();
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder commentViewHolder, int i) {
        commentViewHolder.setIsRecyclable(false);
        String comment_message=commentsList.get(i).getMessage();
        commentViewHolder.setComments(comment_message);

        firestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String userName=task.getResult().getString("name");
                    String userImageUrl=task.getResult().getString("profilePicUrl");
                    commentViewHolder.setUserData(userName,userImageUrl);
                }
                else {
                    //Error handling
                    Toast.makeText(context,"Data Retrieve Failed: "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (commentsList!= null){
            return commentsList.size();
        }
        else {
            return 0;
        }
    }
}
