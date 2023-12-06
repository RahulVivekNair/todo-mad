package com.example.todo;


import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<String> tasks;
    private TaskItemListener listener;

    public interface TaskItemListener {
        void onTaskRemoved(int position);
        void onTaskCompleted(int position, boolean isCompleted);
    }

    public TaskAdapter(List<String> tasks, TaskItemListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String task = tasks.get(position);
        holder.taskNameTextView.setText(task);

        holder.checkBoxCompleted.setOnCheckedChangeListener(null);
        holder.checkBoxCompleted.setChecked(false); // Reset the checkbox state
        holder.taskNameTextView.setPaintFlags(holder.taskNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTaskCompleted(holder.getAdapterPosition(), isChecked);
            }

            if (isChecked) {
                holder.taskNameTextView.setPaintFlags(holder.taskNameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.taskNameTextView.setPaintFlags(holder.taskNameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView taskNameTextView;
        public CheckBox checkBoxCompleted;

        public ViewHolder(View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.textViewTaskName);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
        }
    }

    public void removeTask(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
    }
}
