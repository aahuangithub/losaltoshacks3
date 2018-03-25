package tech.trash.finderssweepers;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.Bundle;

/**
 * Created by camilleviviani on 3/24/18.
 */

public class Collector extends Activity implements OnMapReadyCallback{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng california = new LatLng(36.778261, -119.417932);

//        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(california, 13));

        map.addMarker(new MarkerOptions()
                .title("california")
                .snippet("The most populous city in Australia.")
                .position(california));
    }
}
