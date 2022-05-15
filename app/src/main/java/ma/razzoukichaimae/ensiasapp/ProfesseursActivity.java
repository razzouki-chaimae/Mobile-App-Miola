package ma.razzoukichaimae.ensiasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import model.Etudiant;
import model.Professeur;

public class ProfesseursActivity extends AppCompatActivity {

    RecyclerView listProfs ;
    FloatingActionButton addProf;

    RecyclerViewAdapter recyclerViewAdapter;

    LinkedList<Professeur> profs;

    FirebaseFirestore firestore;

    ProgressDialog progressDialog;

    // calling on create option menu
    // layout to inflate our menu file.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // below line is to get our inflater
        MenuInflater inflater = getMenuInflater();
        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_item, menu);
        // below line is to get our menu item.
        MenuItem item = menu.findItem(R.id.search_item);
        // getting search view of our item
        SearchView searchView = (SearchView) item.getActionView();

        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                //filter(newText);

                // creating a new list to filter our data.
                LinkedList<Professeur> filteredList = new LinkedList<>();

                // running a for loop to compare elements.
                for (Professeur item : profs) {
                    // checking if the entered string matched with any item of our recycler view.
                    if (item.getFullName().toLowerCase().contains(newText.toLowerCase())) {
                        // if the item is matched we are
                        // adding it to our filtered list.
                        filteredList.add(item);
                    }
                }
                if (filteredList.isEmpty()) {
                    // if no item is added in filtered list we are
                    // displaying a toast message as no data found.
                    Toast.makeText(ProfesseursActivity.this, "No Data Found..", Toast.LENGTH_SHORT).show();
                } else {
                    // at last we are passing that filtered
                    // list to our adapter class.
                    recyclerViewAdapter.filterList(filteredList);
                }

                return false;
            }
        });
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professeurs);

        listProfs = findViewById(R.id.listprofs);
        listProfs.setHasFixedSize(true);
        listProfs.setLayoutManager(new LinearLayoutManager(ProfesseursActivity.this));

        addProf = findViewById(R.id.addprof);
        addProf.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(ProfesseursActivity.this);
            //We have added a title in the custom layout. So let's disable the default title.
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //The user will be able to cancel the dialog by clicking anywhere outside the dialog.
            dialog.setCancelable(true);
            //Mention the name of the layout of your custom dialog.
            dialog.setContentView(R.layout.add_prof);

            //Initializing the views of the dialog.
            final EditText nom = dialog.findViewById(R.id.ProfessorName);
            final EditText departementList = dialog.findViewById(R.id.departementprof);
            final EditText email = dialog.findViewById(R.id.email);
            final EditText pwd = dialog.findViewById(R.id.password);
            final ConstraintLayout ajouter = dialog.findViewById(R.id.ajouterprof);

            ajouter.setOnClickListener(view1 -> {
                //get the new data
                String name = nom.getText().toString();
                String departement = departementList.getText().toString();
                String emailText = email.getText().toString();
                String password = pwd.getText().toString();

                if (name.isEmpty()) {
                    nom.setError("full name is required");
                    nom.requestFocus();
                    return;
                }
                ////////////
                if (departement.isEmpty()) {
                    departementList.setError("full name is required");
                    departementList.requestFocus();
                    return;
                }
                ////////////
                if (emailText.isEmpty()) {
                    email.setError("email is required");
                    email.requestFocus();
                    return;
                }
                /////////////////
                if (password.isEmpty()) {
                    pwd.setError("password is required");
                    pwd.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    pwd.setError("min password length should be 6 characters!");
                    pwd.requestFocus();
                    return;
                }

                //Initialize Firebase Auth and store
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(emailText, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfesseursActivity.this, "Le professeur "+name+" a été bien créé.", Toast.LENGTH_SHORT).show();
                        String userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firestore.collection("Professeur").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("fullName", name);
                        user.put("departement", departement);
                        user.put("email", emailText);
                        user.put("password", password);
                        user.put("ID", userID);

                        dialog.dismiss();

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "user profile is created for " + userID);
                            }
                        });

                    } else {
                        Toast.makeText(ProfesseursActivity.this, "Création échouée.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            dialog.show();
        });

        profs = new LinkedList<>();

        firestore = FirebaseFirestore.getInstance();

        recyclerViewAdapter = new RecyclerViewAdapter(ProfesseursActivity.this, profs);
        listProfs.setAdapter(recyclerViewAdapter);
        /************************************************************************************/

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        //displaying progress dialog while fetching images
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        System.out.println("1) Getting profs ..................................................................................................");

        firestore.collection("Professeur").orderBy("fullName", Query.Direction.ASCENDING)
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
                                System.out.println("2) Professeur n° "+i+"-------------------------------------------------------------------------");

                                profs.add(dc.getDocument().toObject(Professeur.class));
                            }
                            recyclerViewAdapter.notifyDataSetChanged();

                            if(progressDialog.isShowing())  progressDialog.dismiss();
                        }
                    }
                });

        System.out.println("-------------------------------Getting data finished-------------------------------");
    }

}