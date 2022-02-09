/***************************************************************************************************
 * Filename:	    Maps.java
 * Date Created:	11/14/20
 * Modifications:
 *      Jason Adam 11/14/20 - Added the first implementation of LocationToggle
 *      Tyler Kukkola 11/14/20 - Added the first implementation of SetManualLocationMarker
 *      Brandon Toledo 11/16/20 - Added the first implementation of GPSLocation
 *      Jason Adam 11/16/20 - Removed ACCESS_COARSE_LOCATION since we won't use it.
 *                            Cleaned up a lot of excess code and fixed issue with app crashing due
 *                            a misplaced line of code not running until after it needed to be ran
 *                            in the GPSLocation function.
 *                            Maps class no longer inherits from MapsActivity and instead all
 *                            functions should pass the required arguments. Inheritance was making
 *                            it so that instead of using the previously assigned member variables
 *                            of MapsActivity it was using new variables that were all set to null.
 *                            Also somewhat implemented LocationPermissionsDenied function. It will
 *                            need to updated in the future to do something more useful.
 *      Jason Adam 11/17/20 - Updated the LocationToggle and GPSLocation function to take in a
 *                            MapsActivity object rather than having way too many arguments passed
 *                            this also fixed an issue with variables not being set in the functions
 *                            because java passes by value and the only way to pass by reference is
 *                            to pass an object. I went ahead and switched the arguments to
 *                            MapsActivity objects to remove some errors when passing vars by value.
 *                            Also implemented some compiler recommended improvements to clean up
 *                            some excess code.
 *
 * Class: Maps
 *
 * Purpose: Brief description of class purpose.
 *
 * Manager functions:
 *
 * Methods:
 *  LocationToggle(MapsActivity mapsActivity)
 *			This function toggles whether or not the location tracking is enabled or disabled.
 *
 *  SetManualLocationMarker(GoogleMap googleMap)
 *          Calling this function allows the user to touch the screen and set a marker
 *
 *  GPSLocation(MapsActivity mapsActivity)
 *     This function Checks for location permission and if permissions aren't granted tells the
 *     user with a temporary marker on the map, implements onLocationChanged from LocationListener,
 *     and it calls LocationToggle to toggle the location listening.
 *
 **************************************************************************************************/

package com.dodocrusaiders.utilitytracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps
{
    protected boolean isLocationTrackingOn = false;
    protected Marker manualLocationMarker; // Store value of manual location marker
    protected LatLng anchor = null;
    protected LatLng bearing = null;

    /***********************************************************************************************
     * Author: Tyler Kukkola 11/14/20
     * Purpose: Let user add a marker to map for location
     **********************************************************************************************/
    public void SetManualLocationMarker(GoogleMap googleMap)
    {
        // Setup listener for user input
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng point)
            {
                // Set manual location marker
                if (manualLocationMarker != null)
                {
                    manualLocationMarker.setPosition(point);
                }
                else
                {
                    manualLocationMarker = googleMap.addMarker(new MarkerOptions()
                            .position(point)
                            .title("Manual Location Marker")
                            .snippet("Your location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                }

                //anchor = bearing;
                //bearing  = point;
                anchor = point;

                // Remove this listener (removing every listener?)
                googleMap.setOnMapClickListener(null);
            }
        });
    }

    /***********************************************************************************************
     * Author: Jason Adam 11/14/20
     * Purpose: Toggle on/off automatic location updates
     **********************************************************************************************/
    protected void LocationToggle(Boolean Tracking, MapsActivity mapsActivity)
    {
        // Stop the function if the required permissions are not granted. Maybe add a popup and tell them to enable location services?
        if (ActivityCompat.checkSelfPermission(mapsActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        // If the location is currently on then turn off updates to the location manager
        if (!Tracking)
        {
            mapsActivity.locationManager.removeUpdates(mapsActivity.locationListener);
            isLocationTrackingOn = false;
        }
        // If the location is currently off then turn on updates to the location manager
        else
        {
            mapsActivity.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    mapsActivity.MIN_UPDATE_LOCATION_TIME_MS, mapsActivity.MIN_DISTANCE_FOR_UPDATE_METERS, mapsActivity.locationListener);
            isLocationTrackingOn = true;
        }
    }

    /***********************************************************************************************
     * Author: Jason Adam & Brandon Toledo 11/16/20
     * Purpose: Checks permission to access location, and assigns location if granted. Also
     *          implements onLocationChanged from LocationListener and toggles LocationToggle.
    ***********************************************************************************************/
    protected void GPSLocation(MapsActivity mapsActivity)
    {
        if (ContextCompat.checkSelfPermission(mapsActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            LocationPermissionsDenied(mapsActivity.mMap);
            // FIXME: Not sure what this is suppose to do. Something about requesting permissions but other than that I'm not to sure.
            //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        else
        {
            mapsActivity.locationManager = (LocationManager) mapsActivity.getSystemService(Context.LOCATION_SERVICE);
            mapsActivity.gpsLocation = mapsActivity.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Instantiate locationListener and define the onLocationChanged function
            mapsActivity.locationListener = new LocationListener()
            {
                @Override
                public void onLocationChanged(@NonNull Location location)
                {
                    // Set gpsLocation Lat/Long to the updated version
                    mapsActivity.gpsLocation.setLatitude(location.getLatitude());
                    mapsActivity.gpsLocation.setLongitude(location.getLongitude());
                    // Set latLng to the updated version for the new marker
                    mapsActivity.latLng = new LatLng(mapsActivity.gpsLocation.getLatitude(), mapsActivity.gpsLocation.getLongitude());
                    // If the marker exists remove it otherwise do nothing
                    if (mapsActivity.currentLocationMarker != null)
                        mapsActivity.currentLocationMarker.remove();

                    // Place a marker at the current location
                    mapsActivity.currentLocationMarker = mapsActivity.mMap.addMarker(new MarkerOptions().position(mapsActivity.latLng).title("Current Location"));
                    // Center the camera on the current location
                    // TODO: NICK THE CODE IS HERE
                    mapsActivity.mMap.moveCamera(CameraUpdateFactory.newLatLng(mapsActivity.latLng));
                    if (manualLocationMarker != null)
                        manualLocationMarker.remove();
                }

                // Tyler Kukkola (1/03/21) Application can crash without defining these overrides here
                @Override
                public void onProviderEnabled(@NonNull String provider) {

                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            };

            // Set the locationManager to update its last known location based off of MIN_UPDATE_LOCATION_TIME_MS and MIN_DISTANCE_FOR_UPDATE_METERS
            LocationToggle(true, mapsActivity);
        }
    }
    /***********************************************************************************************
     * Author: Brandon Toledo 11/16/20
     * Purpose: Optional function that handles denied location permission.
     * FIXME: CREATE A PROPER IMPLEMENTATION LATER!!! THIS IS ONLY FOR DEMO!!!
     **********************************************************************************************/
    private void LocationPermissionsDenied(GoogleMap mMap)
    {
        LatLng ErrorLatLng = new LatLng(0.0f,0.0f);
        Marker ErrorMarker = mMap.addMarker(new MarkerOptions().position(ErrorLatLng).title("Location Permissions Denied."));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ErrorLatLng));
        //Handle location if location permission is denied (manual location setting here?)
    }

    public LatLng GetAnchor()
    {
        return anchor;
    }

    public LatLng GetBearing()
    {
        return bearing;
    }
}
