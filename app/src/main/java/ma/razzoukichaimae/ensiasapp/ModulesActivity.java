package ma.razzoukichaimae.ensiasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import model.Etudiant;
import model.Module;

public class ModulesActivity extends AppCompatActivity {

    RecyclerView recyclerViewModules;
    FloatingActionButton addModule;

    ModulesAdapter recyclerViewAdapter;

    LinkedList<Module> modulesList;

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
                LinkedList<Module> filteredList = new LinkedList<>();

                // running a for loop to compare elements.
                for (Module item : modulesList) {
                    // checking if the entered string matched with any item of our recycler view.
                    if (item.getIntitule().toLowerCase().contains(newText.toLowerCase())) {
                        // if the item is matched we are
                        // adding it to our filtered list.
                        filteredList.add(item);
                    }
                }
                if (filteredList.isEmpty()) {
                    // if no item is added in filtered list we are
                    // displaying a toast message as no data found.
                    Toast.makeText(ModulesActivity.this, "No Data Found..", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_modules);

        addModule = findViewById(R.id.addmodule);
        addModule.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(ModulesActivity.this);
            //We have added a title in the custom layout. So let's disable the default title.
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //The user will be able to cancel the dialog by clicking anywhere outside the dialog.
            dialog.setCancelable(true);
            //Mention the name of the layout of your custom dialog.
            dialog.setContentView(R.layout.add_module);

            //Initializing the views of the dialog.
            final EditText nom = dialog.findViewById(R.id.module);
            final EditText prof = dialog.findViewById(R.id.profmodule);
            final EditText desc = dialog.findViewById(R.id.moduledescription);
            final ConstraintLayout ajouter = dialog.findViewById(R.id.ajoutermodule);

            ajouter.setOnClickListener(view1 -> {
                //get the new data
                String name = nom.getText().toString();
                String professeur = prof.getText().toString();
                String description = desc.getText().toString();

                if (name.isEmpty()) {
                    nom.setError("champs obligatoire");
                    nom.requestFocus();
                    return;
                }
                ////////////
                if (professeur.isEmpty()) {
                    prof.setError("champs obligatoire");
                    prof.requestFocus();
                    return;
                }
                /////////////////
                if (description.isEmpty()) {
                    desc.setError("champs obligatoire");
                    desc.requestFocus();
                    return;
                }

                // Add a new document with a generated id.
                Map<String, Object> module = new HashMap<>();
                module.put("intitule", name);
                module.put("professeur", professeur);
                module.put("description", description);
                module.put("id", "");

                firestore = FirebaseFirestore.getInstance();

                firestore.collection("Module")
                        .add(module)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());

                                // Set the "isCapital" field of the city 'DC'
                                documentReference
                                        .update("id", documentReference.getId())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("TAG", "DocumentSnapshot successfully updated!");
                                                dialog.dismiss();
                                                Toast.makeText(ModulesActivity.this, "Le module a été bien ajouté", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("TAG", "Error updating document", e);
                                                dialog.dismiss();
                                                Toast.makeText(ModulesActivity.this, "Le module est ajouté avec un id érroné", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error adding document", e);
                                dialog.dismiss();
                                Toast.makeText(ModulesActivity.this, "Echec d'ajouter un module", Toast.LENGTH_LONG).show();
                            }
                        });

            });
            dialog.show();
        });

        recyclerViewModules = findViewById(R.id.recyclerviewmodules);
        recyclerViewModules.setHasFixedSize(true);
        recyclerViewModules.setLayoutManager(new LinearLayoutManager(ModulesActivity.this));

        modulesList = new LinkedList<>();

        firestore = FirebaseFirestore.getInstance();

        recyclerViewAdapter = new ModulesAdapter(this, modulesList);
        recyclerViewModules.setAdapter(recyclerViewAdapter);
        /************************************************************************************/

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        //displaying progress dialog while fetching images
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        System.out.println("1) Getting modules ..................................................................................................");

        firestore.collection("Module").orderBy("intitule", Query.Direction.ASCENDING)
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
                                System.out.println("2) Module n° "+i+"-------------------------------------------------------------------------");

                                modulesList.add(dc.getDocument().toObject(Module.class));
                            }
                            recyclerViewAdapter.notifyDataSetChanged();

                            if(progressDialog.isShowing())  progressDialog.dismiss();
                        }
                    }
                });

        System.out.println("-------------------------------Getting data finished-------------------------------");


    }
}