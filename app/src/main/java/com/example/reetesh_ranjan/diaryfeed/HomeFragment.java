package com.example.reetesh_ranjan.diaryfeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.reetesh_ranjan.diaryfeed.Model.BlogPost;
import com.example.reetesh_ranjan.diaryfeed.adapter.BlogRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView blog_list_view;
    private List<BlogPost> blog_list;
    private BlogRecyclerAdapter blogRecyclerAdapter;

    private FirebaseFirestore firestore;
    private DocumentSnapshot lastVisible;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String user_id;

    private Boolean isLoaded=true;

    public HomeFragment(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.home_fragment,container,false);

        firestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        blog_list_view=view.findViewById(R.id.blog_list_view);
        blog_list = new ArrayList<>();

        blogRecyclerAdapter=new BlogRecyclerAdapter(blog_list,getContext());
        blog_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        blog_list_view.setAdapter(blogRecyclerAdapter);

        if (mAuth.getCurrentUser()!=null){
            loadPost();
        }

        return view;
    }

    private void loadPost() {

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean reachedButton = !(recyclerView.canScrollVertically(1));
                    if (reachedButton) {
                        String title = lastVisible.getString("title");
                        Toast.makeText(getContext(), "Reached: " + title, Toast.LENGTH_SHORT).show();
                        loadMorePost();
                    }
                }
            });
            Query firstQuery = firestore.collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(3);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (isLoaded){
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    }
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostId=doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                            if (isLoaded){
                                blog_list.add(blogPost);
                            }
                            else {
                                blog_list.add(0,blogPost);
                            }

                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                    isLoaded=false;
                }
            });
    }
    private void loadMorePost(){
        Query nextQuery=firestore.collection("posts")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostId=doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            blog_list.add(blogPost);
                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }
}
