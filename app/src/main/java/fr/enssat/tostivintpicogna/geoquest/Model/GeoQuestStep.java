package fr.enssat.tostivintpicogna.geoquest.Model;

import org.osmdroid.util.GeoPoint;

/**
 * Created by Alexandre on 01/12/2016.
 */

public class GeoQuestStep {
    String titreEtape;
    GeoPoint gpsPosition;
    String imageIndice;
    String texteIndice;

    GeoQuestStep(String titreEtape,
                 float lat,
                 float lng,
                 String imageIndice,
                 String texteIndice) {
        this.titreEtape = titreEtape;
        this.gpsPosition = new GeoPoint((lat * 1E6), (lng * 1E6));
        this.imageIndice = imageIndice;
        this.texteIndice = texteIndice;
    }
}
