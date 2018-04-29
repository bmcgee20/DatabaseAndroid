package com.example.bmcge.databaseheatmap;

import android.nfc.Tag;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        //Set the center of the current to new york state
        LatLng NewYorkStateCenter = new LatLng(42.796400, -75.610987);

        //restrict the user to new york state boundary
        LatLngBounds NewYorkState = new LatLngBounds(
          new LatLng(39.886484, -80.572464), new LatLng(45.292211, -70.794633));
        mMap.setLatLngBoundsForCameraTarget(NewYorkState);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(NewYorkStateCenter));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6.0f));
        mMap.setMinZoomPreference(6.0f);
        addHeatMapWeighted();
    }
    public void addHeatMapWeighted(){
        List<WeightedLatLng> VioList = null;
        //Set the list to our lat and longs with weight
        try{
            VioList = getViolationData();
        }catch(JSONException e){
            Toast.makeText(this, "Problem reading violations.", Toast.LENGTH_LONG).show();
        }
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(VioList)
                .build();

        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
    private ArrayList<WeightedLatLng> getViolationData() throws JSONException{
        ArrayList<WeightedLatLng> VioList = new ArrayList<WeightedLatLng>();
        //Read from out data
        InputStream inputStream = getResources().openRawResource(R.raw.food_services_vio);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            String coord = object.getString("Cords");
            double violations = object.getDouble("VioNum");
            //Coord is in form "(12.5446, 75.66546)" so we need to get the two doubles out
            coord =coord.replaceAll("[()\\s]",""); //remove all the symbols
            String coords[] = coord.split(",",2);
            Double lat = Double.parseDouble(coords[0]);
            Double lng = Double.parseDouble(coords[1]);
            Log.i("Tag","Coords"+lat+lng);
            LatLng newCord = new LatLng(lat,lng);
            VioList.add(new WeightedLatLng(newCord,violations));
        }
        return VioList;
    }
}
