package com.example.triporganizer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentImageAdapter extends RecyclerView.Adapter<CommentImageAdapter.CommentImageViewHolder> {

    private Context context;
    private List<String> imageURLs;

    public CommentImageAdapter(Context context, List<String> imageURLs) {
        this.context = context;
        this.imageURLs = imageURLs;
    }

    @NonNull
    @Override
    public CommentImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentImageViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_image_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentImageViewHolder holder, int position) {
//        holder.commentImage.setImageResource(imageURLs.get(position));
        Picasso.get().load(imageURLs.get(position)).into(holder.commentImage);

    }

    @Override
    public int getItemCount() {
        int itemCount;
        try {
            itemCount = imageURLs.size();
        } catch (Exception e){
            itemCount = 0;
        }
        return itemCount;
    }

    public static final class CommentImageViewHolder extends RecyclerView.ViewHolder{

        ImageView commentImage;

        public CommentImageViewHolder(@NonNull View itemView) {
            super(itemView);

            commentImage = itemView.findViewById(R.id.iv_comment_image);
        }
    }


}
