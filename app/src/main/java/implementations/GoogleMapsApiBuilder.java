package implementations;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Eric Schmidt on 25.04.2015.
 */
public class GoogleMapsApiBuilder implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static GoogleApiClient mGoogleApiClient;
    private LocationAdapter mLocationAdapter;
    private FragmentActivity context;

    private GoogleMapsApiBuilder(FragmentActivity context, LocationAdapter mLocationAdapter){
        this.mLocationAdapter = mLocationAdapter;
        this.context = context;
        rebuildGoogleApiClient();
    }

    public static GoogleApiClient getInstance(FragmentActivity context, LocationAdapter mLocationAdapter){
        if (mGoogleApiClient == null){
            new GoogleMapsApiBuilder(context, mLocationAdapter);
        }

        return mGoogleApiClient;
    }

    /**
     * Construct a GoogleApiClient for the {@link com.google.android.gms.location.places.Places#GEO_DATA_API} using AutoManage
     * functionality.
     * This automatically sets up the API client to handle Activity lifecycle events.
     */
    protected synchronized void rebuildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and connection failed
        // callbacks should be returned, which Google APIs our app uses and which OAuth 2.0
        // scopes our app requests.
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(context, 0 /* clientId */, this)
                .addConnectionCallbacks(this)
                .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                .build();
    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(context,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();

        // Disable API access in the adapter because the client was not initialised correctly.
        mLocationAdapter.setGoogleApiClient(null);
    }


    @Override
    public void onConnected(Bundle bundle) {
        // Successfully connected to the API client. Pass it to the adapter to enable API access.
        mLocationAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Connection to the API client has been suspended. Disable API access in the client.
        mLocationAdapter.setGoogleApiClient(null);
    }

}
