package com.example.saarthi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class displayParkikng extends AppCompatActivity {


    String server;
    JSONObject ress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_parkikng);
        String id = getIntent().getStringExtra("id");
//        Toast.makeText(this,
//                id,
//                Toast.LENGTH_SHORT).show();
        server = getResources().getString(R.string.server);
        addParkingSpotstoMap(id);
        try {
            onCallClick();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            onGoClick();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void  addParkingSpotstoMap(String id){


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        //   String rname = name.getText().toString();

        String url = server + "parking/"+id;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        //Call call = client.newCall(request);

        client.newCall(request).enqueue(new Callback(){

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                runOnUiThread(new Runnable(){


                    @Override
                    public void run() {
                        displayParkikng.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    displayTheDetails(response.body().string());
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

    void onCallClick() throws JSONException {

        ImageButton b = (ImageButton)(findViewById(R.id.callbutton));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Toast.makeText(getApplicationContext(), "ghvhgv", Toast.LENGTH_LONG).show();
                try {
                    intent.setData(Uri.parse("tel:"+ress.get("phone").toString()));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });




    }

    void onGoClick() throws JSONException {

        ImageButton b = (ImageButton)(findViewById(R.id.travel));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = null;
                try {
                    s = "https://www.google.com/maps/dir/?api=1&destination="+ress.get("lat").toString()+"%2c"+ress.get("lang").toString();
                     Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Uri u = Uri.parse(s);
                Intent Maps = new Intent(Intent.ACTION_VIEW,u);
                Maps.setPackage("com.google.android.apps.maps");
                startActivity(Maps);


            }
        });





    }

    void displayTheDetails(String s) throws JSONException {

        //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        ress = new JSONObject(s);
        TextView name = (TextView)findViewById(R.id.name);
        TextView timing = (TextView)findViewById(R.id.timing);
        TextView price = (TextView)findViewById(R.id.price);
        TextView ins = (TextView)findViewById(R.id.ins);
        name.setText(ress.get("name").toString());
        timing.setText(ress.get("timing").toString());
        price.setText(ress.get("price").toString());
        ins.setText(ress.get("ins").toString());
        TextView heavy = (TextView)findViewById(R.id.heavyvehicle);
        if(ress.get("heavy").toString().equals("true")){
            heavy.setText("Allowed");
        }
        else{
            heavy.setText("Not Allowed");

        }

    }
}
