package com.example.triporganizer.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.Helpers.Utils;
import com.example.triporganizer.Models.Comment;
import com.example.triporganizer.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    Context context;
    ArrayList<Comment> allComments;
    ArrayList<String> commentsIDs;

    public CommentAdapter(Context context, ArrayList<Comment> allComments, ArrayList<String> commentsIDs) {
        this.context = context;
        this.allComments = allComments;
        this.commentsIDs = commentsIDs;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_main_row, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.commentUser.setText(allComments.get(position).getUser() + ":");
        holder.commentText.setText(allComments.get(position).getComment());

        String time, date, combined;
        time = Utils.timestampToTime(allComments.get(position).getTimestamp());
        date = Utils.timestampToDate(allComments.get(position).getTimestamp());
        combined = time + " " + date;

        holder.commentTime.setText(combined);

        // try get pictures, if exists setImageRecycler
        boolean picturesExists = false;
        try {
            allComments.get(position).getPictures();
            picturesExists = true;
        } catch (Exception e){
            Log.d("COMMENTS", allComments.get(position).getComment() + " - komentar bez slika");
        }

        if (picturesExists){
            setImageRecycler(holder.commentImagesRecycler, allComments.get(position).getPictures());
        }
        
        holder.cardViewParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText(context, "Long click", Toast.LENGTH_SHORT).show();
                showDeleteDialog(context, position);
                return false;
            }
        });



    }

    @Override
    public int getItemCount() {
        return allComments.size();
    }

    private void showDeleteDialog(Context context, int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Comment delete");
        alert.setMessage("Delete this comment?");
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(context, "izbriši", Toast.LENGTH_SHORT).show();
                deleteComment(position);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();

    }

    public void deleteComment(int position){
        String str = allComments.get(position).getComment();
//        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Comments");
        databaseReference.child(commentsIDs.get(position)).removeValue();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{

        TextView commentUser;
        TextView commentText;
        TextView commentTime;
        RecyclerView commentImagesRecycler;
        CardView cardViewParent;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            commentUser = itemView.findViewById(R.id.tv_comment_user);
            commentText = itemView.findViewById(R.id.tv_comment_text);
            commentTime = itemView.findViewById(R.id.tv_comment_time);
            commentImagesRecycler = itemView.findViewById(R.id.rv_images);
            cardViewParent = itemView.findViewById(R.id.cardview_comment);
        }
    }

    private void setImageRecycler(RecyclerView recyclerView, List<String> images){
        CommentImageAdapter commentImageAdapter = new CommentImageAdapter(context, images);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(commentImageAdapter);
    }
}
