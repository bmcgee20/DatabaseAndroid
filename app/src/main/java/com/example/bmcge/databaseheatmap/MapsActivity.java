package com.example.bmcge.databaseheatmap;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        LatLng NewYorkCity = new LatLng(40.664077, -73.950162);
        //restrict the user to new york state boundary
        LatLngBounds NewYorkState = new LatLngBounds(
          new LatLng(40.494700, -74.287262), new LatLng(40.908946, -73.654218));
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.setLatLngBoundsForCameraTarget(NewYorkState);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(NewYorkCity));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(9.85f));
        mMap.setMinZoomPreference(9.85f);
        addHeatMapWeighted();
    }
    public void addHeatMapWeighted(){
        List<WeightedLatLng> VioList = null;
        //Set the list to our lat and longs with weight
        try{
            VioList = getViolationData(1);
        }catch(JSONException e){
            Toast.makeText(this, "Problem reading violations.", Toast.LENGTH_LONG).show();
        }
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(VioList)
                .build();

        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
    private ArrayList<WeightedLatLng> getViolationData(int resource) throws JSONException{
        ArrayList<WeightedLatLng> VioList = new ArrayList<WeightedLatLng>();
        //Read from out data now
        VioList.add(new WeightedLatLng(new LatLng(40.664077, -73.950132),2.0f));
        VioList.add(new WeightedLatLng(new LatLng(40.631386, -74.026723),4.0f));
        VioList.add(new WeightedLatLng(new LatLng(40.744054, -73.976719),1.0f));

        return VioList;
    }
}
