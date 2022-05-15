package ma.razzoukichaimae.ensiasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // drop down menu : départements
    private String[] departements = {"Génie Logiciel", "Informatique et aide à la décision", "Ingénierie des Systèmes Embarqués", "Langues et Communication", "Web and Mobile Engineering"};
    private AutoCompleteTextView listDepartements;
    private ArrayAdapter<String> adapterDepartements;
    private String departement;

    // return to login
    private TextView registerToLogin;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    String userID;
    public static final String tag="TAG";

    // formulaire inscription
    private EditText editTextFullName,editTextEmail,editTextPassword;
    private ConstraintLayout registerUser;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        // return to login
        registerToLogin = (TextView) findViewById(R.id.registerToLogin);
        registerToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        //Initialize Firebase Auth and store
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //initialisation des champs du formulaire
        editTextFullName=(EditText) findViewById(R.id.fullName);
        editTextEmail=(EditText) findViewById(R.id.email);
        editTextPassword=(EditText) findViewById(R.id.password);
        listDepartements = findViewById(R.id.list_departements);

        // récuperation du dépatement sélectionné
        adapterDepartements = new ArrayAdapter<String>(this, R.layout.list_departements, departements);
        listDepartements.setAdapter(adapterDepartements);
        listDepartements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                departement = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(), "departement : "+departement, Toast.LENGTH_SHORT).show();
            }
        });

        progressBar=(ProgressBar) findViewById(R.id.progressBar);

        // For registration button
        registerUser = (ConstraintLayout) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = editTextFullName.getText().toString();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (fullName.isEmpty()) {
                    editTextFullName.setError("full name is required");
                    editTextFullName.requestFocus();
                    return;
                }
                ////////////
                if (email.isEmpty()) {
                    editTextEmail.setError("email is required");
                    editTextEmail.requestFocus();
                    return;
                }

                /////////////////
                if (password.isEmpty()) {
                    editTextPassword.setError("password is required");
                    editTextPassword.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    editTextPassword.setError("min password length should be 6 characters!");
                    editTextPassword.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "User created", Toast.LENGTH_SHORT).show();
                        userID = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = firestore.collection("Professeur").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("fullName", fullName);
                        user.put("departement", departement);
                        user.put("email", email);
                        user.put("password", password);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(tag, "user profile is created for " + userID);
                            }
                        });
                        progressBar .setVisibility(View.GONE);
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}