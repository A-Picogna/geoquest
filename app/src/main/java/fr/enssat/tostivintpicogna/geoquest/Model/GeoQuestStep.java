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

    public String getTitreEtape() {
        return titreEtape;
    }

    public void setTitreEtape(String titreEtape) {
        this.titreEtape = titreEtape;
    }

    public GeoPoint getGpsPosition() {
        return gpsPosition;
    }

    public void setGpsPosition(GeoPoint gpsPosition) {
        this.gpsPosition = gpsPosition;
    }

    public String getImageIndice() {
        return imageIndice;
    }

    public void setImageIndice(String imageIndice) {
        this.imageIndice = imageIndice;
    }

    public String getTexteIndice() {
        return texteIndice;
    }

    public void setTexteIndice(String texteIndice) {
        this.texteIndice = texteIndice;
    }
}
