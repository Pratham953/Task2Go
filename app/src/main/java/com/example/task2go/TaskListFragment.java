package com.example.task2go;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> taskList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_tasks, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), taskList);
        recyclerView.setAdapter(taskAdapter);

        loadTasks(); // Call method to load tasks

        return view;
    }


    private void loadTasks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tasks") // Fetch all tasks
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        taskList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            TaskModel taskModel = document.toObject(TaskModel.class);
                            if (taskModel != null) {
                                taskModel.setTaskId(document.getId()); // Store Firestore document ID
                                taskList.add(taskModel);
                                Log.d("TaskListFragment", "Task Loaded: " + taskModel.getTitle() + " (ID: " + taskModel.getTaskId() + ")");
                            }
                        }

                        Log.d("TaskListFragment", "Total Tasks Loaded: " + taskList.size());

                        taskAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("TaskListFragment", "Error fetching tasks", task.getException());
                    }
                });
    }






    @Override
    public void onResume() {
        super.onResume();
        loadTasks(); // Refresh the list when returning to this fragment
    }
}
