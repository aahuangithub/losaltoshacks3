package tech.trash.finderssweepers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static tech.trash.finderssweepers.Constants.CATEGORIES;
import static tech.trash.finderssweepers.Constants.URL;
import static tech.trash.finderssweepers.Constants.dummyCategories;
import static tech.trash.finderssweepers.Constants.dummyCoordinates;
import static tech.trash.finderssweepers.Constants.dummyScore;
import static tech.trash.finderssweepers.Constants.dummyTrash;

public class Finder extends AppCompatActivity implements OnMapReadyCallback {
    FrameLayout fl;
    LocationListener mLocationListener;
    LocationManager locationManager;
    private Marker now;
    private Location currentLocation;
    private GoogleMap mMap;
    FloatingActionButton scoreButt;
    TextView scoreTooltip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder);
        scoreTooltip = findViewById(R.id.finder_score_tooltip);
        scoreButt = findViewById(R.id.finder_score);

        scoreButt.setOnClickListener(v->{
            scoreTooltip.setVisibility(scoreTooltip.getVisibility()==View.GONE?View.VISIBLE:View.GONE);
        });
        scoreButt.setImageBitmap(textAsBitmap(Integer.toString(dummyScore),45, Color.WHITE));
        fl = findViewById(R.id.activity_finder_framelayout);
        MapFragment mp = MapFragment.newInstance();
        mp.getMapAsync(this);
        getFragmentManager().beginTransaction().add(R.id.activity_finder_framelayout, mp).commit();
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                onMapChange(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, R.id.activity_finder_framelayout);
            return;
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        }
        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner);
        //create a list of items for the spinner.

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CATEGORIES);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        Button submit = findViewById(R.id.finder_submit);
        submit.setOnClickListener((v)->{
            dummyTrash.add(new Trash(dropdown.getSelectedItem().toString(), currentLocation.getLatitude(), currentLocation.getLongitude()));
//            dummyCoordinates.add(new Coordinate(currentLocation.getLatitude(), currentLocation.getLongitude()));
//            dummyCategories.add(dropdown.getSelectedItem().toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(Finder.this);
            builder.setMessage("Thanks for your contribution!")
                    .setPositiveButton("OK", (dialogInterface, i) -> {

                    });
            builder.create().show();
        });
        Button b = findViewById(R.id.api_test);
        b.setOnClickListener((View v) ->{

// Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("server", response);
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("server", error.toString());
                        }
                        protected Map<String, String> getParams(){
                            Map<String, String> params = new HashMap<>();
                            params.put("location", "[1,2]");
                            params.put("category", "test");
                            return params;
                        }
                    }
            );

// Add the request to the RequestQueue.
            queue.add(stringRequest);
        });
        LinearLayout l = findViewById(R.id.finder_ll);
        fl.bringChildToFront(l);
//        l.bringToFront();
    }
    public void onMapChange(Location location){
        if(now != null){
            now.remove();
        }
        currentLocation = location;

        MarkerOptions mo = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
        now = mMap.addMarker(mo);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(location.getLatitude(), location.getLongitude())
        ));
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
    //method to convert your text to image
    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
}

