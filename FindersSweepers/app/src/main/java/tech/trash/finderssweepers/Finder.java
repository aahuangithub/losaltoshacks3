package tech.trash.finderssweepers;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static tech.trash.finderssweepers.Constants.url;

public class Finder extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder);


        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner);
        //create a list of items for the spinner.
        String[] category = {
                "Soft Plastic",
                "Hard Plastic",
                "Paper",
                "Cigarettes",
                "Cans/Bottles",
                "Needles",
                "Pile",
                "Human",
                "Other"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, category);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        Button b = findViewById(R.id.api_test);
        b.setOnClickListener((v)->{

// Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        // Display the first 500 characters of the response string.
                        Log.d("server", response);
                    }, error -> Log.d("server", "fail"));

// Add the request to the RequestQueue.
            queue.add(stringRequest);
        });
    }

}

