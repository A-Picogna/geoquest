package fr.enssat.tostivintpicogna.geoquest.Model;

/**
 * Created by Alexandre on 01/12/2016.
 */

public class GeoQuestStep {
    String titreEtape;
    GpsPosition gpsPosition;
    String imageIndice;
    String texteIndice;

    GeoQuestStep(String titreEtape,
                 float north,
                 float west,
                 String imageIndice,
                 String texteIndice) {
        this.titreEtape = titreEtape;
        this.gpsPosition = new GpsPosition(north, west);
        this.imageIndice = imageIndice;
        this.texteIndice = texteIndice;
    }
}
