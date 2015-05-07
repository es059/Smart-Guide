package com.smart.guide.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.smart.guide.interfaces.IPlaces;

/**
 * Created by Eric Schmidt on 04.05.2015.
 */
public class Population {
    private ArrayList<IPlaces> mPlacesList;
    private ArrayList<Chromosome> mChromosomeList = new ArrayList<>();

    // Construct a population
    public Population(int populationSize, ArrayList<IPlaces> placesList, boolean initialise) {
        // If we need to initialise a population of tours do so
        if (initialise) {
            // Loop and create individuals
            for (int i = 0; i < populationSize; i++) {
                mChromosomeList.add(new Chromosome(placesList));
            }
        }
    }

    public ArrayList<Chromosome> getChromosomeList() {
        return mChromosomeList;
    }

    public void cutPopulation(){
        sortByFitness();
        if (mChromosomeList.size() > 50) mChromosomeList.subList(50, mChromosomeList.size()).clear();
    }

    public void sortByFitness(){
        Collections.sort(mChromosomeList, new Comparator<Chromosome>() {
            @Override
            public int compare(Chromosome firstChromosome, Chromosome secondChromosome) {
                if (firstChromosome.getFitness() > secondChromosome.getFitness()) {
                    return -1;
                } else if (firstChromosome.getFitness() < secondChromosome.getFitness()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }
}
