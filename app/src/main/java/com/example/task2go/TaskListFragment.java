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
        View view = inflater.inflate(R.layout.task_list_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, getContext());
        recyclerView.setAdapter(taskAdapter);

        db = FirebaseFirestore.getInstance();
        loadTasks();

        return view;
    }

    private void loadTasks() {
        db.collection("tasks").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    TaskModel taskItem = doc.toObject(TaskModel.class);

                    // ✅ Assign Firestore document ID as taskId
                    taskItem.setTaskId(doc.getId());

                    taskList.add(taskItem);
                }
                taskAdapter.notifyDataSetChanged();
            } else {
                Log.e("Firestore", "Error loading tasks", task.getException());
            }
        });
    }

}
