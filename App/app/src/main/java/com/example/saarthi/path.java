package com.example.saarthi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class path extends AppCompatActivity {


    EditText src,dst;
    Spinner dropdown;
    ProgressBar p;
    Integer hold;
    private String server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);
        src = (EditText)findViewById(R.id.editSrc);
        src.setVisibility(View.INVISIBLE);
        dst = (EditText)findViewById(R.id.editDst);
        dst.setVisibility(View.INVISIBLE);
        p = (ProgressBar)findViewById(R.id.progressBar1);
        p.setVisibility(View.INVISIBLE);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        server =   getResources().getString(R.string.server);
        dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Safe For General", "Patient", "Noiseless","Kids fun", "Greenery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                hold = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        String apiKey = getString(R.string.api_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setHint("Enter source");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                src.setText(place.getAddress());
            }

            @Override
            public void onError(Status status) {

            }
        });

        final AutocompleteSupportFragment autocompleteFragment1 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment1);
        autocompleteFragment1.setHint("Enter Destination");
        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                dst.setText(place.getAddress());

                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                // Log.i(TAG, "An error occurred: " + status);
            }
        });
    }



    public void onClickpostButton(View v){
//        p.setVisibility(View.VISIBLE);
//        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//        //   String rname = name.getText().toString();
        String srct = src.getText().toString();
        String dstt = dst.getText().toString();
     //   String url = server + "path/";
        String  mode = hold.toString();

        Intent intent = new Intent(this, Desire.class);
        Bundle extras = new Bundle();
        extras.putString("starts",srct);
         extras.putString("ends",dstt);
        extras.putString("modes",mode);
        intent.putExtras(extras);
        startActivity(intent);

//
//        RequestBody body = new FormBody.Builder()
//                .add("src", srct)
//                .add("dst", dstt)
//                .add("mod", mode)
//                .build();
//
//        OkHttpClient client1 = new OkHttpClient();
//        OkHttpClient client = client1.newBuilder()
//                .connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .build();
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        Toast.makeText(getApplicationContext(), "Hang On Tight", Toast.LENGTH_LONG).show();
//        //Call call = client.newCall(request);
//
//        client.newCall(request).enqueue(new Callback(){
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
//                runOnUiThread(new Runnable(){
//
//
//                    @Override
//                    public void run() {
//                        path.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try (ResponseBody responseBody = response.body()) {
//                                    Toast.makeText(getApplicationContext(), "Let's Go", Toast.LENGTH_LONG).show();
//                                    loadMap(responseBody.string());
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                //catch (IOException e) {
//
//                                   // e.printStackTrace();
//                                //} catch (JSONException e) {
//                                //    e.printStackTrace();
//                                //}
//                            }
//                        });
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//                e.printStackTrace();
//            }
//        });


    }

    public void loadMap(String response) throws JSONException,IOException
    {
        p.setVisibility(View.INVISIBLE);
        Uri gmmIntentUri = Uri.parse(response);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
