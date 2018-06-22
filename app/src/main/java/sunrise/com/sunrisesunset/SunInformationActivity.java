package sunrise.com.sunrisesunset;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class SunInformationActivity extends AppCompatActivity implements OnConnectionFailedListener {

    private static final String TAG = "SunriseSunset";
    private SunriseSunsetAPI sunriseSunsetAPI;
    private GoogleApiClient mGoogleApiClient;

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
                Log.i(TAG, "Place: " + place.getName());
                setSunInfo(place);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }
    
    private void setSunInfo(Place place){
        sunriseSunsetAPI.setLat(place.getLatLng().latitude);
        sunriseSunsetAPI.setLng(place.getLatLng().longitude);
        sunriseSunsetAPI.getRepoList(this);
        ((TextView)findViewById(R.id.sunriseText)).setText(sunriseSunsetAPI.getSunrise());
        ((TextView)findViewById(R.id.sunsetText)).setText(sunriseSunsetAPI.getSunset());
    }



    public void setCurrentAddress(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "No permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        final PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);

        final ProgressDialog progressDialog = new ProgressDialog(SunInformationActivity.this);
        progressDialog.setCancelable(false); // set cancelable to false
        progressDialog.setMessage("Please, wait"); // set message
        progressDialog.show(); // show progress dialog

        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer placeLikelihoods) {
                ((EditText)findViewById(R.id.place_autocomplete_search_input)).setText(placeLikelihoods.get(0).getPlace().getAddress());
                setSunInfo(placeLikelihoods.get(0).getPlace());
                placeLikelihoods.release();
            }
        });

        progressDialog.dismiss();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sun_information);

        sunriseSunsetAPI = new SunriseSunsetAPI();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        setCurrentAddress();

        placeFinder();

        ((Button)findViewById(R.id.currentLocationButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentAddress();
            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed!");
    }
}
