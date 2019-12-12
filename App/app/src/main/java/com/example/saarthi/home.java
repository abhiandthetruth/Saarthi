package com.example.saarthi;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.SystemUpdatePolicy;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.CountDownTimer;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.orhanobut.hawk.Hawk;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        server =   getResources().getString(R.string.server);;
        getSupportFragmentManager().beginTransaction().add(R.id.mainFrame,new HomeFragment()).commit();

        Intent intent = getIntent();

        Toast t = Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT);
        t.show();
        updateNameandPhoneofUser();

    }

      public void updateNameandPhoneofUser(){
          MediaType JSON = MediaType.parse("application/json; charset=utf-8");
          //   String rname = name.getText().toString();

          String url = server + "auth/me";

          Hawk.init(getApplicationContext()).build();




          OkHttpClient client = new OkHttpClient();

          Request request = new Request.Builder()
                  .url(url)
                  .header("x-access-token", Hawk.get("token").toString())

                  .build();

          //Toast.makeText(getApplicationContext(), "Trying to Login", Toast.LENGTH_LONG).show();
          //Call call = client.newCall(request);

          client.newCall(request).enqueue(new Callback(){

              @Override
              public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                  runOnUiThread(new Runnable(){


                      @Override
                      public void run() {
                          home.this.runOnUiThread(new Runnable() {
                              @Override
                              public void run() {

                                  try {
                                      carryItAhead(response.body().string());
                                  } catch (IOException e) {

                                      e.printStackTrace();
                                  } catch (JSONException e) {
                                      e.printStackTrace();
                                  }
                              }
                          });
                      }
                  });
              }

              @Override
              public void onFailure(@NotNull Call call, @NotNull IOException e) {

                  e.printStackTrace();
              }
          });


      }

    public void carryItAhead(String s) throws JSONException {
//        new CountDownTimer(30000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//              //  mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
//            }
//
//            public void onFinish() {
//              //  mTextField.setText("done!");
//            }
//        }.start();
        JSONObject ress = new JSONObject(s);
        // Toast.makeText(getApplicationContext(), ress.get("firstName").toString(), Toast.LENGTH_LONG).show();
        String name = (String) ress.get("firstName");
        String phone = (String) ress.get("phone");
        try{

//            int t=10000;
//            while(t>0){
//                t=t-1;
//            }
            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            TextView title = headerView.findViewById(R.id.viewNameUser);
            TextView subTitle = headerView.findViewById(R.id.viewPhoneUser);
             subTitle.setText(phone);
  title.setText(name);
        }
        catch (Exception ss){

        }
            //




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }





        public void logout(){

            Hawk.init(getApplicationContext()).build();
            Intent intent = new Intent(this, MainActivity.class);

            Hawk.delete("token");
            startActivity(intent);
            finish();
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
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,new HomeFragment()).commit();

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        else if (id == R.id.nav_logout) {
            logout();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


//     Location currentLocation;
//    FusedLocationProviderClient fusedLocationProviderClient;
//    private static final int REQUEST_CODE = 101;
//
//
    public void onParkingButtonClick(View v){

        Intent intent = new Intent(this, parking.class);
        startActivity(intent);
    }

    public void onPathButtonClick(View v){
        Intent intent = new Intent(this, path.class);
        startActivity(intent);
    }

    public void onQuickViewClick(View view){
    Intent intent = new Intent(this, QuickView.class);
        startActivity(intent);
    }
//    private void fetchLastLocation(){
//
//        Task<Location> task = fusedLocationProviderClient.getLastLocation();
//        task.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if(location != null ){
//                    currentLocation = location;
//                    Toast.makeText(getApplicationContext(),currentLocation.getLatitude()+" ",Toast.LENGTH_SHORT).show();
//                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById((R.id.parkingmap));
//                             supportMapFragment.getMapAsync( home.this);
//                }
//            }
//        });
//
//    }

}
