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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> taskList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_list_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTasks); // Ensure correct ID
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), taskList);
        recyclerView.setAdapter(taskAdapter);

        loadTasks(); // Load tasks on startup

        return view;
    }

    private void loadTasks() {
        db = FirebaseFirestore.getInstance();
        Log.d("FirestoreDebug", "Fetching tasks from Firestore...");

        db.collection("tasks") // Ensure correct collection name
                .orderBy("date", Query.Direction.DESCENDING) // Sort by date
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error fetching tasks: " + error.getMessage());
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        Log.d("FirestoreDebug", "No tasks found.");
                        return;
                    }

                    taskList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        TaskModel task = doc.toObject(TaskModel.class);
                        if (task != null) {
                            task.setTaskId(doc.getId()); // Assign Firestore document ID as taskId
                            taskList.add(task);
                            Log.d("FirestoreDebug", "Task Loaded: " + task.getTitle() + " | ID: " + task.getTaskId());
                        }
                    }
                    taskAdapter.notifyDataSetChanged();
                    Log.d("FirestoreDebug", "Total tasks loaded: " + taskList.size());
                });
    }
}
