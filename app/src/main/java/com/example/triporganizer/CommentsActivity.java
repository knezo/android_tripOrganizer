package com.example.triporganizer;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.Adapters.CommentAdapter;
import com.example.triporganizer.Models.Comment;
import com.example.triporganizer.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageButton ibChooseImage, ibAddComment;
    EditText etComment;
    ProgressBar pbComment;
    TextView tvImageNumber;

    RecyclerView commentRecycleView;
    CommentAdapter commentAdapter;
    ArrayList<Comment> allComments;

    ArrayList<Uri> imageList = new ArrayList<>();
    ArrayList<String> imagesUrl = new ArrayList<>();

    StorageReference storageReference;
    DatabaseReference databaseReference;
    DatabaseReference commentsReference;
    FirebaseAuth firebaseAuth;

    String userID;
    String userName;
    String tripID;

    double uploadProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        ibChooseImage = findViewById(R.id.ib_choose_image);
        ibAddComment = findViewById(R.id.ib_add_comment);
        etComment = findViewById(R.id.et_comment);
        pbComment = findViewById(R.id.progressBar_comment);
        tvImageNumber = findViewById(R.id.tv_image_num);

        getIncomingIntent();

        storageReference = FirebaseStorage.getInstance().getReference("pictures");
        databaseReference = FirebaseDatabase.getInstance().getReference("Comments");
        commentsReference = FirebaseDatabase.getInstance().getReference("Comments");


        // get current userID and userName from database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            userID = user.getUid();

            FirebaseDatabase.getInstance().getReference("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User tmpUser = snapshot.getValue(User.class);

                    assert tmpUser != null;
                    userName = tmpUser.username;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            Log.d("SLIKE", "userID" + user.getUid());
        }


        allComments = new ArrayList<>();


        commentRecycleView = findViewById(R.id.rv_comments);
        commentRecycleView.setHasFixedSize(true);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        commentRecycleView.setLayoutManager(layoutManager);
        commentRecycleView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, allComments);
        commentRecycleView.setAdapter(commentAdapter);

        Query query = commentsReference
                .orderByChild("tripID")
                .equalTo(tripID);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allComments.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    allComments.add(0, comment);

//                    try {
//                        Log.d("COMMENTS", comment.getPictures().toString());
//
//                    } catch (Exception e){
//                        Log.d("COMMENTS", "Comment nema slike:" + comment.getComment());
//                    }
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        ibChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        ibAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadComment();
            }
        });
    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void uploadComment() {
        if (etComment.getText().toString().matches("")){
            etComment.setError("Please enter comment");
            etComment.requestFocus();
            return;
        }

        // if there is only text comment, no pictures
        if (imageList.isEmpty()){
            Log.d("SLIKE", "imageList is empty:" + imageList.toString());
            saveToDatabase();
            return;
        }

        uploadProgress = 0.0;
        pbComment.setVisibility(View.VISIBLE);
        imagesUrl.clear();

        Uri lastImage = imageList.get(imageList.size()-1);

//        Log.d("SLIKE", "imageList: " + imageList.toString());
//        Log.d("SLIKE", "lastImageURI: " + lastImage.toString());

        for (int i = 0; i < imageList.size(); i++){
            Uri imageUri = imageList.get(i);

            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // get image URL
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.d("SLIKE", "url: " + url);
                            imagesUrl.add(url);
                        }
                    });
                    Log.d("SLIKE", "Uploadana je slika: " + imageUri);


                    // successfuly uploaded last image
                    if (imageUri == lastImage){
                        tvImageNumber.setVisibility(View.GONE);
                        pbComment.setVisibility(View.GONE);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("SLIKE", "imagesURL delay: "+imagesUrl.toString());
                                saveToDatabase();
                            }
                        }, 3000);

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CommentsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount())/ (double) imageList.size();
//                    Log.d("SLIKE", "PROGRESS: " + progress);
                    uploadProgress = uploadProgress + progress;
                    pbComment.setProgress((int) uploadProgress);
                }
            });

        }

    }

    // save comment to database
    private void saveToDatabase(){

        long timestamp = System.currentTimeMillis();
        String commentStr = etComment.getText().toString().trim();

        Log.d("COMMENT", "user: " + userName);
        Log.d("COMMENT", "tripID: " + tripID);
        Log.d("COMMENT", "comment: " + commentStr);
        Log.d("COMMENT", "pictures: " + imagesUrl.toString());
        Log.d("COMMENT", "long: " + timestamp);

        String commentID = databaseReference.push().getKey();
        Comment comment = new Comment(userName, tripID, commentStr, imagesUrl, timestamp);


        assert commentID != null;
        databaseReference.child(commentID).setValue(comment);

        imageList.clear();
        imagesUrl.clear();
        etComment.setText("");

    }


    // Choose images for upload
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null){

            Uri imageUri;
            imageList.clear();

            // 2 and more pictures selected
            if (data.getClipData() != null){
                Log.d("SLIKE", "Ušo, više slika");

                int countData = data.getClipData().getItemCount();

                for (int i = 0; i < countData; i++){
                    imageUri = data.getClipData().getItemAt(i).getUri();
                    imageList.add(imageUri);
                    Log.d("SLIKE", "Upload - " + i + ":___" + imageUri);
                }

                countData = imageList.size();

                tvImageNumber.setText("You have selected " + countData + " pictures.");
                tvImageNumber.setVisibility(View.VISIBLE);


            // 1 picture selected
            } else if (data.getData() != null){
                Log.d("SLIKE", "Ušo, jedna slika");

                imageUri = data.getData();
                imageList.add(imageUri);
                Log.d("SLIKE", "Upload - :___" + imageUri);

                tvImageNumber.setText("You have selected 1 picture.");
                tvImageNumber.setVisibility(View.VISIBLE);
            }


        } else {
            Log.d("SLIKE", "Upload nije prošao");
        }
    }

    // get intent extras sent form main acitvity
    private void getIncomingIntent(){
        if(getIntent().hasExtra("trip_id")){
            tripID = getIntent().getStringExtra("trip_id");
//            Toast.makeText(this, tripID, Toast.LENGTH_SHORT).show();
        }
    }
}
