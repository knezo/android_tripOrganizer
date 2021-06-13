package com.example.triporganizer.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
        holder.commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageDialog(position);
            }
        });

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

    private void openImageDialog(int position){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.image_popup);
        ImageView imageView = dialog.findViewById(R.id.iv_bigImage);
        Picasso.get().load(imageURLs.get(position)).into(imageView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public static final class CommentImageViewHolder extends RecyclerView.ViewHolder{

        ImageView commentImage;

        public CommentImageViewHolder(@NonNull View itemView) {
            super(itemView);

            commentImage = itemView.findViewById(R.id.iv_comment_image);
        }
    }


}
