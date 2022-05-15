package ma.razzoukichaimae.ensiasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import model.Professeur;

public class HomeActivity extends AppCompatActivity {
    TextView name, dep, mail, userid;
    ImageView profilPhoto;

    Button allProfs;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;
    StorageReference storageReference;

    LinkedList<Professeur> profs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profilPhoto = findViewById(R.id.profilphoto);
        name = findViewById(R.id.name);
        dep = findViewById(R.id.dep);
        mail = findViewById(R.id.mail);
        userid = findViewById(R.id.userid);

        allProfs = findViewById(R.id.button);
        allProfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getAllProfessors();
                // creating a intent
                Intent intent = new Intent(HomeActivity.this, EtudiantsActivity.class);
                //Bundle bundle = null;
                //bundle.putParcelable("list", new ParcelableLinkedList<>(profs));
                startActivity(intent);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        //userID = firebaseAuth.getCurrentUser().getUid();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userID = preferences.getString("userID", "n/a");
        DocumentReference documentReference = firestore.collection("Professeur").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("fullName"));
                dep.setText(value.getString("departement"));
                mail.setText(value.getString("email"));
                userid.setText(userID);

                storageReference = FirebaseStorage.getInstance().getReference("images/professeurs/"+userID+".png");

                try {
                    File localFile = File.createTempFile("tempfile", ".png");
                    storageReference.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            profilPhoto.setImageBitmap(bitmap);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeActivity.this, "Exception : "+e, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}