package com.example.kuba.sloik;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Button placePickerButton;
    private final int PLACE_PICKER_REQUEST = 1;
    FloatingActionButton fab;

    private DatabaseReference mDatabese;
    private DatabaseReference mDatabase;
    private final int RB_1 = 1001;
    private final int RB_2 = 1002;
    private final int RB_3 = 1003;

    private List<JarClass> jarList;
    private ArrayList<UserClass> userList;

    private String userID;
    private UserClass userClassId;

    private TextView userName;
    private TextView userEmail;
    //for choosing date while adding new jar
    private Button mDateDisplayButton;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    final Context context = this;
    private int jarId = 0;
    private int session_id = 1;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        jarList = new ArrayList<>();

        mDatabese = FirebaseDatabase.getInstance().getReference("jars");

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

                setJarButtonIds(dialog);

                placePickerButton = (Button) dialog.findViewById(R.id.selectPlaceButton);
                placePickerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                    try {
                        startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    }
                });

                mDateDisplayButton = (Button) dialog.findViewById(R.id.jarDateButton);
                mDateDisplayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar c = Calendar.getInstance();
                        int year = c.get(Calendar.YEAR);
                        int month = c.get(Calendar.MONTH);
                        int day = c.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                MainActivity.this,
                                R.style.MyDatePickerDialogTheme,
                                mDateSetListener,
                                year, month, day);

                        datePickerDialog.show();
                    }
                });

                mDateSetListener = new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, month);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String date = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());
                        mDateDisplayButton.setText(date);
                    }
                };

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
                        String date = ((Button)(dialog.findViewById(R.id.jarDateButton))).getText().toString();
                        String latitude = String.valueOf(Wrapper.place.getLatLng().latitude);
                        String longitude = String.valueOf(Wrapper.place.getLatLng().longitude);

                        // Creating new user node, which returns the unique key value
                        // new user node would be /users/$userid/
                        String jarId = mDatabese.push().getKey();

                        JarClass jar = new JarClass(String.valueOf(jarId), size,name,description,date,latitude,longitude);

                        mDatabese.child(jarId).setValue(jar);

                        Toast.makeText(context, "Słoik " + name + " dodany", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();


                    }
                });

            }
        });
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Wrapper.place = PlacePicker.getPlace(data, this);

                //unnecessary toast giving some information
                String toastMsg = String.format("Place: %s", Wrapper.place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

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
        CameraUpdateFactory.zoomTo(8.0f);


        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);



        mDatabese.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot jarSnapshot : dataSnapshot.getChildren()){
                    JarClass jar = jarSnapshot.getValue(JarClass.class);
                    LatLng coords = new LatLng(Double.valueOf(jar.getLatitude()), Double.valueOf(jar.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(coords).title(jar.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    jarList.add(jar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("TEST","wrong");
            }
        });
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 //Get Post object and use the values to update the UI
                JarClass jar = dataSnapshot.getValue(JarClass.class);
                LatLng jarPosition = new LatLng(Integer.valueOf(jar.latitude), 15);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    void setJarButtonIds(Dialog dialog){
        dialog.findViewById(R.id.smallJarButton).setId(RB_1);
        dialog.findViewById(R.id.mediumJarButton).setId(RB_2);
        dialog.findViewById(R.id.bigJarButton).setId(RB_3);
    }


}