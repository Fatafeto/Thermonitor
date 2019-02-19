package com.example.thermonitor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    Button button1;
    Button button2;
    EditText inputMail;
    EditText inputPassword;
    EditText inputUsername;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ProgressDialog progressDialog;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        inputMail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputUsername = (EditText) findViewById(R.id.username);
        progressDialog = new ProgressDialog(this);
        addListenerOnButton();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            Intent intent = new Intent(LoginActivity.this, DeviceListActivity.class);
            startActivity(intent);
        }
    }

    public void addListenerOnButton() {
        final Context context = this;

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputMail.getText().toString();
                final String password = inputPassword.getText().toString();
                String username = inputUsername.getText().toString();

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext() , "Please enter an e-mail" , Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext() , "Please enter a password" , Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext() , "Please enter a username" , Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length() < 8) {
                    Toast.makeText(getApplicationContext() , "Password is at least 8 characters" , Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Signing in ...");
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!(task.isSuccessful())) {
                            progressDialog.hide();
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext() , "Successfully signed in" , Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, DeviceListActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterActivity.class);
                startActivity(intent);


            }
        })



        ;}
}
