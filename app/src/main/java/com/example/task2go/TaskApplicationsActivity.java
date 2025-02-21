package com.example.task2go;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class TaskApplicationsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ApplicationAdapter applicationAdapter;
    private List<ApplicationModel> applicationList;
    private FirebaseFirestore db;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_applications);

        recyclerView = findViewById(R.id.recyclerViewApplications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        applicationList = new ArrayList<>();
        applicationAdapter = new ApplicationAdapter(applicationList, this);
        recyclerView.setAdapter(applicationAdapter);

        taskId = getIntent().getStringExtra("taskId");
        loadApplications();
    }

    private void loadApplications() {
        db.collection("tasks").document(taskId)
                .collection("applications")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ApplicationModel app = doc.toObject(ApplicationModel.class);
                        applicationList.add(app);
                    }
                    applicationAdapter.notifyDataSetChanged();
                });
    }
}
