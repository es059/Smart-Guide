package interfaces;

import java.util.ArrayList;

import implementations.Tour;

/**
 * Created by Eric Schmidt on 22.04.2015.
 */
public interface ISmartWay {

    public ArrayList<IPlaces> doAction(ArrayList<IPlaces> placesList);
}
