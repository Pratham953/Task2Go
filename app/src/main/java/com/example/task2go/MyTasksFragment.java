package com.example.task2go;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MyTasksFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> myTasksList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public MyTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_tasks, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myTasksList = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), myTasksList);
        recyclerView.setAdapter(taskAdapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadAppliedTasks(); // Fetch applied tasks

        return view;
    }

    private void loadAppliedTasks() {
        String userId = auth.getCurrentUser().getUid();
        CollectionReference appliedTasksRef = db.collection("applied_tasks");

        appliedTasksRef.whereEqualTo("userId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    myTasksList.clear(); // Clear old data

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        TaskModel task = doc.toObject(TaskModel.class);
                        if (task != null) {
                            myTasksList.add(task);
                        }
                    }

                    taskAdapter.notifyDataSetChanged();

                    if (myTasksList.isEmpty()) {
                        Toast.makeText(getContext(), "No applied tasks found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error loading tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
