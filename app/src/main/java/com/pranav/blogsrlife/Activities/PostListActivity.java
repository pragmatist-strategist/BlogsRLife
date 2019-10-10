package com.pranav.blogsrlife.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pranav.blogsrlife.DataHandling.BlogRecyclerAdapter;
import com.pranav.blogsrlife.Models.Blog;
import com.pranav.blogsrlife.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostListActivity extends AppCompatActivity {
    private DatabaseReference myDatabaseReference;
    private RecyclerView recyclerView;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private List<Blog> blogList;
    private FirebaseUser myUser;
    private FirebaseAuth myAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();

        FirebaseDatabase myDatabase = FirebaseDatabase.getInstance();
        myUser = myAuth.getCurrentUser();
        myDatabaseReference = myDatabase.getReference().child("MyBlog");
        myDatabaseReference.keepSynced(true);

        blogList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                if (myUser != null && myAuth != null) {
                    startActivity(new Intent(PostListActivity.this, AddPostActivity.class));
                    finish();
                }
                break;
            case R.id.action_signout:
                if (myUser != null && myAuth != null) {
                    myAuth.signOut();
                    startActivity(new Intent(PostListActivity.this, MainActivity.class));
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Blog blog = dataSnapshot.getValue(Blog.class);
                blogList.add(blog);

                Collections.reverse(blogList);
                blogRecyclerAdapter = new BlogRecyclerAdapter(PostListActivity.this, blogList);
                recyclerView.setAdapter(blogRecyclerAdapter);
                blogRecyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
