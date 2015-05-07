package com.smart.guide.algorithm;

import java.util.ArrayList;
import java.util.Collections;

import com.smart.guide.interfaces.IPlaces;

/**
 * Created by Eric Schmidt on 04.05.2015.
 */
public class Chromosome{
    private ArrayList<IPlaces> mChromosome = new ArrayList<>();
    private ArrayList<IPlaces> mPlacesList;

    private double fitness = -1;
    private double distance = -1;

    public Chromosome (ArrayList<IPlaces> placesList){
        this.mPlacesList = placesList;
        generateChromosome();
        getFitness();
    }

    public Chromosome(){
    }

    public void setChromosome(ArrayList<IPlaces> chromosome) {
        this.mChromosome = chromosome;
        getFitness();
    }

    public ArrayList<IPlaces> getChromosome() {
        return mChromosome;
    }

    private Chromosome generateChromosome(){
        Integer startPlaceIndex = -1;

        //Creating random numbers to iterate through the list
        ArrayList<Integer> placesIndex = new ArrayList<>();
        for (int numbers = 1; numbers < mPlacesList.size();numbers++){
            placesIndex.add(numbers);
            mChromosome.add(null); //Add null references to the List
        }
        Collections.shuffle(placesIndex);

        //Take always the first entry as start
        placesIndex.add(0, 0);

        for (int i = 0; i < mPlacesList.size(); i++) {
            mChromosome.add(placesIndex.get(i), mPlacesList.get(i));

            //Check if a place has a isStartElement-Tag
            if (mPlacesList.get(i).isStartElement()){
                startPlaceIndex = placesIndex.get(i);
            }
        }

        //Swap the first index with the startPlaceIndex if it was set
        if (startPlaceIndex != -1){
            Collections.swap(mChromosome, 0, startPlaceIndex);
        }

        mChromosome.removeAll(Collections.singleton(null));

        return this;
    }

    public double getFitness(){
        if (fitness == -1){
            fitness = 1 / getDistance();
        }

        return fitness;
    }

    private double getDistance(){
        if (distance == -1) {
            int tourDistance = 0;
            // Loop through our tour's places
            for (int placeIndex=0; placeIndex < mChromosome.size(); placeIndex++) {
                // Get place we're travelling from
                IPlaces fromCity = mChromosome.get(placeIndex);
                // place we're travelling to
                IPlaces destinationPlace;
                // Check we're not on our tour's last place, if we are set our
                // tour's final destination place to our starting place
                if(placeIndex+1 < mChromosome.size()){
                    destinationPlace = mChromosome.get(placeIndex + 1);
                }
                else{
                    destinationPlace = mChromosome.get(0);
                }
                // Get the distance between the two places
                tourDistance += fromCity.distanceTo(destinationPlace);
            }
            distance = tourDistance;
        }

        return distance;
    }

    //Compare with the placesID
    @Override
    public boolean equals(Object o) {
        String thisObjectString = "";
        String otherObjectString = "";

        for (IPlaces place : this.mChromosome){
            thisObjectString += place.getPlaceID();
        }

        Chromosome compareChromosome = (Chromosome)o;

        for (IPlaces place : compareChromosome.getChromosome() ){
            otherObjectString += place.getPlaceID();
        }

        if (thisObjectString.equals(otherObjectString)) return true;
        return false;
    }
}
