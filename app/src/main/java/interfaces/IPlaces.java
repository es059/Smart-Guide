package interfaces;

import com.google.android.gms.location.places.Place;

/**
 * Created by Eric Schmidt on 22.04.2015.
 */
public interface IPlaces {

    public CharSequence getPlaceID();
    public void setPlace(Place place);
    public Place getPlace();
    public double distanceTo(IPlaces place);

}
