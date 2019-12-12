package com.example.saarthi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class QuickView extends AppCompatActivity {
    EditText src;

    private String server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        server =   getResources().getString(R.string.server);
          src = (EditText)findViewById(R.id.editOrg);
          src.setVisibility(View.INVISIBLE);
        String apiKey = getString(R.string.api_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment0);
        autocompleteFragment.setHint("Enter source");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                src.setText(place.getName() +" " +place.getAddress());
            }

            @Override
            public void onError(Status status) {

            }
        });
        Button b = (Button)(findViewById(R.id.viewbutton));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = server + "quick";


                RequestBody body = new FormBody.Builder()
                        .add("name", String.valueOf(src.getText()))
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
                                QuickView.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            String message = response.body().string();
                                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(QuickView.this, quicklist.class);
                                            intent.putExtra("json", message);
                                    startActivity(intent);

                                        }
                                        catch (IOException e) {

                                            e.printStackTrace();
                                        }
//                                catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
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
        });
    }

    public void onQuickViewButtonClick(View view){

    }
}
