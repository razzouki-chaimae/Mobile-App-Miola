package ma.razzoukichaimae.ensiasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SchedulesActivity extends AppCompatActivity {

    EditText searchSchedule;
    //RecyclerView recyclerViewSchedules;
    FloatingActionButton addSchedule;

    DatabaseReference database;
    String message;
/*
    SchedulesAdapter recyclerViewAdapter;

    LinkedList<Schedule> schedulesList;

    FirebaseFirestore firestore;

    ProgressDialog progressDialog;
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedules);

        searchSchedule = findViewById(R.id.search_schedule);

        addSchedule = findViewById(R.id.addSchedule);
        addSchedule.setOnClickListener(view -> {

        });

        // Initialising the reference to database
        database = FirebaseDatabase.getInstance().getReference().child("pdf");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // getting a DataSnapshot for the location at the specified
                // relative path and getting in the link variable
                message = dataSnapshot.getValue(String.class);
            }

            // this will called when any problem
            // occurs in getting data
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // we are showing that error message in toast
                Toast.makeText(SchedulesActivity.this, "Error Loading Pdf", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = new Intent(SchedulesActivity.this, ViewpdfActivity.class);
        intent.putExtra("url", message);
        startActivity(intent);
    }
}