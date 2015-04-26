package implementations;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ericschmidt.smartguide.R;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

import interfaces.IPlaces;

/**
 * Created by Eric Schmidt on 25.04.2015.
 */
public class ListViewLocationAdapter extends ArrayAdapter<IPlaces>{
    private FragmentActivity context;

    public ListViewLocationAdapter(Context context, ArrayList<IPlaces> placesList) {
        super(context, 0, placesList);

        this.context = (FragmentActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        IPlaces place = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_location, parent, false);
        }
        // Lookup view for data population
        //ImageView locationImage = (ImageView) convertView.findViewById(R.id.list_view_image);
        TextView locationAddress = (TextView) convertView.findViewById(R.id.list_view_address);
        TextView locationDescription = (TextView) convertView.findViewById(R.id.list_view_description);
        ImageView locationImage = (ImageView) convertView.findViewById(R.id.list_view_image);

        locationImage.setImageDrawable(getLocationDrawableByType(place.getPlace().getPlaceTypes()));
        locationAddress.setText(place.getPlace().getAddress());
        locationDescription.setText(place.getPlace().getName());
        // Return the completed view to render on screen
        return convertView;
    }


    /**
     * Return a drawable object according to the current placeType
     *
     * @param placeTypeList
     * @return
     */
    private Drawable getLocationDrawableByType(List<Integer> placeTypeList){
        Drawable drawable;

        if (placeTypeList.contains(Place.TYPE_GROCERY_OR_SUPERMARKET) || placeTypeList.contains(Place.TYPE_STORE)) {
            drawable = context.getResources().getDrawable(R.drawable.location_shop);
            return drawable;
        }else if (placeTypeList.contains(Place.TYPE_BAR) || placeTypeList.contains(Place.TYPE_FOOD)) {
            drawable = context.getResources().getDrawable(R.drawable.location_bar);
            return drawable;
        }else if (placeTypeList.contains(Place.TYPE_LOCALITY) && !placeTypeList.contains(Place.TYPE_LOCALITY)) {
            drawable = context.getResources().getDrawable(R.drawable.location_city);
            return drawable;
        }else if (placeTypeList.contains(Place.TYPE_ESTABLISHMENT) && placeTypeList.contains(Place.TYPE_LOCALITY)){
            drawable = context.getResources().getDrawable(R.drawable.location_landmark);
            return drawable;
        }else{
            drawable = context.getResources().getDrawable(R.drawable.location_other);
            return drawable;
        }
    }
}
