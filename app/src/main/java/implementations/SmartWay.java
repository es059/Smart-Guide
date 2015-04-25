package implementations;

import java.util.ArrayList;

import interfaces.IPlaces;
import interfaces.ISmartWay;

/**
 * Created by Eric Schmidt on 22.04.2015.
 */
public class SmartWay implements ISmartWay{

    public ArrayList<IPlaces> doAction(ArrayList<IPlaces> placesList){

       TourManager.addPlaces(placesList);
       // Initialize population
       Population pop = new Population(50, true);
       System.out.println("Initial distance: " + pop.getFittest().getDistance());

        // Evolve population for 100 generations
        pop = GA.evolvePopulation(pop);
        for (int i = 0; i < 100; i++) {
            pop = GA.evolvePopulation(pop);
        }

        // Print final results
        System.out.println("Finished");
        System.out.println("Final distance: " + pop.getFittest().getDistance());
        System.out.println("Solution:");
        System.out.println(pop.getFittest());

        return pop.getFittest().getTour();

    }
}

