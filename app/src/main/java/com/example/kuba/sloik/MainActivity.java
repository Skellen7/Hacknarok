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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FloatingActionButton fab;

    private DatabaseReference mDatabese;
    private DatabaseReference mDatabase;

    private List<JarClass> jarList;
    private ArrayList<UserClass> userList;

    private String userID;
    private UserClass userClassId;

    private TextView userName;
    private TextView userEmail;


    final Context context = this;
    private int uID = 0;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userID = getIntent().getStringExtra("USER_ID");

        mDatabese = FirebaseDatabase.getInstance().getReference("jars");
        mDatabase = FirebaseDatabase.getInstance().getReference("users");


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
                        String name = ((EditText) (dialog.findViewById(R.id.jarName))).getText().toString();
                        String description = ((EditText) (dialog.findViewById(R.id.jarDescription))).getText().toString();
                        String date = ((EditText) (dialog.findViewById(R.id.jarDate))).getText().toString();
                        String latitude = "test";
                        String longitude = "test";

                        // Creating new user node, which returns the unique key value
                        // new user node would be /users/$userid/
                        String jarId = mDatabese.push().getKey();

                        JarClass jar = new JarClass(size, name, description, date, latitude, longitude);

                        mDatabese.child(jarId).setValue(jar);

                        Toast.makeText(context, "Słoik " + name + " dodany", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();


                    }
                });

            }
        });
        jarList = new ArrayList<>();
        userList = new ArrayList<>();


    }

    private UserClass getUser() {
        for (UserClass user : userList) {
            if (user.getEmail().toString().equals(userID)) {
                return user;
            }
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabese.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot jarSnapshot : dataSnapshot.getChildren()) {
                    JarClass jar = jarSnapshot.getValue(JarClass.class);
                    jarList.add(jar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserClass user= userSnapshot.getValue(UserClass.class);
                    if (user.getEmail().toString().equals(userID)) {

                        userClassId = user;
                    afterDB();}
                    userList.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void afterDB() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        userEmail = (TextView) headerView.findViewById(R.id.current_user_email);
        userEmail.setText(userID);
        userName = (TextView) headerView.findViewById(R.id.current_user);
        userName.setText(userClassId.getUsername());
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
            startActivity(new Intent(MainActivity.this, JarList.class));

        } else if (id == R.id.nav_history) {
            startActivity(new Intent(MainActivity.this, History.class));

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

        Button back = (Button) dialog.findViewById(R.id.product_back);

        dialog.show();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CameraUpdateFactory.zoomTo(8.0f);


        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);

        LatLng m1 = new LatLng(50.021842, 19.887334);
        LatLng m2 = new LatLng(50.019360, 19.881583);
        LatLng m3 = new LatLng(50.016795, 19.879395);
        LatLng m4 = new LatLng(50.018725, 19.885661);
        mMap.addMarker(new MarkerOptions().position(m1).title("Słoik 1"));
        mMap.addMarker(new MarkerOptions().position(m2).title("Słoik 2"));
        mMap.addMarker(new MarkerOptions().position(m3).title("Słoik 3"));
        mMap.addMarker(new MarkerOptions().position(m4).title("Słoik 4"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(m1, 12));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(m1));

    }
}