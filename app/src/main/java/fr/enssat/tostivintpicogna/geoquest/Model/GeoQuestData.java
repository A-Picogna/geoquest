package fr.enssat.tostivintpicogna.geoquest.Model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alexandre on 01/12/2016.
 */

public class GeoQuestData {

    String title;
    List<GeoQuestStep> steps = new LinkedList<GeoQuestStep>();
    public GeoQuestData(JSONObject response) throws JSONException {

        this.title = response.getString("title");
        JSONArray buffer = response.getJSONArray("steps");

        for(int i = 0; i < buffer.length(); i++) {
            JSONObject o = buffer.getJSONObject(i);
            steps.add(new GeoQuestStep(
                    o.getString("titreEtape"),

                    (float)o.getJSONObject("gpsPosition").getDouble("north"),
                    (float)o.getJSONObject("gpsPosition").getDouble("west"),

                    o.getString("imageIndice"),
                    o.getString("texteIndice")
            ));
        }
    }

    public List<GeoQuestStep> getSteps() {
        return steps;
    }

    public void setSteps(List<GeoQuestStep> steps) {
        this.steps = steps;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
