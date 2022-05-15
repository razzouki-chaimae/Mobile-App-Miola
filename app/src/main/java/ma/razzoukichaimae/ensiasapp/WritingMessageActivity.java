package ma.razzoukichaimae.ensiasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WritingMessageActivity extends AppCompatActivity {

    FirebaseFirestore firestore;

    TextView discadMessage;
    Button publier;
    EditText writtenMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing_message);

        getSupportActionBar().hide();

        discadMessage = findViewById(R.id.discadMessage);
        discadMessage.setOnClickListener(view -> finish());

        publier = findViewById(R.id.publier);
        publier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = writtenMessage.getText().toString();

                if(message.isEmpty()){
                    writtenMessage.setError("Ecrivez votre message.");
                    writtenMessage.requestFocus();
                    return;
                } else {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WritingMessageActivity.this);
                    String userID = preferences.getString("userID", "n/a");
                    firestore = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = firestore.collection("Professeur").document(userID);
                    documentReference.addSnapshotListener(WritingMessageActivity.this, new EventListener<DocumentSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            String name= value.getString("fullName");

                                DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("dd");
                                LocalDateTime now1 = LocalDateTime.now();

                                Calendar c = Calendar.getInstance();
                                String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.FRANCE );

                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                                LocalDateTime now = LocalDateTime.now();
                                String today = dtf1.format(now1)+" "+month+", "+dtf.format(now);

                                // Add a new document with a generated id.
                                Map<String, Object> msg = new HashMap<>();
                                msg.put("Professeur", name);
                                msg.put("Corps", message);
                                msg.put("dateMessage", today);

                                firestore.collection("Message")
                                        .add(msg)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                                                Toast.makeText(WritingMessageActivity.this, "Le message a été publié", Toast.LENGTH_LONG).show();
                                                WritingMessageActivity.this.finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Error adding document", e);
                                                Toast.makeText(WritingMessageActivity.this, "Publication échouée.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                        }
                    });
                }
            }
        });

        writtenMessage = findViewById(R.id.writtenMessage);
    }
}