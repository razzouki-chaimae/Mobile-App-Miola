package ma.razzoukichaimae.ensiasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

import model.Constants;
import model.Schedule;

public class ViewUploadedSchedulesActivity extends AppCompatActivity {

    //the listview
    ListView listView;

    //database reference to get uploads data
    DatabaseReference mDatabaseReference;

    //list to store uploads data
    List<Schedule> scheduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_uploaded_schedules);

        getSupportActionBar().hide();

        listView = findViewById(R.id.listSchedules);
        scheduleList = new LinkedList<>();

        //adding a clicklistener on listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the upload
                Schedule schedule = scheduleList.get(i);

                System.out.println("------------------- URL = "+schedule.getUrl()+" --------------------------------------------");
                Intent intent = new Intent(ViewUploadedSchedulesActivity.this, ViewpdfActivity.class);
                //intent.putExtra("pdf_url", "https://");
                startActivity(intent);
/*
                //Opening the upload file in browser using the upload url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setData(Uri.parse(schedule.getUrl()));
                intent.setDataAndType(Uri.parse(schedule.getUrl()), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
 */
            }
        });

        //getting the database reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        //retrieving upload data from firebase database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Schedule schedule = postSnapshot.getValue(Schedule.class);
                    scheduleList.add(schedule);
                }

                String[] schedules = new String[scheduleList.size()];

                for (int i = 0; i < schedules.length; i++) {
                    schedules[i] = scheduleList.get(i).getName();
                }

                //displaying it to list
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, schedules);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}