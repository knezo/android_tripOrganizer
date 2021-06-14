package com.example.triporganizer.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.Models.Task;
import com.example.triporganizer.Models.Tasklist;
import com.example.triporganizer.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private Tasklist tasklist;
    private ArrayList<Task> tasks;

    public TaskAdapter(Context context, Tasklist tasklist) {
        this.context = context;
        this.tasklist = tasklist;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.task_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Log.d("TASK", tasklist.getTasks().toString());
        Task task = tasklist.getTasks().get(position);
        holder.taskCheckBox.setText(task.getTask_str());
        if (task.isDone()){ holder.taskCheckBox.setChecked(true);}
        else { holder.taskCheckBox.setChecked(false);}

        // listener for click on checkbox
        holder.taskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTaskDone(position, isChecked);
            }
        });

    }



    @Override
    public int getItemCount() {
        int tasksCount;
        try {
            tasksCount = tasklist.getTasks().size();
        } catch (Exception e){
            tasksCount = 0;
        }
        return tasksCount;
    }

    public static final class TaskViewHolder extends RecyclerView.ViewHolder{

        MaterialCheckBox taskCheckBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            taskCheckBox = itemView.findViewById(R.id.cb_task);
        }
    }

    // change task.isDone in database
    private void setTaskDone(int position, boolean isChecked) {
        ArrayList<Task> tasks = tasklist.getTasks();
        Task task = tasks.get(position);

        if (isChecked){
            task.setDone(true);
        } else {
            task.setDone(false);
        }

        Map<String, Object> taskUpdate = new HashMap<>();
        taskUpdate.put(String.valueOf(position), task);

        FirebaseDatabase.getInstance().getReference("Tasklists")
                .child(tasklist.getTasklistID())
                .child("tasks")
                .updateChildren(taskUpdate);
    }
}
