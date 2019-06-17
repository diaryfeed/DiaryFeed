package com.example.reetesh_ranjan.diaryfeed.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.reetesh_ranjan.diaryfeed.Model.BlogPost;
import com.example.reetesh_ranjan.diaryfeed.R;
import com.example.reetesh_ranjan.diaryfeed.adapter.BookmarksListAdapter;
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


public class BookMarksFragment extends Fragment {
    private List<BlogPost> blogPostList;
    private RecyclerView bookmarks_list_view;
    private BookmarksListAdapter adapter;

    private FirebaseFirestore firestore;
    private DocumentSnapshot lastVisible;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String user_id;
    private Boolean isLoaded=true;

    private View view;


    public BookMarksFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_book_marks, container, false);
        firestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        user_id=mAuth.getCurrentUser().getUid();

        bookmarks_list_view=view.findViewById(R.id.bookmarks_list_view);
        blogPostList= new ArrayList<>();

        adapter=new BookmarksListAdapter(blogPostList);
        bookmarks_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        bookmarks_list_view.setAdapter(adapter);

        if (mAuth.getCurrentUser()!=null){
            loadPost();
        }
        return view;
    }

    private void loadPost() {
        bookmarks_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        Query firstQuery = firestore.collection("Users").document(user_id).collection("SavePosts")
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
                            blogPostList.add(blogPost);
                        }
                        else {
                            blogPostList.add(0,blogPost);
                        }

                        adapter.notifyDataSetChanged();
                    }
                }
                isLoaded=false;
            }
        });
    }

    private void loadMorePost() {
        Query nextQuery=firestore.collection("Users").document(user_id).collection("SavePosts")
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
                            blogPostList.add(blogPost);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

}
