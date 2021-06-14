package com.example.triporganizer.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.triporganizer.Models.Task;
import com.example.triporganizer.Models.Tasklist;
import com.example.triporganizer.Models.Trip;
import com.example.triporganizer.R;
import com.example.triporganizer.TripActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TasklistAdapter extends RecyclerView.Adapter<TasklistAdapter.TasklistViewHolder> {

    Context context;
    ArrayList<Tasklist> allTasklists;

    Trip trip;


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
        holder.deleteTasklistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Attention!");
                builder.setMessage("You surely want to delete this trip?");

                // Negative - dont delete tasklist
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                // Positive - delete tasklist;
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTasklist(position);
                    }
                });
                builder.show();
            }
        });

        holder.addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, holder.newTask.getText(), Toast.LENGTH_SHORT).show();
                addTask(holder.newTask.getText().toString().trim(), position);
                holder.newTask.setText("");
            }
        });

        setTaskRecycler(holder.tasksRecycleView, allTasklists.get(position), position);
        Log.d("TASK-provjera", "onbindviewholder");
    }


    @Override
    public int getItemCount() {
        return allTasklists.size();
    }


    static class TasklistViewHolder extends RecyclerView.ViewHolder {

        TextView tasklistUser;
        ImageButton deleteTasklistBtn;
        EditText newTask;
        ImageButton addTask;
        RecyclerView tasksRecycleView;




        TasklistViewHolder(@NonNull View itemView) {
            super(itemView);

            tasklistUser = itemView.findViewById(R.id.tv_username_tasklist);
            deleteTasklistBtn = itemView.findViewById(R.id.ib_delete_tasklist);
            newTask = itemView.findViewById(R.id.et_newtask);
            addTask = itemView.findViewById(R.id.ib_add_newtask);
            tasksRecycleView = itemView.findViewById(R.id.rv_tasklist_tasks);

        }

    }


    private void setTaskRecycler(RecyclerView recyclerView, Tasklist tasklist, int tasklistPositon){

        Log.d("TASK", "setTaskRecycler");

        TaskAdapter taskAdapter = new TaskAdapter(context, tasklist);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // implement ItemTouchHelper swipe to delete task in TaskRecycleView
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                deleteTask(tasklistPositon, position);

                taskAdapter.notifyDataSetChanged();
                Toast.makeText(context, "Deleted task", Toast.LENGTH_SHORT).show();
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(taskAdapter);
    }


    private void addTask(String taskText, int position) {
        Tasklist tasklist = allTasklists.get(position);
        String taskID = FirebaseDatabase.getInstance().getReference().push().getKey();

        Task newTask = new Task(taskID, taskText);


        // there is no tasks in tasklist yet
        if (tasklist.getTasks()==null){
            Log.d("TASK", "nema taskova do sad");

            ArrayList<Task> tasks = new ArrayList<Task>();
            tasks.add(newTask);

            Map<String, Object> tasklistUpdate = new HashMap<>();
            tasklistUpdate.put("tasks", tasks);

            FirebaseDatabase.getInstance().getReference("Tasklists")
                    .child(tasklist.getTasklistID())
                    .updateChildren(tasklistUpdate);

        // tasks exists in tasklist
        } else {
            Log.d("TASK", "taskovi vec postoje");

            ArrayList<Task> tasks = tasklist.getTasks();
            tasks.add(newTask);

            Map<String, Object> tasklistUpdate = new HashMap<>();
            tasklistUpdate.put("tasks", tasks);

            FirebaseDatabase.getInstance().getReference("Tasklists")
                    .child(tasklist.getTasklistID())
                    .updateChildren(tasklistUpdate);
        }


    }


    private void deleteTasklist(int position) {
        trip = null;

        Tasklist tasklistForDelete = allTasklists.get(position);

        FirebaseDatabase.getInstance().getReference("Tasklists")
                .child(tasklistForDelete.getTasklistID())
                .removeValue();

        FirebaseDatabase.getInstance().getReference("Trips")
                .child(tasklistForDelete.getTripID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                trip = snapshot.getValue(Trip.class);
//                Log.d("USER", "trip je: " + trip.toString() + trip.getName());
                updateTripMembers(tasklistForDelete);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    // when deleting tasklist, update trip members (remove member from trip.members)
    private void updateTripMembers(Tasklist tasklist) {
        ArrayList<String> members = trip.getMembers();
        String memberID = tasklist.getUserID();

        members.remove(memberID);

        Map<String, Object> tripUpdate = new HashMap<>();
        tripUpdate.put("members", members);

        FirebaseDatabase.getInstance().getReference("Trips").child(tasklist.getTripID()).updateChildren(tripUpdate);
    }


    private void deleteTask(int tasklistPosition, int taskPosition){
        ArrayList<Task> tasks = new ArrayList<>();
        Tasklist tasklist = allTasklists.get(tasklistPosition);
        tasks = tasklist.getTasks();

        tasks.remove(taskPosition);

        Map<String, Object> tasklistUpdate = new HashMap<>();
        tasklistUpdate.put("tasks", tasks);

        FirebaseDatabase.getInstance().getReference("Tasklists").
                child(tasklist.getTasklistID())
                .updateChildren(tasklistUpdate);

    }


}
