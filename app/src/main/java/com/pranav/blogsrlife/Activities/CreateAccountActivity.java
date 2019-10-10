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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pranav.blogsrlife.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {
    private final static int GALLERY_CODE = 1;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private DatabaseReference myDatabaseReference;
    private StorageReference myFirebaseStorage;
    private FirebaseAuth myAuth;
    private ProgressDialog myProgressDialog;
    private ImageButton profilePic;
    private Uri resultUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        FirebaseDatabase myDatabase = FirebaseDatabase.getInstance();
        myDatabaseReference = myDatabase.getReference().child("MyUsers");

        myAuth = FirebaseAuth.getInstance();

        myFirebaseStorage = FirebaseStorage.getInstance().getReference().child("MyBlog_Profile_Pics");


        myProgressDialog = new ProgressDialog(this);

        firstName = findViewById(R.id.firstNameAct);
        lastName = findViewById(R.id.lastNameAct);
        email = findViewById(R.id.emailAct);
        password = findViewById(R.id.passwordAct);
        profilePic = findViewById(R.id.profilePic);

        Button createAccountBtn = findViewById(R.id.createAccountAct);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                startActivityForResult(galleryIntent, GALLERY_CODE);

            }
        });

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }

    private void createNewAccount() {

        final String name = firstName.getText().toString().trim();
        final String lname = lastName.getText().toString().trim();
        String email = this.email.getText().toString().trim();
        String passwd = password.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lname)
                && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(passwd)) {

            myProgressDialog.setMessage("Creating Account...");
            myProgressDialog.show();

            myAuth.createUserWithEmailAndPassword(email, passwd)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if (authResult != null) {

                                StorageReference imagePath = myFirebaseStorage.child("MyBlog_Profile_Pics")
                                        .child(Objects.requireNonNull(resultUri.getLastPathSegment()));

                                imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String userid = Objects.requireNonNull(myAuth.getCurrentUser()).getUid();

                                        DatabaseReference currenUserDb = myDatabaseReference.child(userid);
                                        currenUserDb.child("firstname").setValue(name);
                                        currenUserDb.child("lastname").setValue(lname);
                                        currenUserDb.child("image").setValue(resultUri.toString());


                                        myProgressDialog.dismiss();

                                        //send users to postList
                                        Intent intent = new Intent(CreateAccountActivity.this, PostListActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });

                            }

                        }
                    });


        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Uri mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                profilePic.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
