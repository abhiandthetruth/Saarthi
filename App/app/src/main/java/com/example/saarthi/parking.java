package com.example.saarthi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import static android.graphics.Bitmap.Config.ARGB_8888;
public class parking extends AppCompatActivity implements OnMapReadyCallback,
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        server = getResources().getString(R.string.server);
        ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

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
        getSupportFragmentManager().beginTransaction().add(R.id.parkingFrame, new parkingOpt()).commit();








    }


    @Override
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

    //Getting current location
    private void getCurrentLocation() {
        searchLocation();
        listentoButton();
        mMap.clear();
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

    public void listentoButton(){

        ImageButton parkingButton = (ImageButton) findViewById(R.id.RegisterP);
        parkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parking.this, parkingInput.class);
                startActivity(intent);
            }
        });


    }
    private void moveMap() {
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("Starting Location"));

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


        addParkingSpotstoMap();
        addPublicParkingSpotstoMap();

    }
    public void addPublicParkingSpotstoMap(){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //   String rname = name.getText().toString();

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+Double.toString(latitude)+","+Double.toString(longitude)+"&radius=500&types=parking&sensor=false&key=AIzaSyB6ky0s6kmaxH15hsxsNHKuZeI6n_OG2eA";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Toast.makeText(getApplicationContext(), "Loading Parking Spots nearby", Toast.LENGTH_LONG).show();
        //Call call = client.newCall(request);

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                runOnUiThread(new Runnable(){


                    @Override
                    public void run() {
                        parking.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    //Toast.makeText(getApplicationContext(), response.body().string(), Toast.LENGTH_LONG).show();
                                    afterGettingPublicParkingSpots(response.body().string());
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

    public void afterGettingPublicParkingSpots(String s) throws JSONException {

        JSONObject ress = new JSONObject(s);

        JSONArray lineItems = ress.getJSONArray("results");
        for(int i=0;i<lineItems.length();i++){
            JSONObject a = null;
            try {
                a = lineItems.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String Rating;
            String Name = a.getString("name");
            try {
                Rating = a.getString("rating");
            }
            catch (Exception JSONException){
                Rating = "2.5";
            }
            // Toast.makeText(getApplicationContext(),Name+  Rating, Toast.LENGTH_LONG).show();
            a= a.getJSONObject("geometry");
            a=a.getJSONObject("location");
            Double lat = Double.valueOf(a.getString("lat"));
            Double lng = Double.valueOf(a.getString("lng"));
            LatLng latLng = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .title(Name)
                    .snippet(Rating+"★")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            ).setTag("0");
        }
//    for (Object o : lineItems) {
//        JSONObject jsonLineItem = (JSONObject) o;
//        String key = jsonLineItem.getString("key");
//        String value = jsonLineItem.getString("value");
//        ...
//    }






    }
    private void  addParkingSpotstoMap(){


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //   String rname = name.getText().toString();

        String url = server + "parking";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        // Toast.makeText(getApplicationContext(), "Loading Parking Spots nearby", Toast.LENGTH_LONG).show();
        //Call call = client.newCall(request);

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                runOnUiThread(new Runnable(){


                    @Override
                    public void run() {
                        parking.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    afterGettingParkingSpots(response.body().string());
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
    private JSONArray jsonArray;

    private void afterGettingParkingSpots(String s) throws JSONException {

        jsonArray = new JSONArray(s);

//        int height = 100;
//        int width = 100;
//        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.mipmap.marker);
//        Bitmap b=bitmapdraw.getBitmap();
//        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


        for (int i = 0; i < jsonArray.length(); i++) {


            JSONObject a = jsonArray.getJSONObject(i);


            //Toast.makeText(getApplicationContext(), a.get("heavy").toString(), Toast.LENGTH_LONG).show();
            Bitmap bitmap = makeBitmap(this, a.get("price").toString());
            LatLng Loc1 = new LatLng(Double.parseDouble(a.get("lat").toString()), Double.parseDouble(a.get("lang").toString()));
            Marker Loc11 = mMap.addMarker(new MarkerOptions()
                    .position(Loc1)
                    .title(a.get("name").toString())
                    .snippet(a.get("price").toString())
                    // .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

            Loc11.setTag(a.get("_id").toString());





        }

        mMap.setOnMarkerClickListener(this);
        listentoSwitch();
    }



    public Bitmap makeBitmap(Context context, String text)
    {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.mmm);
        bitmap = bitmap.copy(ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK); // Text color
        paint.setTextSize(17 * scale); // Text size
        paint.setShadowLayer(2f, 0f, 1f, Color.WHITE); // Text shadow
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x =  bounds.width() - 15; // 10 for padding from right
        int y = bounds.height()+40;
        canvas.drawText("₹"+ text, x, y, paint);

        return  bitmap;
    }
    private void listentoSwitch(){
        final Switch mySwitch = (Switch)findViewById(R.id.swtchH);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMap.clear();
                if(mySwitch.isChecked()){
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject a = null;
                        try {
                            a = jsonArray.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            if(a.get("heavy").toString().equals("true")) {
                                Bitmap bitmap = makeBitmap(parking.this, a.get("price").toString());
                                LatLng Loc1 = new LatLng(Double.parseDouble(a.get("lat").toString()), Double.parseDouble(a.get("lang").toString()));
                                Marker Loc11 = mMap.addMarker(new MarkerOptions()
                                        .position(Loc1)
                                        .title(a.get("name").toString())
                                        .snippet(a.get("price").toString())
                                        // .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                                Loc11.setTag(a.get("_id").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }else{
                    addPublicParkingSpotstoMap();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject a = null;
                        try {
                            a = jsonArray.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            Bitmap bitmap = makeBitmap(parking.this, a.get("price").toString());
                            LatLng Loc1 = new LatLng(Double.parseDouble(a.get("lat").toString()), Double.parseDouble(a.get("lang").toString()));
                            Marker Loc11 = mMap.addMarker(new MarkerOptions()
                                    .position(Loc1)
                                    .title(a.get("name").toString())
                                    .snippet(a.get("price").toString())
                                    // .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));

                            Loc11.setTag(a.get("_id").toString());

                        }catch (JSONException e){

                        }

                    }



                }
            }
        });


    }





    public void searchLocation() {

        SearchView locationSearch = (SearchView) findViewById(R.id.searchView);

        final Geocoder geocoder = new Geocoder(this);
        locationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            List<Address> addressList;

            public boolean onQueryTextSubmit(String s) {
                if (s != null || !s.equals("")) {
                    Address address;
                    address=null;
                    try {

                        address = geocoder.getFromLocationName(s, 1).get(0);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(address!=null) {
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(s));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Cannot Connect to server",Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

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
        if(!((String)marker.getTag()).equals("0")) {

            String s = (String) marker.getTag();
            Intent intent = new Intent(getBaseContext(), displayParkikng.class);
            intent.putExtra("id", s);
            startActivity(intent);
            return true;
        }

        return false;
    }
}