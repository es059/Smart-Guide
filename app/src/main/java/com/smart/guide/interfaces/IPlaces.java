package com.smart.guide.interfaces;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Eric Schmidt on 22.04.2015.
 */
public interface IPlaces {

    public CharSequence getPlaceID();
    public void setPlace(Place place);
    public Place getPlace();
    public void setMarker(Marker marker);
    public Marker getMarker();
    public boolean isStartElement();
    public void setIsStartElement(boolean isStartElement);

    public double distanceTo(IPlaces place);

}
