package tech.trash.finderssweepers;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.easing.linear.Linear;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static java.lang.Math.cos;
import static tech.trash.finderssweepers.Constants.CATEGORIES;
import static tech.trash.finderssweepers.Constants.dummyCategories;
import static tech.trash.finderssweepers.Constants.dummyCoordinates;
import static tech.trash.finderssweepers.Constants.dummyScore;
import static tech.trash.finderssweepers.Constants.dummyTrash;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private LocationListener mLocationListener;
    private GoogleMap mMap;
    private Marker now;
    private Button trashButton;
    private FloatingActionButton scoreDisplay;
    private LocationManager locationManager;
    Location current;
    private TextView scoreTooltip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);
        scoreTooltip = findViewById(R.id.score_tooltip);
        scoreDisplay = findViewById(R.id.collector_score);
        scoreDisplay.setImageBitmap(textAsBitmap(Integer.toString(dummyScore),45, Color.WHITE));
        scoreDisplay.setOnClickListener(v->{
            scoreTooltip.setVisibility(scoreTooltip.getVisibility()==View.GONE?View.VISIBLE:View.GONE);
        });
        trashButton = findViewById(R.id.found);
        trashButton.setOnClickListener(v->{
            View inner = getLayoutInflater().inflate(R.layout.fragment_map_dialog, null);

            Button submit = inner.findViewById(R.id.fragment_map_dialog_submit);
            Spinner spin = inner.findViewById(R.id.map_dialog_fragment_spinner);
            ProgressBar pb = inner.findViewById(R.id.map_dialog_fragment_loader);

            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this)
                    .setTitle("What did you pick up?")
                    .setView(inner);
            ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, CATEGORIES);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(aa);
// spin.set
            LinearLayout ll = findViewById(R.id.collector_ll);
            Dialog d = builder.create();
            submit.setOnClickListener(v1->{
                d.setCanceledOnTouchOutside(false);
                submit.setVisibility(View.GONE);
                spin.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
                final Handler daniel = new Handler();
                daniel.postDelayed(() -> {
                    d.dismiss();
                    int toRemove = -1;
                    for(int i=0; i<dummyTrash.size(); i++){
                        if(dummyTrash.get(i).getCategory().equals(spin.getSelectedItem().toString())
                                && nearby(current.getLatitude(), current.getLongitude(), dummyTrash.get(i).getCoordinate().getX(), dummyTrash.get(i).getCoordinate().getY())
                                ){
                            toRemove = i;

                            if(spin.getSelectedItem().toString().equals("Pile")){
                                Toast.makeText(this, "Swept! +2", Toast.LENGTH_SHORT).show();
                                dummyScore+=2;
                            }else {
                                Toast.makeText(this, "Swept! +1", Toast.LENGTH_SHORT).show();
                                dummyScore++;
                            }
                            scoreDisplay.setImageBitmap(textAsBitmap(Integer.toString(dummyScore),45, Color.WHITE));
                            YoYo.with(Techniques.Pulse).duration(500).repeat(1).playOn(ll);
                            break;
                        }
                    }
                    if(toRemove>=0){
                        dummyTrash.remove(toRemove);
                        mMap.clear();
                        onMapChange(current);
                        for(Trash trash:dummyTrash){
                            mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(trash.getCoordinate().getX(), trash.getCoordinate().getY()))
                                    .radius(5)
                                    .strokeColor(Color.RED)
                                    .fillColor(Color.parseColor("#E57373"))
                            );
                        }
                    }else{
                        Toast.makeText(this, "Thanks for cleaning!", Toast.LENGTH_SHORT).show();
                    }
                }, 1500); //after 3s
            });

            d.show();
        });
        MapFragment mp = MapFragment.newInstance();
        mp.getMapAsync(this);
        getFragmentManager().beginTransaction().add(R.id.map_frame, mp).commit();
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
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, R.id.map_frame);
            return;
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
        }
        }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
//    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        for(Trash trash:dummyTrash){
            mMap.addCircle(new CircleOptions()
                    .center(new LatLng(trash.getCoordinate().getX(), trash.getCoordinate().getY()))
                    .radius(5)
                    .strokeColor(Color.RED)
                    .fillColor(Color.parseColor("#E57373"))
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == R.id.map_frame){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                } catch (SecurityException e) {

                }
            } else{
//                AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                        .setMessage("Please enable location services.");
//                builder.create().show();
                finish();
            }
        }
    }

    public void onMapChange(Location location){
        if(now != null){
            now.remove();
        }
        current = location;

        MarkerOptions mo = new MarkerOptions().position(new LatLng(current.getLatitude(), current.getLongitude()));
        now = mMap.addMarker(mo);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(current.getLatitude(), current.getLongitude())
        ));
    }

    private boolean nearby(double latitude, double longitude, double x, double y) {
        double rad = 6371.0;
        double lat1 = Math.toRadians(latitude);
        double lon1 = Math.toRadians(longitude);
        double lat2 = Math.toRadians(x);
        double lon2 = Math.toRadians(y);

        double   dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double  a = Math.pow(Math.sin(dlat / 2),2) + cos(lat1) * cos(lat2) * Math.pow(Math.sin(dlon / 2),2);
        double  c = 2 * Math.atan(Math.sqrt(a)/ Math.sqrt(1 - a));
        double distance = rad * c;
        Log.d("distance", String.valueOf(distance));
        return distance<0.005?true:false;
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
