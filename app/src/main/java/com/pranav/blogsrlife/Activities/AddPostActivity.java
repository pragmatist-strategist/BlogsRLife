package com.pranav.blogsrlife.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pranav.blogsrlife.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddPostActivity extends AppCompatActivity {
    private static final int GALLERY_CODE = 1;
    private ImageButton myPostImage;
    private EditText myPostTitle;
    private EditText myPostDesc;
    private StorageReference myStorage;
    private DatabaseReference myPostDatabase;
    private FirebaseUser myUser;
    private ProgressDialog myProgress;
    private Uri myImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        myProgress = new ProgressDialog(this);
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();
        myStorage = FirebaseStorage.getInstance().getReference();
        myPostDatabase = FirebaseDatabase.getInstance().getReference().child("MyBlog");
        myPostImage = findViewById(R.id.imageButton);
        myPostTitle = findViewById(R.id.EtPostTitle);
        myPostDesc = findViewById(R.id.descriptionPost);
        Button mySubmitButton = findViewById(R.id.submitPost);
        myPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });
        mySubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            myImageUri = data.getData();
            myPostImage.setImageURI(myImageUri);

        }
    }

    private void startPosting() {
        myProgress.setMessage("Posting to blog... please wait");
        myProgress.show();
        final String titleVal = myPostTitle.getText().toString().trim();
        final String descVal = myPostDesc.getText().toString().trim();
        if (!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal)
                && myImageUri != null) {
            final StorageReference filepath = myStorage.child("MyBlog_Images").child(Objects.requireNonNull(myImageUri.getLastPathSegment()));
            filepath.putFile(myImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadurl = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost = myPostDatabase.push();

                    Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("title", titleVal);
                    dataToSave.put("desc", descVal);
                    dataToSave.put("image", downloadurl != null ? downloadurl.toString() : null);
                    dataToSave.put("timeId", String.valueOf(java.lang.System.currentTimeMillis()));
                    dataToSave.put("userId", myUser.getUid());
                    dataToSave.put("username", myUser.getEmail());
                    newPost.setValue(dataToSave);
                    myProgress.dismiss();
                    startActivity(new Intent(AddPostActivity.this, PostListActivity.class));
                    finish();


                }
            });
        }
    }

}


