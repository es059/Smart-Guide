package implementations;

import com.google.android.gms.location.places.Place;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;

import interfaces.IPlaces;

/**
 * Created by Eric Schmidt on 22.04.2015.
 */
public class PlacesImpl implements IPlaces {
    public CharSequence mPlaceID;
    public CharSequence mDescription;
    public Place mPlace;


    public PlacesImpl(CharSequence placeID, CharSequence description) {
        this.mPlaceID = placeID;
        this.mDescription = description;
    }

    @Override
    public String toString() {
        return mDescription.toString();
    }

    @Override
    public CharSequence getPlaceID() {
        return mPlaceID;
    }

    @Override
    public void setPlace(Place place) {
        this.mPlace = place;
    }

    @Override
    public Place getPlace() {
        return mPlace;
    }

    @Override
    public double distanceTo(IPlaces place) {
        DistanceMatrix distanceMatrix;
        double distance = 0.0;
        try {
            String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+mPlace.getAddress()+"&destinations="+place.getPlace().getAddress()+"&mode=car&language=de-DE&sensor=false";
            URL searchUrl = new URL(url);
            System.out.print(searchUrl);
            BufferedReader br = new BufferedReader(new InputStreamReader(searchUrl.openStream()));
            String strTemp = "";


            while (null != (strTemp = br.readLine())) {
                System.out.print(strTemp);

                com.google.gson.Gson gson = new com.google.gson.Gson();
                distanceMatrix = gson.fromJson(strTemp, DistanceMatrix.class);

                for(int i= 0; i < distanceMatrix.rows.length; i++) {
                    for(int x = 0; x < distanceMatrix.rows[i].elements.length; x++){
                        distance = distanceMatrix.rows[i].elements[x].distance.inMeters;
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.print(distance);
        return distance;
    }
}
