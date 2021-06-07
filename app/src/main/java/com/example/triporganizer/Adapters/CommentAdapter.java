package com.example.triporganizer.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.Helpers.Utils;
import com.example.triporganizer.Models.Comment;
import com.example.triporganizer.R;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    Context context;
    ArrayList<Comment> allComments;

    public CommentAdapter(Context context, ArrayList<Comment> allComments) {
        this.context = context;
        this.allComments = allComments;
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


//        try {
//            setImageRecyclcer(holder.commentImagesRecycler, allComments.get(position).getPictures());
//        } catch (Exception e){
//
//        }
//        if (!allComments.get(position).getPictures().isEmpty()){
//            Log.d("COMMENT", "nije prazan pictures");
//            setImageRecyclcer(holder.commentImagesRecycler, allComments.get(position).getPictures());
//        }
    }

    @Override
    public int getItemCount() {
        return allComments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{

        TextView commentUser;
        TextView commentText;
        TextView commentTime;
        RecyclerView commentImagesRecycler;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            commentUser = itemView.findViewById(R.id.tv_comment_user);
            commentText = itemView.findViewById(R.id.tv_comment_text);
            commentTime = itemView.findViewById(R.id.tv_comment_time);
            commentImagesRecycler = itemView.findViewById(R.id.rv_images);
        }
    }

    private void setImageRecycler(RecyclerView recyclerView, List<String> images){
        CommentImageAdapter commentImageAdapter = new CommentImageAdapter(context, images);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(commentImageAdapter);
    }
}
