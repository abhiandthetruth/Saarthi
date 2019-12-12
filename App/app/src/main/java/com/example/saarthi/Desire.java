package com.example.saarthi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.orhanobut.hawk.Hawk;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Desire extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener {



    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    private String server;
    JSONObject ress;
    Button r,b,g;
    TextView rt,bt,gt;

    String starts;
    String ends ;
    String modes ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        server = getResources().getString(R.string.server);
        ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desire);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();



        //getSupportFragmentManager().beginTransaction().add(R.id.desireFrame, new desireOptions()).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.desireFrame, new desireOptions()).commit();

        Intent intent = getIntent();
         starts = intent.getStringExtra("starts");
         ends = intent.getStringExtra("ends");
         modes = intent.getStringExtra("modes");
     //   Toast.makeText(getApplicationContext(), starts + ends + modes, Toast.LENGTH_LONG).show();
        //initialize();
        tryaddingpath();




    }

    void initialize(){
        r = (Button)findViewById(R.id.redButton);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = null;
                try {
                    gmmIntentUri = Uri.parse(ress.get("redLink").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        b = (Button)findViewById(R.id.blueButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = null;
                try {
                    gmmIntentUri = Uri.parse(ress.get("blueLink").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        g = (Button)findViewById(R.id.greenButton);
        g.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = null;
                try {
                    gmmIntentUri = Uri.parse(ress.get("greenLink").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }
    void tryaddingpath(){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //   String rname = name.getText().toString();

        String url = server + "path";


        RequestBody body = new FormBody.Builder()
                .add("src", starts)
                .add("dst", ends)
                .add("mod",modes)
                .build();

        OkHttpClient client1 = new OkHttpClient();
        OkHttpClient client = client1.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
     //   Toast.makeText(getApplicationContext(), "Trying to Login", Toast.LENGTH_LONG).show();
        //Call call = client.newCall(request);

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                runOnUiThread(new Runnable(){


                    @Override
                    public void run() {
                        Desire.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                   // Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_LONG).show();

                                    carryAddingAhead(response.body().string());

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
    private void carryAddingAhead(String r) throws JSONException {


        //Toast.makeText(getApplicationContext(), r, Toast.LENGTH_LONG).show();
        ress = new JSONObject(r);
        rt.setText(ress.get("redTime").toString());
        bt.setText(ress.get("blueTime").toString());
        gt.setText(ress.get("greenTime").toString());
        if(!(ress.get("startLat").toString().equals("~"))) {
           String lat = ress.get("startLat").toString();
            Double startLat = Double.valueOf(lat);
            String lang = ress.get("startLang").toString();
            Double startlang = Double.valueOf(lang);

            LatLng latLng = new LatLng(startLat, startlang);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .title("Startings Location"));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,
                    17);
            mMap.moveCamera(update);
        }
        if(!(ress.get("endLat").toString().equals("~"))) {
            String lat = ress.get("endLat").toString();
            Double endl = Double.valueOf(lat);
            String lang = ress.get("endtLang").toString();
            Double endll = Double.valueOf(lang);

            LatLng latLng = new LatLng(endl, endll);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .title("Endling Location"));
        }
        if(!(ress.get("red").toString().equals("~")))
            addpolyline(ress.get("red").toString(),1);

        if(!(ress.get("blue").toString().equals("~")))
            addpolyline(ress.get("blue").toString(),2);

        if(!(ress.get("green").toString().equals("~")))
            addpolyline(ress.get("green").toString(),3);

    }
    private void addpolyline(String ss,int col){



        if(col==2) {
            List<LatLng> polyLineList = decodePoly(ss);
            PolylineOptions lineOptions = new PolylineOptions();
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(polyLineList);
            lineOptions.width(10);
            lineOptions.color(Color.BLUE);
            mMap.addPolyline(lineOptions);
        }
        if(col==1) {
            List<LatLng> polyLineList = decodePoly(ss);
            PolylineOptions lineOptions = new PolylineOptions();
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(polyLineList);
            lineOptions.width(10);
            lineOptions.color(Color.RED);
            mMap.addPolyline(lineOptions);
        }
        if(col==3) {
            List<LatLng> polyLineList = decodePoly(ss);
            PolylineOptions lineOptions = new PolylineOptions();
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(polyLineList);
            lineOptions.width(10);
            lineOptions.color(Color.GREEN);
            mMap.addPolyline(lineOptions);
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
    //Getting current location
    private void getCurrentLocation() {

        initialize();
        rt = (TextView)findViewById(R.id.redtext);
        bt = (TextView)findViewById(R.id.bluetext);
        gt = (TextView)findViewById(R.id.greentext);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Toast.makeText(getApplicationContext(), "Permission please", Toast.LENGTH_LONG).show();
            return;
        }
        // Toast.makeText(getApplicationContext(), "Permission please", Toast.LENGTH_LONG).show();
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //  mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // googleMapOptions.mapType(googleMap.MAP_TYPE_HYBRID)
        //    .compassEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng india = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(india).title("Marker in India"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(india));
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
    }



    private void moveMap() {
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */

        LatLng latLng = new LatLng(latitude, longitude);


//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,
                17);
        mMap.moveCamera(update);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);




    }


    @Override
    public void onClick(View view) {
        //Log.v(TAG,"view click event");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        //Toast.makeText(MapsActivity.this, "onMarkerDragStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //Toast.makeText(MapsActivity.this, "onMarkerDrag", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // getting the Co-ordinates
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;

        //move to current position
        moveMap();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        String s = (String) marker.getTag();
        Intent intent = new Intent(getBaseContext(), displayParkikng.class);
        intent.putExtra("id", s);
        startActivity(intent);
        return true;
    }
}