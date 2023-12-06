package com.example.todo;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> tasks;
    private TaskAdapter taskAdapter;
    private RecyclerView recyclerViewTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));

        loadTasks();

        taskAdapter = new TaskAdapter(tasks, new TaskAdapter.TaskItemListener() {
            @Override
            public void onTaskRemoved(int position) {
                // This method will be called when a task is swiped away
                tasks.remove(position);
                taskAdapter.notifyItemRemoved(position);
                saveTasks();
            }

            @Override
            public void onTaskCompleted(int position, boolean isCompleted) {
                // Here you can handle the task completion logic if needed
                saveTasks();
            }
        });
        recyclerViewTasks.setAdapter(taskAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // We don't want move functionality
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                // First, remove the task from the data set
                tasks.remove(position);
                // Then, notify the adapter that an item has been removed
                taskAdapter.notifyItemRemoved(position);
                // Update the adapter's data set to reflect the removal
                taskAdapter.notifyItemRangeChanged(position, taskAdapter.getItemCount());
                // Finally, save the updated task list
                saveTasks();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewTasks);


        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTaskDialog();
            }
        });
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a new task");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addTask(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addTask(String task) {
        tasks.add(task);
        taskAdapter.notifyItemInserted(tasks.size() - 1);
        saveTasks();
    }

    private void saveTasks() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tasks);
        editor.putString("tasks", json);
        editor.apply();
    }

    private void loadTasks() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("tasks", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        tasks = gson.fromJson(json, type);

        if (tasks == null) {
            tasks = new ArrayList<>();
        }
    }
}
