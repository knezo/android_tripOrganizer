package com.example.triporganizer.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.Helpers.Utils;
import com.example.triporganizer.Models.Notification;
import com.example.triporganizer.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    Context context;
    ArrayList<Notification> notifications;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_row, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        String time =
                Utils.timestampToTime(notification.getTimestamp()) +
                " " +
                Utils.timestampToDate(notification.getTimestamp());

        holder.title.setText(notification.getTitle());
        holder.text.setText(notification.getText());
        holder.time.setText(time);


    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView text;
        TextView time;


        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_notification_title);
            text = itemView.findViewById(R.id.tv_notification_text);
            time = itemView.findViewById(R.id.tv_notification_date);
        }
    }
}
