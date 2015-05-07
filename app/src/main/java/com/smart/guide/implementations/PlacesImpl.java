package com.smart.guide.implementations;

import android.location.Location;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.model.DistanceMatrix;

import com.smart.guide.interfaces.IPlaces;

/**
 * Created by Eric Schmidt on 22.04.2015.
 */
public class PlacesImpl implements IPlaces {
    private CharSequence mPlaceID;
    private CharSequence mDescription;
    private Place mPlace;
    private Marker mMarker;
    private boolean mIsStartElement;

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
    public void setMarker(Marker marker) {
        this.mMarker = marker;
    }

    @Override
    public Marker getMarker() {
        return mMarker;
    }

    @Override
    public boolean isStartElement() {
        return mIsStartElement;
    }

    @Override
    public void setIsStartElement(boolean isStartElement) {
        this.mIsStartElement = isStartElement;
    }

    @Override
    public double distanceTo(IPlaces place) {
        DistanceMatrix distanceMatrix;
        double distance = 0.0;
        try {
            float[] distanceResult = new float[1];
            Location.distanceBetween(mPlace.getLatLng().latitude, mPlace.getLatLng().longitude,
                    place.getPlace().getLatLng().latitude, place.getPlace().getLatLng().longitude, distanceResult);
            distance = distanceResult[0];
/*
            String originAddress = mPlace.getAddress().toString();
            String destinationAddress = place.getPlace().getAddress().toString();

            originAddress = originAddress.replace(" ", "%20");
            destinationAddress = destinationAddress.replace(" ", "%20");

            String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+originAddress+"&destinations="+destinationAddress+"&mode=car&language=de-DE&sensor=false";
            URL searchUrl = new URL(url);

            BufferedReader br = new BufferedReader(new InputStreamReader(searchUrl.openStream()));
            String strTemp = "";

            while (null != (strTemp = br.readLine())) {
                System.out.print(strTemp);
                strTemp += strTemp;
            }

            com.google.gson.Gson gson = new com.google.gson.Gson();
            distanceMatrix = gson.fromJson(strTemp, DistanceMatrix.class);

            for(int i= 0; i < distanceMatrix.rows.length; i++) {
                for(int x = 0; x < distanceMatrix.rows[i].elements.length; x++){
                    distance = distanceMatrix.rows[i].elements[x].distance.inMeters;
                }
            }
*/

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.print(distance);
        return distance;
    }
}
