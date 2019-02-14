package com.example.thermonitor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    Button button;
    EditText inputMail;
    EditText inputPassword;
    EditText inputConfirmPassword;
    EditText inputUsername;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        button = (Button) findViewById(R.id.button);
        inputMail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfirmPassword = (EditText) findViewById(R.id.confpassword);
        inputUsername = (EditText) findViewById(R.id.username);
        addListenerOnButton();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void addListenerOnButton() {
        final Context context = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputMail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confPassword = inputConfirmPassword.getText().toString().trim();


                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext() , "Please enter an e-mail" , Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext() , "Please enter a password" , Toast.LENGTH_SHORT).show();
                    return;

                }

                if(password.length() < 8) {
                    Toast.makeText(getApplicationContext() , "Password length is at least 8 characters" , Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!(password.equals(confPassword))) {
                    Toast.makeText(getApplicationContext() , "Passwords don't match" , Toast.LENGTH_SHORT).show();
                    return;
                }


                Toast.makeText(RegisterActivity.this , "Creating account ....." , Toast.LENGTH_SHORT).show();

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!(task.isSuccessful()))
                            Toast.makeText(RegisterActivity.this , task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                        if(task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this , "Successful" , Toast.LENGTH_SHORT).show();
                            addUser();
                            Intent intent = new Intent(context, ListActivity.class);
                            startActivity(intent);
                        }


                    }
                });


            }
        })


        ;}

        public void addUser () {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
            UserProfile user = new UserProfile(inputMail.getText().toString() , inputPassword.getText().toString() , inputUsername.getText().toString());
            myRef.setValue(user);

        }


}
