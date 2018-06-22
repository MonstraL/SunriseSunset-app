package sunrise.com.sunrisesunset;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class SunInformationActivity extends AppCompatActivity implements OnConnectionFailedListener {

    private static final String TAG = "SunriseSunset";
    private PlaceController placeController;
    private SunriseSunsetAPI sunriseSunsetAPI;

    private EditText citySearchField;
    private LinearLayout searchLayout;



    public void placeFinder() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                //placeController.setPlace(place);
                sunriseSunsetAPI.setLat(place.getLatLng().latitude);
                sunriseSunsetAPI.setLng(place.getLatLng().longitude);
                setSunInfo();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void setSunInfo(){
        sunriseSunsetAPI.getRepoList(this);

    }

    public void setCurrentAddress(){
        ((EditText)findViewById(R.id.place_autocomplete_search_input)).setText(placeController.getPlace().getAddress());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sun_information);

        sunriseSunsetAPI = new SunriseSunsetAPI();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        PlaceController placeController = new PlaceController(this, mGoogleApiClient);

        placeFinder();

        //setCurrentAddress();


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed!");
    }
}
