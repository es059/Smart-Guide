package com.smart.guide.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.hudomju.swipe.adapter.ViewAdapter;
import com.smart.guide.R;
import com.smart.guide.algorithm.SmartWay;
import com.smart.guide.implementations.GoogleMapsApiBuilder;
import com.smart.guide.implementations.ListViewLocationAdapter;
import com.smart.guide.implementations.LocationAdapter;
import com.smart.guide.interfaces.IPlaces;
import com.smart.guide.interfaces.ISmartWay;

import java.util.ArrayList;

public class SmartGuideActivity extends ActionBarActivity
        implements AdapterView.OnItemClickListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ISmartWay mSmartWay; //reference to the implementation of the SmartWay-Algorithmen
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Marker> mMarkerList = new ArrayList<>();
    private LocationAdapter mLocationAdapter;
    private ListViewLocationAdapter mListViewLocationAdapter;
    private ArrayList<IPlaces> mPlacesList = new ArrayList<>();

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private AutoCompleteTextView searchTextView;
    private ListView locationListView;
    private TextView locationHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_guide);

        setUpMapIfNeeded();

        locationHint = (TextView) findViewById(R.id.location_hint);

        //Set up the Autocomplete TextView
        initializeAutoCompleteView();

        //Receive the GoogleApiClient Object
        mGoogleApiClient = GoogleMapsApiBuilder.getInstance(this, mLocationAdapter);

        //Set the reference to the list view and assign the adapter
        initializeListView();

        //Set the reference to the smart way algorithmen
        mSmartWay = new SmartWay();

    }

    private void initializeAutoCompleteView(){
        //Set up the Autocomplete TextView
        searchTextView = (AutoCompleteTextView) findViewById(R.id.location_txt);
        mLocationAdapter = new LocationAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_GREATER_SYDNEY, null);
        searchTextView.setAdapter(mLocationAdapter);
        searchTextView.setOnItemClickListener(this);
    }

    private void initializeListView(){
        //Set the reference to the list view and assign the adapter
        locationListView = (ListView) findViewById(R.id.location_list);
        mListViewLocationAdapter = new ListViewLocationAdapter(this, mPlacesList);
        locationListView.setAdapter(mListViewLocationAdapter);
        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IPlaces place = (IPlaces) locationListView.getItemAtPosition(position);
                centerMap(place.getMarker());
            }
        });

        final SwipeToDismissTouchListener touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(locationListView),
                        new SwipeToDismissTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ViewAdapter viewAdapter, int i) {
                                removeMarker(mPlacesList.get(i));
                                mPlacesList.remove(i);
                                mListViewLocationAdapter.notifyDataSetChanged();
                            }
                        });

        locationListView.setOnTouchListener(touchListener);
        locationListView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    if (mPlacesList.isEmpty()){
                        locationHint.setVisibility(View.VISIBLE);
                    }else{
                        locationHint.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        if (mPlacesList.isEmpty()){
            locationHint.setVisibility(View.VISIBLE);
        }else{
            locationHint.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_smart_guide, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_do) {
            if (!mPlacesList.isEmpty()) {
                new SmartGuideBackGroundTask().execute();
                return true;
            }else{
                return false;
            }
        }else if(id == R.id.action_navigate){
            openRouteInMaps();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    private void openRouteInMaps(){
        if (!mPlacesList.isEmpty()){
            String uriString = "";

            for(IPlaces place : mPlacesList){
                if (uriString.isEmpty()) {
                    uriString += "http://maps.google.com/maps?saddr=" + place.getPlace().getAddress().toString().replace(" ", "+") + "&daddr=";
                }else{
                    uriString += place.getPlace().getAddress().toString().replace(" ", "+")+ "+to:";
                }
            }

            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse(uriString));
            startActivity(intent);
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setMarker(IPlaces place) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(place.getPlace().getLatLng()).title(place.getPlace().getName().toString()));

        place.setMarker(marker);
        mMarkerList.add(marker);
        centerMap();
    }

    private void removeMarker(IPlaces place){
        place.getMarker().remove();
    }

    private void centerMap(Marker marker){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker.getPosition());
        doCenter(builder);
    }

    private void doCenter(LatLngBounds.Builder builder){
        LatLngBounds bounds = builder.build();
        int padding = 50;// offset from edges of the map in pixels

        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(cu);
            }
        });
    }

    private void centerMap(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkerList) {
            builder.include(marker.getPosition());
        }
        doCenter(builder);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final IPlaces place = mLocationAdapter.getItem(position);
        mListViewLocationAdapter.add(place);
        mListViewLocationAdapter.notifyDataSetChanged();

        if (mPlacesList.isEmpty()){
            locationHint.setVisibility(View.VISIBLE);
        }else{
            locationHint.setVisibility(View.GONE);
        }

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, place.getPlaceID().toString());
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
              @Override
              public void onResult(PlaceBuffer places) {
                  place.setPlace(places.get(0));
                  setMarker(place);

                  mListViewLocationAdapter.notifyDataSetChanged();
              }
         });

        //Clear TextView
        searchTextView.setText("");
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);
    }

    public class SmartGuideBackGroundTask extends AsyncTask<Void, Void,Void> {
        ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mPlacesList = mListViewLocationAdapter.getPlacesList();

            nDialog = new ProgressDialog(SmartGuideActivity.this); //Here I get an error: The constructor ProgressDialog(PFragment) is undefined
            nDialog.setMessage(getResources().getString(R.string.progress_dialog_message));
            nDialog.setTitle(getResources().getString(R.string.progress_dialog_title));
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mPlacesList = mSmartWay.doAction(mPlacesList);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mListViewLocationAdapter.clear();
            mListViewLocationAdapter.addAll(mPlacesList);
            mListViewLocationAdapter.notifyDataSetChanged();

            //Receive the GoogleApiClient Object
            mGoogleApiClient = GoogleMapsApiBuilder.getInstance(SmartGuideActivity.this, mLocationAdapter);

            nDialog.dismiss();
        }
    }
}
