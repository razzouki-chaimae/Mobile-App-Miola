package ma.razzoukichaimae.ensiasapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import model.Etudiant;
import model.Message;

public class ProfesseurActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    FirebaseFirestore firestore;

    RecyclerView recyclerviewMessages;
    MessagesAdapter recyclerViewAdapter;
    LinkedList<Message> messagesList;
    ProgressDialog progressDialog;

    RecyclerView recyclerViewEtudiants;
    EtudiantsAdapterForProf recyclerViewEtudiantsAdapter;
    LinkedList<Etudiant> etudiantsList;

    LinearLayout addMessageLayout;

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
                LinkedList<Etudiant> filteredList = new LinkedList<>();

                // running a for loop to compare elements.
                for (Etudiant item : etudiantsList) {
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
                    Toast.makeText(ProfesseurActivity.this, "No Data Found..", Toast.LENGTH_SHORT).show();
                } else {
                    // at last we are passing that filtered
                    // list to our adapter class.
                    recyclerViewEtudiantsAdapter.filterList(filteredList);
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
        setContentView(R.layout.activity_professeur);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.messageItem);
    }

    MessagesFragment messagesFragment = new MessagesFragment();
    StudentsFragment studentsFragment = new StudentsFragment();
    ScheduleFragment scheduleFragment = new ScheduleFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){

        ImageView profImg = findViewById(R.id.profImg);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userID = preferences.getString("userID", "n/a");
        firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("Professeur").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                /*name.setText(value.getString("fullName"));
                dep.setText(value.getString("departement"));
                mail.setText(value.getString("email"));
                userid.setText(userID);*/

                StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/professeurs/"+userID+".png");

                try {
                    File localFile = File.createTempFile("tempfile", ".png");
                    storageReference.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    profImg.setImageBitmap(bitmap);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Toast.makeText(ProfesseurActivity.this, "Exception : "+e, Toast.LENGTH_LONG).show();
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        switch (item.getItemId()){
            case R.id.messageItem:
                getSupportActionBar().hide();

                addMessageLayout = findViewById(R.id.addmessage);
                addMessageLayout.setVisibility(View.VISIBLE);
                addMessageLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ProfesseurActivity.this, WritingMessageActivity.class));
                    }
                });

                recyclerviewMessages = findViewById(R.id.recyclerviewMessages);
                recyclerviewMessages.setHasFixedSize(true);
                recyclerviewMessages.setLayoutManager(new LinearLayoutManager(ProfesseurActivity.this));

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

                getSupportFragmentManager().beginTransaction().replace(R.id.container, messagesFragment).commit();
                return true;

            case R.id.studentItem:
                getSupportActionBar().show();

                addMessageLayout = findViewById(R.id.addmessage);
                addMessageLayout.setVisibility(View.GONE);

                recyclerViewEtudiants = findViewById(R.id.recyclerviewMessages);
                recyclerViewEtudiants.setHasFixedSize(true);
                recyclerViewEtudiants.setLayoutManager(new LinearLayoutManager(ProfesseurActivity.this));

                etudiantsList = new LinkedList<>();

                firestore = FirebaseFirestore.getInstance();

                recyclerViewEtudiantsAdapter = new EtudiantsAdapterForProf(this, etudiantsList);
                recyclerViewEtudiants.setAdapter(recyclerViewEtudiantsAdapter);
                /************************************************************************************/

                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                //displaying progress dialog while fetching images
                progressDialog.setMessage("Fetching data...");
                progressDialog.show();

                System.out.println("1) Getting students ..................................................................................................");

                firestore.collection("Etudiant").orderBy("fullName", Query.Direction.ASCENDING)
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
                                        System.out.println("2) Etudiant n° "+i+"-------------------------------------------------------------------------");

                                        etudiantsList.add(dc.getDocument().toObject(Etudiant.class));
                                    }
                                    recyclerViewEtudiantsAdapter.notifyDataSetChanged();

                                    if(progressDialog.isShowing())  progressDialog.dismiss();
                                }
                            }
                        });

                System.out.println("-------------------------------Getting data finished-------------------------------");

                getSupportFragmentManager().beginTransaction().replace(R.id.container, studentsFragment).commit();
                return true;

            case R.id.scheduleItem:
                String getUrl = "https://firebasestorage.googleapis.com/v0/b/miola-application.appspot.com/o/schedules%2F1652550598522.pdf?alt=media&token=784e072f-4589-4e7d-beb3-6ec8e777ca7e";
                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(getUrl));
                String title = URLUtil.guessFileName(getUrl,null,null);
                request.setTitle(title);
                request.setDescription("Dowloading file please .........");
                String cookies = CookieManager.getInstance().getCookie(getUrl);
                request.addRequestHeader("cookie",cookies);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title);
                DownloadManager downloadManager=(DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
                Toast.makeText(ProfesseurActivity.this, "Téléchargement en cours...", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}