package com.example.reetesh_ranjan.diaryfeed.adapter;

import android.content.Context;
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
import com.example.reetesh_ranjan.diaryfeed.Model.BlogPost;
import com.example.reetesh_ranjan.diaryfeed.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class BookmarksViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
    public TextView blog_title;
    public TextView blog_desc;
    public TextView blog_time;
    public ImageView blog_image;
    public TextView blog_user_name;
    public CircleImageView blog_user_image;

    public BookmarksViewHolder(@NonNull View itemView) {
        super(itemView);

        blog_desc=(TextView)itemView.findViewById(R.id.blog_desc);
        blog_time=(TextView)itemView.findViewById(R.id.blog_date);
        blog_image=(ImageView) itemView.findViewById(R.id.blog_image);
        blog_user_name=(TextView)itemView.findViewById(R.id.blog_user_name);
        blog_user_image=(CircleImageView) itemView.findViewById(R.id.blog_user_image);
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
}
public class BookmarksListAdapter extends RecyclerView.Adapter<BookmarksViewHolder> {
    private List<BlogPost> blogPostList;
    private Context context;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;

    public BookmarksListAdapter(List<BlogPost> blogPostList) {
        this.blogPostList = blogPostList;
    }

    @NonNull
    @Override
    public BookmarksViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
        View view=inflater.inflate(R.layout.bookmarks_list_items,viewGroup,false);
        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        currentUserId=firebaseAuth.getCurrentUser().getUid();
        context=viewGroup.getContext();
        return new BookmarksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookmarksViewHolder bookmarksViewHolder, int i) {
        bookmarksViewHolder.setIsRecyclable(false);

        final String blogPostId=blogPostList.get(i).blogPostId;
        String desc=blogPostList.get(i).getDescription();
        bookmarksViewHolder.setDescText(desc);
        String title=blogPostList.get(i).getTitle();
        // blogItemViewHolder.setDescText(desc);
        final String userId=blogPostList.get(i).getUser_id();
        firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String userName=task.getResult().getString("name");
                    String userImageUrl=task.getResult().getString("profilePicUrl");
                    bookmarksViewHolder.setUserData(userName,userImageUrl);
                }
                else {
                    //Error handling
                    Toast.makeText(context,"Data Retrieve Failed: "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        String imageUrl=blogPostList.get(i).getImage_url();
        String thumbUrl=blogPostList.get(i).getImage_thumb();
        bookmarksViewHolder.setBlogImage(imageUrl,thumbUrl);

        long milliseconds=blogPostList.get(i).getTimestamp().getTime();
        String date=DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
        bookmarksViewHolder.setDate(date);

    }

    @Override
    public int getItemCount() {
        return blogPostList.size();
    }
}
