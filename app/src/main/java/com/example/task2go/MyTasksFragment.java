package com.example.task2go;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyTasksFragment extends Fragment {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> taskList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_tasks, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), taskList);
        recyclerView.setAdapter(taskAdapter);

        loadAppliedTasks();

        return view;
    }

    private void loadAppliedTasks() {
        String userId = auth.getCurrentUser().getUid();

        // Fetch user document to get applied tasks
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> appliedTaskIds = (List<String>) documentSnapshot.get("appliedTasks");

                        if (appliedTaskIds != null && !appliedTaskIds.isEmpty()) {
                            taskList.clear();
                            for (String taskId : appliedTaskIds) {
                                db.collection("tasks").document(taskId).get()
                                        .addOnSuccessListener(taskDoc -> {
                                            if (taskDoc.exists()) {
                                                TaskModel task = taskDoc.toObject(TaskModel.class);
                                                taskList.add(task);
                                                taskAdapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load tasks", Toast.LENGTH_SHORT).show();
                });
    }
}

