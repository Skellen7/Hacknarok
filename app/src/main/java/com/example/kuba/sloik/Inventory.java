package com.example.kuba.sloik;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.kuba.sloik.R.layout.one_jar_view;

public class Inventory extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout linearLayout;

    private ArrayList<JarClass> jarList;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle bundle = getIntent().getExtras();
        jarList = (ArrayList<JarClass>) bundle.getSerializable("jarList");

        for (JarClass jr : jarList) {
            Log.v("TEST", jr.name);
        }

        // adding one_jars to inventory in scroll view mode
        linearLayout = (LinearLayout) findViewById(R.id.inventory_content_container);
        for (int i = 0; i < jarList.size(); i++) {

            View oneJar = getLayoutInflater().inflate(R.layout.one_jar_view, null);
            oneJar.setId(i);
            TextView jarName = (TextView) oneJar.findViewById(R.id.my_jar_text);
            jarName.setText(jarList.get(i).name);
            oneJar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedOneJar(view);
                }
            });
            linearLayout.addView(oneJar);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    public void clickedOneJar(View view) {
        final Dialog productDialog = new Dialog(context);
        productDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        productDialog.setContentView(R.layout.product_info);
        productDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView big_jar = productDialog.findViewById(R.id.jar_big_icon);
        ImageView medium_jar = productDialog.findViewById(R.id.jar_medium_icon);
        ImageView small_jar = productDialog.findViewById(R.id.jar_small_icon);

        ImageView jar_img = productDialog.findViewById(R.id.mainJar);
        TextView title = productDialog.findViewById(R.id.jarTitle);
        TextView description = productDialog.findViewById(R.id.description);
        TextView date = productDialog.findViewById(R.id.jar_date);

        JarClass jar = jarList.get(view.getId());
        title.setText(jar.name);
        description.setText(jar.description);
        date.setText(jar.date);


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
    }


    public void deleteJar(View view) {
        linearLayout.removeViewAt((((RelativeLayout) view.getParent()).getId()));
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
            startActivity(new Intent(Inventory.this, MainActivity.class));
        } else if (id == R.id.nav_info) {
            startActivity(new Intent(Inventory.this, ProfileActivity.class));
        } else if (id == R.id.nav_inventory) {
            startActivity(new Intent(Inventory.this, Inventory.class));

        } else if (id == R.id.nav_list) {
            startActivity(new Intent(Inventory.this, JarList.class));

        } else if (id == R.id.nav_history) {
            startActivity(new Intent(Inventory.this, History.class));

        } else if (id == R.id.nav_share) {
            startActivity(new Intent(Inventory.this, ProfileActivity.class));

        } else if (id == R.id.nav_send) {
            startActivity(new Intent(Inventory.this, ProfileActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}