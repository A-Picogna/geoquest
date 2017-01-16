package fr.enssat.tostivintpicogna.geoquest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import fr.enssat.tostivintpicogna.geoquest.Model.GeoQuestData;

public class MapTracker extends Activity implements LocationListener {

    private LocationManager lm;
    private MapController mapController;
    private MapView map;
    private double latitude;
    private double longitude;
    private double altitude;
    private double accuracy;
    private MyLocationNewOverlay mylocation;
    private Location destination;

    static String TAG = "mapTrackerActivity";
    String GeoQuestDataURL = "http://s3.amazonaws.com/projet-enssat/geoquest";
    GeoQuestData gqd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_tracker);
        //important! set your user agent to prevent getting banned from the osm servers
        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getTileProvider().setTileSource(new XYTileSource("Mapnik",
                0, 22, 256, ".png", new String[]{
                "http://a.tile.openstreetmap.org/",
                "http://b.tile.openstreetmap.org/",
                "http://c.tile.openstreetmap.org/"}));
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setMaxZoomLevel(22);
        map.setMinZoomLevel(5);
        mapController = (MapController) map.getController();
        mapController.setZoom(14);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        askGeoQuestData();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        accuracy = location.getAccuracy();

        GeoPoint p = new GeoPoint( (latitude ),  (longitude ));

        mapController.animateTo(p);
        mapController.setCenter(p);

        mylocation = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);

        mylocation.enableMyLocation();
        mylocation.enableFollowLocation();
        map.getOverlays().add(mylocation);

        //locationFounded();

    }

    public void locationFounded(){
        AlertDialog alertDialog = new AlertDialog.Builder(MapTracker.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Congratz, you've found the location!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    void askGeoQuestData(){
        //TextView mTxtDisplay;
        //ImageView mImageView;
        //mTxtDisplay = (TextView) findViewById(R.id.txtDisplay);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, GeoQuestDataURL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //mTxtDisplay.setText("Response: " + response.toString());
                        // Code à mettre si succès
                        try {
                            gqd = new GeoQuestData(response);
                            Log.d(TAG, gqd.getTitle());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Ex: changement information, etc...
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                    }
                });

        // Access the RequestQueue through your singleton class.
        DownloadManager.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}

