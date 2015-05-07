package com.smart.guide.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import com.smart.guide.interfaces.IPlaces;

/**
 * Created by Eric Schmidt on 04.05.2015.
 */
public class Genetics {
    /* GA parameters */
    private static final double mutationRate = 1;

    private Population mPopulation;

    private final int MAX_SAME_CHROMOSOME_COUNT = 10;
    private int sameChromosomeCount = 0;
    private Chromosome previousChromosome;
    private boolean stopEvolve;

    public Genetics(Population population){
        this.mPopulation = population;
    }

    public void evolvePopulation(){

        NavigableMap<Double, Chromosome> scaleList = getScaleList();

        Chromosome firstChromosome = getRandom(scaleList);
        Chromosome secondChromosome = getRandom(scaleList);

        Chromosome breedChromosome = generateCrossbreed(firstChromosome, secondChromosome);
        mPopulation.getChromosomeList().add(breedChromosome);

        // Apply mutation rate
        if(Math.random() < mutationRate){
            mPopulation.getChromosomeList().add(mutate(firstChromosome));
        }

        mPopulation.cutPopulation();
        checkStopEvolve();
    }

    //Return a sorted hashMap with the cumulative fitness level and the corresponding Chromosome Object
    private NavigableMap<Double, Chromosome> getScaleList(){
        double totalFitness = 0;
        double individualScale = 0;
        NavigableMap<Double, Chromosome> scaleList = new TreeMap<>();

        for (Chromosome chromosome : mPopulation.getChromosomeList()){
            totalFitness += chromosome.getFitness();
        }
        final double totalFitnessTmp = totalFitness;

        Collections.sort(mPopulation.getChromosomeList(), new Comparator<Chromosome>() {
            @Override
            public int compare(Chromosome firstChromosome, Chromosome secondChromosome) {
                if (normalize(0, totalFitnessTmp, firstChromosome.getFitness()) > normalize(0, totalFitnessTmp, secondChromosome.getFitness())) {
                    return -1;
                } else if (normalize(0, totalFitnessTmp, firstChromosome.getFitness()) < normalize(0, totalFitnessTmp, secondChromosome.getFitness())){
                    return 1;
                }
                return 0;
            }
        });

        for (Chromosome chromosome : mPopulation.getChromosomeList()){
            individualScale += normalize(0, totalFitnessTmp, chromosome.getFitness());
            scaleList.put(individualScale, chromosome);
        }

        return scaleList;
    }

    /**
     * Makes a three's rule to normalize a value in a 0..1 interval
     *
     * @param min
     *            lowest saturation point. Values below this will return 0
     * @param max
     *            highest saturation point. Values above this will return 1
     * @param value
     *
     * @return a double value between start and end. If the Value is -1 then the normalization could not be done
     *
     */
    public static double normalize(double min, double max, double value) {
        if (max <= min)
           return -1;

        if (value >= max)
            return 1;
        if (value <= min)
            return 0;

        double i1 = max - min;
        double i2 = 1 - 0; //Scale between 0...1
        double y = (value - min) * i2 / i1;
        return y + 0;
    }

    //Return a Chromosome Object which was picked randomly from a scaleList
    private Chromosome getRandom(NavigableMap<Double, Chromosome> scaleList){
        double rndNumber = Math.random();

        double below = -1;
        double above = -1;
        double key;

        //Searches the nearest value in the Map
        if (scaleList.ceilingKey(rndNumber) != null) {
            above = scaleList.ceilingKey(rndNumber);
        }
        if (scaleList.floorKey(rndNumber) != null) {
            below = scaleList.floorKey(rndNumber);
        }

        if (above == -1){
            key = below;
        }else if (below == -1){
            key = above;
        }else {
            key = rndNumber - below > above - rndNumber ? above : below;
        }

        return scaleList.get(key);
    }

    //Crossbreed two chromosomes into one
    private Chromosome generateCrossbreed(Chromosome firstChromosome, Chromosome secondChromosome){
        Chromosome newChromosome = new Chromosome();
        ArrayList<IPlaces> chromosome = new ArrayList<>();

        //Cut at half of the places and copy them to the new chromosome
        for (int chromosomeIndex = 0; chromosomeIndex < firstChromosome.getChromosome().size()/2; chromosomeIndex++){
            chromosome.add(firstChromosome.getChromosome().get(chromosomeIndex));
        }

        //Integrate the second chromosome
        for (IPlaces place : secondChromosome.getChromosome()){
            if (!chromosome.contains(place)) chromosome.add(place);
        }

        newChromosome.setChromosome(chromosome);

        return newChromosome;
    }

    //Mutate a chromosome
    private Chromosome mutate(Chromosome hostChromosome){
        //-1;+1 so that the first element cannot be swapped
        int rnd1 = new Random().nextInt(hostChromosome.getChromosome().size() -1) + 1;
        int rnd2 = new Random().nextInt(hostChromosome.getChromosome().size() -1) + 1;

        Collections.swap(hostChromosome.getChromosome(),rnd1, rnd2);
        hostChromosome.getFitness();

        return hostChromosome;
    }

    //Count if the same chromosome is the fittest again
    private void checkStopEvolve(){
        if (previousChromosome != null){
            if (previousChromosome.equals(mPopulation.getChromosomeList().get(0))){
                sameChromosomeCount += 1;
            }else{
                sameChromosomeCount = 0;
            }
        }

        previousChromosome = mPopulation.getChromosomeList().get(0);

        //Set Tag to stop the evaluation if the last 10 times the fittest chromosome was the same one
        if (sameChromosomeCount > MAX_SAME_CHROMOSOME_COUNT){
            stopEvolve = true;
        }
    }

    public boolean isStopEvolve() {
        return stopEvolve;
    }
}