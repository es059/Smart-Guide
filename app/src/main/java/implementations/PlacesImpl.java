package implementations;

import com.google.android.gms.location.places.Place;

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
}
