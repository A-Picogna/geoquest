package fr.enssat.tostivintpicogna.geoquest;

import android.os.Debug;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import fr.enssat.tostivintpicogna.geoquest.Model.GeoQuestData;
import fr.enssat.tostivintpicogna.geoquest.Model.GeoQuestStep;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

public class MainActivity extends Activity {

    static String TAG = "mainActivity";
    String GeoQuestDataURL = "http://korobase.info/geoquest";
    GeoQuestData gqd;

    @Override public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //important! set your user agent to prevent getting banned from the osm servers
        org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        askGeoQuestData();

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
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
                        int i =0;
                        // Code à mettre si erreur
                        // Ex: Code par défaut, message erreur, etc...
                    }
                });

        // Access the RequestQueue through your singleton class.
        DownloadManager.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}
