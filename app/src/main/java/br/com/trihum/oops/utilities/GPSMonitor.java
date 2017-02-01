package br.com.trihum.oops.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import br.com.trihum.oops.RegistraInfracaoActivity;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by raphaelmoraes on 08/01/17.
 */

public class GPSMonitor extends Service {

    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    Location location; // Location
    double latitude; // Latitude
    double longitude; // Longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES_NETWORK = 2;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES_GPS = 2;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 500; // 1 segundo

    //https://devdiscoveries.wordpress.com/2010/02/04/android-use-location-services/

    private final Activity activity;
    private Location currentLocation;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private LocationListener listenerCoarse;
    private LocationListener listenerFine;
    private boolean locationAvailable = true;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSMonitor(Activity activity) {
        this.activity = activity;
    }

    public void getLocation() {
        try {
            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( activity, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
                showSettingsAlert();

            } else {
                this.canGetLocation = true;

                // Initialize criteria for location providers
                Criteria fine = new Criteria();
                fine.setAccuracy(Criteria.ACCURACY_FINE);
                Criteria coarse = new Criteria();
                coarse.setAccuracy(Criteria.ACCURACY_COARSE);

                // Get at least something from the device,
                // could be very inaccurate though
                currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(fine, true));

                if (listenerFine == null || listenerCoarse == null)
                    createLocationListeners();

                // Will keep updating about every 500 ms until
                // accuracy is about 1000 meters to get quick fix.
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES_NETWORK, listenerCoarse);
                // Will keep updating about every 500 ms until
                // accuracy is about 50 meters to get accurate fix.
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES_GPS, listenerFine);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     * */
    public void stopUsingGPS(){
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( activity, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if(locationManager != null){
            if (listenerCoarse != null) locationManager.removeUpdates(listenerCoarse);
            if (listenerFine != null) locationManager.removeUpdates(listenerFine);
        }
    }


    /**
     * Function to get latitude
     * */
    public double getLatitude(){

        if(currentLocation != null){
            latitude = currentLocation.getLatitude();
        }

        return latitude;
    }


    /**
     * Function to get longitude
     * */
    public double getLongitude(){

        if(currentLocation != null){
            longitude = currentLocation.getLongitude();
        }

        return longitude;
    }

    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     * */
    public void showSettingsAlert(){

        AlertDialog.Builder alertDialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            alertDialog = new AlertDialog.Builder(activity);
        }

        // Setting Dialog Title
        alertDialog.setTitle("Configurações de GPS");

        // Setting Dialog Message
        alertDialog.setMessage("O GPS não está habilitado. Deseja ir para os ajustes para habilitar o seu uso pelo Oops?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void createLocationListeners() {
        listenerCoarse = new LocationListener() {
            public void onStatusChanged(String provider,
                                        int status, Bundle extras) {
                switch(status) {
                    case LocationProvider.OUT_OF_SERVICE:
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        locationAvailable = false;
                        break;
                    case LocationProvider.AVAILABLE:
                        locationAvailable = true;
                }
            }
            public void onProviderEnabled(String provider) {
            }
            public void onProviderDisabled(String provider) {
            }
            public void onLocationChanged(Location location) {

                ((RegistraInfracaoActivity)activity).informaCoordenadas(location,"CELULAR");
                if (isBetterLocation(location,currentLocation))
                {
                    currentLocation = location;
                    ((RegistraInfracaoActivity)activity).obteveCoordenadas();
                }
                else{
                    locationAvailable = false;
                }

                //currentLocation = location;
                /*if (location.getAccuracy() > 1000 &&
                        location.hasAccuracy())
                    locationManager.removeUpdates(this);*/
            }
        };
        listenerFine = new LocationListener() {
            public void onStatusChanged(String provider,
                                        int status, Bundle extras) {
                switch(status) {
                    case LocationProvider.OUT_OF_SERVICE:
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        locationAvailable = false;
                        break;
                    case LocationProvider.AVAILABLE:
                        locationAvailable = true;
                }
            }
            public void onProviderEnabled(String provider) {
            }
            public void onProviderDisabled(String provider) {
            }
            public void onLocationChanged(Location location) {

                ((RegistraInfracaoActivity)activity).informaCoordenadas(location,"GPS");
                if (isBetterLocation(location,currentLocation))
                {
                    currentLocation = location;
                    ((RegistraInfracaoActivity)activity).obteveCoordenadas();
                }
                else{
                    locationAvailable = false;
                }

                //currentLocation = location;
                /*if (location.getAccuracy() > 1000
                        && location.hasAccuracy())
                    locationManager.removeUpdates(this);*/
            }
        };
    }





    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}