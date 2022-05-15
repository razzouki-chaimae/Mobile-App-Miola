package ma.razzoukichaimae.ensiasapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

import model.Etudiant;
import model.Message;

public class MessagesProfessorsActivity extends AppCompatActivity {

    ImageView logoutBtn;
    EditText message;
    RecyclerView recyclerviewMessages;

    MessagesAdapter recyclerViewAdapter;

    LinkedList<Message> messagesList;

    FirebaseFirestore firestore;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_professors);

        logoutBtn = findViewById(R.id.logoutbtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessagesProfessorsActivity.this, LoginActivity.class));
                finish();
            }
        });

        message = findViewById(R.id.writeMessage);

        recyclerviewMessages = findViewById(R.id.recyclerviewMessages);
        recyclerviewMessages.setHasFixedSize(true);
        recyclerviewMessages.setLayoutManager(new LinearLayoutManager(MessagesProfessorsActivity.this));

        messagesList = new LinkedList<>();

        firestore = FirebaseFirestore.getInstance();

        recyclerViewAdapter = new MessagesAdapter(this, messagesList);
        recyclerviewMessages.setAdapter(recyclerViewAdapter);
        /************************************************************************************/

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        //displaying progress dialog while fetching images
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        System.out.println("1) Getting messages ..................................................................................................");

        firestore.collection("Message").orderBy("dateMessage", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            if(progressDialog.isShowing())  progressDialog.dismiss();

                            Log.e("Firestore error", error.getMessage());
                            System.out.println("2) Firebase error ..................................................................................................");
                            return;
                        }
                        int i = 0;
                        for (DocumentChange dc : value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                i++;
                                System.out.println("2) Message n° "+i+"-------------------------------------------------------------------------");

                                messagesList.add(dc.getDocument().toObject(Message.class));
                            }
                            recyclerViewAdapter.notifyDataSetChanged();

                            if(progressDialog.isShowing())  progressDialog.dismiss();
                        }
                    }
                });

        System.out.println("-------------------------------Getting data finished-------------------------------");
    }
}