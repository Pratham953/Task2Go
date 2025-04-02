package com.example.task2go;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ApplicationListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ApplicationAdapter applicationAdapter;
    private List<ApplicationModel> applicationList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_list);

        recyclerView = findViewById(R.id.recyclerViewApplications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        applicationList = new ArrayList<>();
        applicationAdapter = new ApplicationAdapter(applicationList, getApplicationContext());
        recyclerView.setAdapter(applicationAdapter);

        // Get Task ID from Intent
        String taskId = getIntent().getStringExtra("taskId");
        if (taskId != null) {
            loadApplications(taskId);
        }
    }

    private void loadApplications(String taskId) {
        db.collection("tasks").document(taskId).collection("applications")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    applicationList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ApplicationModel application = document.toObject(ApplicationModel.class);
                        applicationList.add(application);
                    }
                    applicationAdapter.notifyDataSetChanged();
                });
    }
}
