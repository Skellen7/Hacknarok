package com.example.kuba.sloik;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    FloatingActionButton fab;

    private DatabaseReference mDatabase;
        private DatabaseReference mRef;


    final Context context = this;
    private int uID = 0;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//DUPA
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRef = mDatabase;


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.activity_sloik_add);
                dialog.setTitle("Title...");
                dialog.show();

                Button returnButton = (Button) dialog.findViewById(R.id.returnButton);
                returnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button addButton = (Button) dialog.findViewById(R.id.addButton);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton = (RadioButton) findViewById(selectedId);

                        String size = Integer.toString(selectedId);
                        String name = ((EditText)(dialog.findViewById(R.id.jarName))).getText().toString();
                        String description = ((EditText)(dialog.findViewById(R.id.jarDescription))).getText().toString();
                        String date = ((EditText)(dialog.findViewById(R.id.jarDate))).getText().toString();
                        String latitude = "test";
                        String longitude = "test";

                        JarClass jar = new JarClass(size,name,description,date,latitude,longitude);
                        mDatabase.child("jars").child(getId()).setValue(jar);
                    }
                });
            }
        });


    }

    private String getId() {
        uID += 1;
        return Integer.toString(uID);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_info) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        } else if (id == R.id.nav_inventory) {
            startActivity(new Intent(MainActivity.this, Inventory.class));

        } else if (id == R.id.nav_list) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));

        } else if (id == R.id.nav_history) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));

        } else if (id == R.id.nav_share) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));

        } else if (id == R.id.nav_send) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.product_info);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView big_jar = dialog.findViewById(R.id.jar_big_icon);
        ImageView medium_jar = dialog.findViewById(R.id.jar_medium_icon);
        ImageView small_jar = dialog.findViewById(R.id.jar_small_icon);

        ImageView jar_img = dialog.findViewById(R.id.mainJar);
        TextView title = dialog.findViewById(R.id.jarTitle);
        TextView description = dialog.findViewById(R.id.description);
        TextView date = dialog.findViewById(R.id.jar_date);

        //if(jar.size=="small") small_jar.setImageResource(R.drawable.ic_jar_of_jam_small);
        //else if(jar.size=="medium") medium_jar.setImageResource(R.drawable.ic_jar_of_jam_medium);
        //else big_jar.setImageResource(R.drawable.ic_jar_of_jam_big);

        //jar_img.setImageBitmap(jar.img);
        //title.setText(jar.title);
        //description.setText(jar.description);
        //date.setText(jar.date);

        Button back = (Button)dialog.findViewById(R.id.product_back);

        dialog.show();

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });

        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);

        // Add a marker in Sydney and move the camera


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 //Get Post object and use the values to update the UI
                JarClass jar = dataSnapshot.getValue(JarClass.class);
                Log.v("TEST",jar.description);
                LatLng jarPosition = new LatLng(Integer.valueOf(jar.latitude), 15);

                mMap.addMarker(new MarkerOptions().position(jarPosition).title(jar.name));
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRef.child("").addValueEventListener(postListener);


        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0,0)));
    }



}
