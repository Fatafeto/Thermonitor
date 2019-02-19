package com.example.thermonitor;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;

public class DeviceListActivity extends AppCompatActivity {
    Button button1;
    Button button2;
    Button button3;
    ListView listView;
    ProgressDialog progressDialog;

    WifiManager wifiManager;
    LocationManager locationManager;
    boolean wifiStatus;
    boolean locationStatus;

    ArrayList<String> availableSSID = new ArrayList<String>();
    ArrayList<String> listElements = new ArrayList<String>();
    ArrayList<Integer> images = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        button1 = (Button) findViewById(R.id.enable);
        button2 = (Button) findViewById(R.id.disable);
        button3 = (Button) findViewById(R.id.scan);

        listView = (ListView)findViewById(R.id.listView);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        enableLocation();
        addListenerOnButton();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(view.getContext(),DeviceDetail.class);
                startActivity(myIntent);

            }

        });
    }

    public void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
        }
    }

    public void onBackPressed() {
        finishAffinity();
    }

    public void addListenerOnButton() {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wifiStatus = wifiManager.isWifiEnabled();
                locationStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (wifiStatus == false) {
                    wifiManager.setWifiEnabled(true);
                    Toast.makeText(getApplicationContext(), "Wi-Fi enabled", Toast.LENGTH_SHORT).show();
                }
                else if (wifiStatus == true) {
                    Toast.makeText(getApplicationContext(), "Wi-Fi is already enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wifiStatus = wifiManager.isWifiEnabled();
                locationStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (wifiStatus == true) {
                    wifiManager.setWifiEnabled(false);
                    Toast.makeText(getApplicationContext(), "Wi-Fi disabled", Toast.LENGTH_SHORT).show();
                }
                else if (wifiStatus == false) {
                    Toast.makeText(getApplicationContext(), "Wi-Fi is already disabled", Toast.LENGTH_SHORT).show();
                }

            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wifiStatus = wifiManager.isWifiEnabled();
                locationStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (wifiStatus == true && locationStatus == true) {
                    if(wifiManager.getScanResults() != null) {
                        listElements.clear();
                        images.clear();
                        CustomAdapter customAdapter = new CustomAdapter();
                        listView.setAdapter(customAdapter);
                    }
                    scanWiFi();
                }

               else  if (wifiStatus == false && locationStatus == true) {
                    Toast.makeText(getApplicationContext(), "Please enable Wi-Fi", Toast.LENGTH_SHORT).show();
                }

                else if (wifiStatus == true && locationStatus == false) {
                    Toast.makeText(getApplicationContext(), "Please turn on location", Toast.LENGTH_SHORT).show();
                    enableLocation();
                }

                else if(wifiStatus == false && locationStatus == false) {
                    Toast.makeText(getApplicationContext(), "Please enable location & WiFi", Toast.LENGTH_SHORT).show();
                    enableLocation();
                }
            }

        });
    }
    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view  = getLayoutInflater().inflate(R.layout.image_list , null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            TextView textView = (TextView) view.findViewById(R.id.textView);
            imageView.setImageResource(images.get(position));
            textView.setText(listElements.get(position));
            return view;
        }
    }

    public void enableLocation() {
        locationStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (locationStatus == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location is Disabled");
            builder.setMessage("Please enable location in settings.");
            builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }
    public void scanWiFi () {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(wifiReceiver , new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(getApplicationContext(), "Scanning for nearby devices.", Toast.LENGTH_SHORT).show();
    }

   BroadcastReceiver wifiReceiver = new BroadcastReceiver() {  //listens for wifi scan results
        @Override
        public void onReceive(Context context, Intent intent) {     //called when scanning finishes
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> results = wifiManager.getScanResults();
                if(results == null){
                    Toast.makeText(getApplicationContext(), "No devices available.", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (ScanResult scanResult : results) {    //not understood
                        availableSSID.add(scanResult.SSID);  //added all networks ssid
                    }

                    for (int i = 0; i<availableSSID.size(); i++) {
                        if (availableSSID.get(i).contains("ESP")) {
                            if(!(listElements.contains(availableSSID.get(i))))  {
                                listElements.add(availableSSID.get(i));
                                images.add(R.drawable.esp);
                            }
                        }
                    }

                    CustomAdapter customAdapter = new CustomAdapter();
                    listView.setAdapter(customAdapter);
                }
            }
        }

    };
}

