package ma.razzoukichaimae.ensiasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextView loginToRegister;
    private ConstraintLayout login;

    private EditText email, password;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth=FirebaseAuth.getInstance();

        login = findViewById(R.id.signIn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

/*        loginToRegister = (TextView) findViewById(R.id.register);
        loginToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });*/
    }

    private void loginUser() {

        String emailContent = email.getText().toString().trim();
        String passwordContent = password.getText().toString().trim();

        if(emailContent.isEmpty()) {
            email.setError("Email is required !");
            email.requestFocus();
            return;
        }
        if(passwordContent.isEmpty()) {
            password.setError("Password is required !");
            password.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        //check if it's the admin
        if(emailContent.equals("admin@gmail.com") && passwordContent.equals("admin123")){
            startActivity(new Intent(LoginActivity.this, MenuAdminActivity.class));
            progressBar.setVisibility(View.GONE);
        }else{
        mAuth.signInWithEmailAndPassword(emailContent,passwordContent).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

                    /**Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                     intent.putExtra("userID", "");
                     startActivity(intent);**/

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString("userID", String.valueOf(user.getUid()));
                    edit.commit();

                    //redirect to user profile
                    //startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    //startActivity(new Intent(LoginActivity.this, MessagesProfessorsActivity.class));
                    startActivity(new Intent(LoginActivity.this, ProfesseurActivity.class));
                    progressBar.setVisibility(View.GONE);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Failed to login! please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
        }
    }
}