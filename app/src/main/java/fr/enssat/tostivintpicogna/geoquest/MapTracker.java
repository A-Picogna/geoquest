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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

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
import fr.enssat.tostivintpicogna.geoquest.Model.GeoQuestStep;

public class MapTracker extends AppCompatActivity implements LocationListener {

    private LocationManager lm;
    private MapController mapController;
    private MapView map;
    private Toolbar tb;
    private double latitude;
    private double longitude;
    private double altitude;
    private double accuracy;
    private MyLocationNewOverlay mylocation;
    private Location destination;

    static String TAG = "mapTrackerActivity";
    String GeoQuestDataURL = "http://s3.amazonaws.com/projet-enssat/geoquest";
    GeoQuestData gqd;
    int EtapeActuelle;
    final float DISTANCE_MAX = 10.0f;

    GeoPoint p;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_tracker);

        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        tb.inflateMenu(R.menu.menu_map_tracker);



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
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        askGeoQuestData();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        accuracy = location.getAccuracy();

        GeoQuestStep step = gqd.getSteps().get(EtapeActuelle);

        p = new GeoPoint( latitude, longitude);

        mapController.animateTo(p);
        mapController.setCenter(p);

        mylocation = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);

        mylocation.enableMyLocation();
        mylocation.enableFollowLocation();
        map.getOverlays().add(mylocation);

        float distanceInMeters = p.distanceTo(step.getGpsPosition());
        Log.d(TAG, "Position : " + p.toString());
        Log.d(TAG, "Point étape : " + step.getGpsPosition().toString());
        Log.d(TAG, "Distance calculée : " + distanceInMeters);

        if(distanceInMeters < DISTANCE_MAX) {
            locationFounded();
        }

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
        nextStep();
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
                            onGeoQuestDataLoaded();
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

    void onGeoQuestDataLoaded() {
        EtapeActuelle = 0;
        tb.setTitle(gqd.getSteps().get(0).getTitreEtape());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()){
            case R.id.showIndiceItem:
                createIndiceDialog();
                return true;
            case R.id.centerPositionItem:
                centerPosition();
                return true;
            default:
                Log.wtf(TAG, "Mauvais item :" + getResources().getResourceEntryName(item.getItemId()));
                return super.onOptionsItemSelected(item);
        }
    }

    void createIndiceDialog() {
        Log.d(TAG, "Entree dans le createur de dialogue");
        AlertDialog.Builder adb = new AlertDialog.Builder(MapTracker.this);

        GeoQuestStep etape = gqd.getSteps().get(EtapeActuelle);

        LayoutInflater li = LayoutInflater.from(MapTracker.this);
        LinearLayout ll = (LinearLayout) li.inflate(R.layout.indice_dialog, null);

        TextView texteIndice = (TextView) ll.findViewById(R.id.indiceText);
        NetworkImageView imageIndice = (NetworkImageView) ll.findViewById(R.id.indiceImageView);

        texteIndice.setText(etape.getTexteIndice());

        ImageLoader mImageLoader = DownloadManager.getInstance(this).getImageLoader();
        imageIndice.setImageUrl(etape.getImageIndice(), mImageLoader);

        adb.setView(ll);

        adb.setNeutralButton("COMPRIS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_tracker,menu);
        return true;
    }

    void nextStep() {
        if(EtapeActuelle+1 < gqd.getSteps().size()) {
            EtapeActuelle = EtapeActuelle + 1;
            tb.setTitle(gqd.getSteps().get(EtapeActuelle).getTitreEtape());
        } else {
            Context context = getApplicationContext();
            CharSequence text = "You won !";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            finish();
        }
    }

    void centerPosition() {
        if(p != null) {
            mapController.setCenter(p);
        }
    }
}

