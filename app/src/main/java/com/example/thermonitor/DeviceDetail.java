package com.example.thermonitor;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DeviceDetail extends AppCompatActivity {

    Button button;
    TextView text;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        button = (Button) findViewById(R.id.logout);
        text = (TextView) findViewById(R.id.textView);
        text.setMovementMethod(new ScrollingMovementMethod());      //text view is scrollable

        //text.append(DeviceListActivity.espName);

        firebaseAuth = FirebaseAuth.getInstance();
        retrieveData();
        addListenerOnButton();
    }
    public void addListenerOnButton() {
        final Context context = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finishAffinity();       //closes completely the app

            }
        });
    }

    public void retrieveData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(DeviceListActivity.espName);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String temperature = dataSnapshot.getValue().toString();
                text.append("Temperature value in celsius = " + temperature + '\n');
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DeviceDetail.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
