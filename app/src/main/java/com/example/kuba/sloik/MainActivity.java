package com.example.kuba.sloik;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private ArrayList<JarClass> jarList;
    private ArrayList<UserClass> userList;

    private String userID;
    private UserClass userClassId;

    private TextView userName;
    private TextView userEmail;
    //for choosing date while adding new jar
    private Button mDateDisplayButton;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    final Context context = this;
    private String session_id = "23";

    private FusedLocationProviderClient mFusedLocationClient;

    //add photo
    Uri globalPhoto;
    private StorageReference mStorage;
    static final int REQUEST_IMAGE = 2;
    String mCurrentPhotoPath;


    //permisions
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        jarList = new ArrayList<>();

        mDatabese = FirebaseDatabase.getInstance().getReference("jars");
        mStorage = FirebaseStorage.getInstance().getReference();

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
                final String jarId = mDatabese.push().getKey();
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

                mDateSetListener = new DatePickerDialog.OnDateSetListener() {
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
                        String name = ((EditText) (dialog.findViewById(R.id.jarName))).getText().toString();
                        String description = ((EditText) (dialog.findViewById(R.id.jarDescription))).getText().toString();
                        String date = ((Button) (dialog.findViewById(R.id.jarDateButton))).getText().toString();
                        String latitude = String.valueOf(Wrapper.place.getLatLng().latitude);
                        String longitude = String.valueOf(Wrapper.place.getLatLng().longitude);

                        // Creating new user node, which returns the unique key value
                        // new user node would be /users/$userid/

                        JarClass jar = new JarClass(String.valueOf(jarId), session_id, size, name, description, date, latitude, longitude);

                        mDatabese.child(jarId).setValue(jar);

                        Toast.makeText(context, "Słoik " + name + " dodany", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();


                    }
                });
                Button addPhotoButton = (Button) dialog.findViewById(R.id.addPhotoButton);
                addPhotoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchTakePictureIntent(jarId);
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
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Wrapper.place = PlacePicker.getPlace(data, this);
                //unnecessary toast giving some information
                String toastMsg = String.format("Lokalizacja: %s", Wrapper.place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK){
            //Uri uri = data.getData();
            StorageReference filepath = mStorage.child("Photos").child(globalPhoto.getLastPathSegment());
            filepath.putFile(globalPhoto);
        }

    }



    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserClass user = userSnapshot.getValue(UserClass.class);
                    if (user.getEmail().toString().equals(userID)) {

                        userClassId = user;
                        afterDB();
                    }
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

        if (id == R.id.nav_home) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        } else if (id == R.id.nav_info) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        } else if (id == R.id.nav_inventory) {
            Intent i = new Intent(MainActivity.this, Inventory.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("jarList", jarList);
            i.putExtras(bundle);
            startActivity(i);
        } else if (id == R.id.nav_list) {
            startActivity(new Intent(MainActivity.this, JarList.class));

        } else if (id == R.id.nav_history) {
            startActivity(new Intent(MainActivity.this, History.class));

        } else if (id == R.id.nav_share) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));

        } else if (id == R.id.nav_send) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));

        } else if (id == R.id.nav_offers) {
            startActivity(new Intent(MainActivity.this, OffersActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final Dialog productDialog = new Dialog(context);
        productDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        productDialog.setContentView(R.layout.product_info);
        productDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView jarTitle = productDialog.findViewById(R.id.jarTitle);
        TextView jarDescription = productDialog.findViewById(R.id.description);
        TextView jarDate = productDialog.findViewById(R.id.jar_date);
        ImageView jarSize;
        final ImageView jarPhoto = productDialog.findViewById(R.id.mainJar);

        for(JarClass jar : jarList){
            if(jar.getJarId().equals(marker.getTag())){
                Log.v("TEST", jar.getName());
                jarTitle.setText(jar.getName());
                jarDescription.setText(jar.getDescription());
                jarDate.setText(jar.getDate());

        ImageView big_jar = productDialog.findViewById(R.id.jar_big_icon);
        ImageView medium_jar = productDialog.findViewById(R.id.jar_medium_icon);
        ImageView small_jar = productDialog.findViewById(R.id.jar_small_icon);
              
                mStorage.child("Photos").child(jar.getJarId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri_) {
                        Log.v("URI", uri_.toString());
                        Wrapper.uri = uri_;
                        // Got the download URL for 'users/me/profile.png'
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

                Glide.with(this).load(Wrapper.uri).into(jarPhoto);

                switch(jar.getSize()){
                    case "1001":
                        jarSize = productDialog.findViewById(R.id.jar_small_icon);
                        jarSize.setImageResource(R.drawable.ic_jar_of_jam_small);
                        break;
                    case "1002":
                        jarSize = productDialog.findViewById(R.id.jar_medium_icon);
                        jarSize.setImageResource(R.drawable.ic_jar_of_jam_medium);
                        break;
                    case "1003":
                        jarSize = productDialog.findViewById(R.id.jar_big_icon);
                        jarSize.setImageResource(R.drawable.ic_jar_of_jam_big);
                        break;
                }
                break;
            }
        }

        Button back = (Button) productDialog.findViewById(R.id.product_back);
        Button exchange = (Button) productDialog.findViewById(R.id.product_exchange);

        productDialog.show();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productDialog.dismiss();
            }
        });

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productDialog.cancel();
                final Dialog exchangeDialog = new Dialog(context);
                exchangeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                exchangeDialog.setContentView(R.layout.exchange_dialog);
                exchangeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                exchangeDialog.show();



            }
        });

        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocationPermissionAndSetMapView();

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);


        mDatabese.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot jarSnapshot : dataSnapshot.getChildren()) {
                    JarClass jar = jarSnapshot.getValue(JarClass.class);
                    LatLng coords = new LatLng(Double.valueOf(jar.getLatitude()), Double.valueOf(jar.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(coords).title(jar.getName())
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.jar_marker)))
                            .setTag(jar.getJarId());
                    jarList.add(jar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("TEST", "wrong");
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

    private void setMapView() {
        Location location = null;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            location = bestLocation;
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location == null ? 50.0262494 : location.getLatitude(),
                location == null ? 19.951888 : location.getLongitude()), 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location == null ? 50.0262494 : location.getLatitude(),
                        location == null ? 19.951888 : location.getLongitude()))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to north
                .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    void setJarButtonIds(Dialog dialog){
        dialog.findViewById(R.id.smallJarButton).setId(RB_1);
        dialog.findViewById(R.id.mediumJarButton).setId(RB_2);
        dialog.findViewById(R.id.bigJarButton).setId(RB_3);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                setMapView();
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void getLocationPermissionAndSetMapView() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            setMapView();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void dispatchTakePictureIntent(String jarId) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(jarId);
            } catch (IOException ex) {
                // Error occurred while creating the File...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                globalPhoto = photoURI;

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }

    private File createImageFile(String jarID) throws IOException {
        // Create an image file name
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, jarID);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        String t = image.getName();
        Log.v("test", t);
        //writeNewUrl(t,4);
        return image;
    }
    private boolean cameraExist(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }
}