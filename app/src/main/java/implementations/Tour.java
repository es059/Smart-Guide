package implementations;

/**
 * Created by rete on 25.04.15.
 */
import java.util.ArrayList;
import java.util.Collections;

import interfaces.IPlaces;

public class Tour{

    // Holds our tour of places
    private ArrayList<IPlaces> tour;
    // Cache
    private double fitness = 0;
    private int distance = 0;

    // Constructs a blank tour
    public Tour(){
        tour = new ArrayList<IPlaces>();
        for (int i = 0; i < TourManager.numberOfPlaces(); i++) {
            tour.add(null);
        }
    }

    public Tour(ArrayList<IPlaces> tour){
        this.tour = new ArrayList<IPlaces>();
        this.tour = tour;
    }

    // Creates a random individual
    public void generateIndividual() {
        IPlaces startPlace = null;
        // Loop through all our destination places and add them to our tour
        for (int placeIndex = 0; placeIndex < TourManager.numberOfPlaces(); placeIndex++) {
            setPlace(placeIndex, TourManager.getPlace(placeIndex));
            if (TourManager.getPlace(placeIndex).isStartElement()){
                startPlace =  TourManager.getPlace(placeIndex);
            }
        }
        // Randomly reorder the tour
        Collections.shuffle(tour);
        if(startPlace != null) {
            tour.remove(startPlace);
            tour.add(0, startPlace);
        }
    }

    // Gets a place from the tour
    public IPlaces getPlace(int tourPosition) {
        return (IPlaces)tour.get(tourPosition);
    }

    // Sets a Place in a certain position within a tour
    public void setPlace(int tourPosition, IPlaces place) {
        tour.set(tourPosition, place);
        // If the tours been altered we need to reset the fitness and distance
        fitness = 0;
        distance = 0;
    }

    // Gets the tours fitness
    public double getFitness() {
        if (fitness == 0) {
            fitness = 1/(double)getDistance();
        }
        return fitness;
    }

    // Gets the total distance of the tour
    public int getDistance(){
        if (distance == 0) {
            int tourDistance = 0;
            // Loop through our tour's places
            for (int placeIndex=0; placeIndex < tourSize(); placeIndex++) {
                // Get place we're travelling from
                IPlaces fromCity = getPlace(placeIndex);
                // place we're travelling to
                IPlaces destinationPlace;
                // Check we're not on our tour's last place, if we are set our
                // tour's final destination place to our starting place
                if(placeIndex+1 < tourSize()){
                    destinationPlace = getPlace(placeIndex + 1);
                }
                else{
                    destinationPlace = getPlace(0);
                }
                // Get the distance between the two places
                tourDistance += fromCity.distanceTo(destinationPlace);
            }
            distance = tourDistance;
        }
        return distance;
    }

    // Get number of places on our tour
    public int tourSize() {
        return tour.size();
    }

    // Check if the tour contains a place
    public boolean containsPlace(IPlaces place){
        return tour.contains(place);
    }

    @Override
    public String toString() {
        String geneString = "|";
        for (int i = 0; i < tourSize(); i++) {
            geneString += getPlace(i)+"|";
        }
        return geneString;
    }

    public ArrayList<IPlaces> getTour() {
        return this.tour;
    }
}