package implementations;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

import interfaces.IPlaces;

/**
 * Created by rete on 25.04.15.
 */
public class TourManager {


    // Holds our places
    private static ArrayList<IPlaces> destinationPLaces = new ArrayList<IPlaces>();


    // Adds a destination place
    public static void addPlace(IPlaces place) {
        destinationPLaces.add(place);
    }

    // Adds a destination places
    public static void addPlaces(ArrayList<IPlaces> placesArrayList) {
        destinationPLaces = placesArrayList;
    }

    // Get a Place
    public static IPlaces getPlace(int index){
        return (IPlaces) destinationPLaces.get(index);
    }

    // Get the number of destination places
    public static int numberOfPlaces(){
        return destinationPLaces.size();
    }
}

