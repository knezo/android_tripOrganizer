package com.example.triporganizer.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.Models.Tasklist;
import com.example.triporganizer.R;

import java.util.ArrayList;

public class TasklistAdapter extends RecyclerView.Adapter<TasklistAdapter.TasklistViewHolder> {

    Context context;
    ArrayList<Tasklist> allTasklists;

    public TasklistAdapter(Context context, ArrayList<Tasklist> allTasklists) {
        this.context = context;
        this.allTasklists = allTasklists;
    }

    @NonNull
    @Override
    public TasklistAdapter.TasklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TasklistViewHolder(LayoutInflater.from(context).inflate(R.layout.tasklist_row, parent, false));


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TasklistAdapter.TasklistViewHolder holder, int position) {
        holder.tasklistUser.setText(allTasklists.get(position).getUsername() + "'s tasks");
    }

    @Override
    public int getItemCount() {
        return allTasklists.size();
    }


    public class TasklistViewHolder extends RecyclerView.ViewHolder {

        TextView tasklistUser;


        public TasklistViewHolder(@NonNull View itemView) {
            super(itemView);

            tasklistUser = itemView.findViewById(R.id.tv_username_tasklist);

        }
    }
}
