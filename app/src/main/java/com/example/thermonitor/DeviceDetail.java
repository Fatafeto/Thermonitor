package com.example.thermonitor;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

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
        text.setMovementMethod(new ScrollingMovementMethod());

        firebaseAuth = FirebaseAuth.getInstance();
        addListenerOnButton();
    }
//manage logout
    public void addListenerOnButton() {
        final Context context = this;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finishAffinity();

            }
        });
    }

}
