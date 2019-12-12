package com.example.saarthi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;



public class quicklist extends AppCompatActivity {
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    //RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
    int clickCounter=0;
    final List<String> fruits_list = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quicklist);
        Intent intent = getIntent();
        String message = getIntent().getStringExtra("json");
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        final ListView lv = (ListView) findViewById(R.id.lv);

        // Initializing a new String Array

        try {
            showlist(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Create a List from String Array elements


        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, fruits_list);

        // DataBind ListView with items from ArrayAdapter
        lv.setAdapter(arrayAdapter);

        // Capture the layout's TextView and set the string as its text



    }


    public void showlist(String message) throws JSONException {
        JSONObject json = new JSONObject(message);
        Iterator<String> keys = json.keys();
       // int i = 0;
        while(keys.hasNext()) {
            String key = keys.next();
                String x = "It's ok";
                int y = Integer.parseInt(json.getString(key));
                if(y >= 3) x = "Great";
                else if(y > 0 ) x = "Good";
                else if(y <= -2) x = "Very Bad";
                else if (y < 0) x = "Bad";
                fruits_list.add(key + " : " + x);
        }
    }
}
