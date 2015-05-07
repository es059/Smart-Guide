package com.smart.guide.algorithm;

import java.util.ArrayList;

import com.smart.guide.interfaces.IPlaces;
import com.smart.guide.interfaces.ISmartWay;

/**
 * Created by Eric Schmidt on 04.05.2015.
 */
public class SmartWay implements ISmartWay {

    public ArrayList<IPlaces> doAction(ArrayList<IPlaces> placesList){

        // Initialize population
        Population population = new Population(50, placesList, true);

        // Evolve population for 200 generations
        Genetics genetics = new Genetics(population);
        for (int i = 0; i < 200; i++) {
            if (genetics.isStopEvolve()) return population.getChromosomeList().get(0).getChromosome();
            genetics.evolvePopulation();
        }

        return population.getChromosomeList().get(0).getChromosome();
    }
}

