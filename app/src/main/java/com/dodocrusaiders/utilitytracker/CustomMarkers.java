////////////////////////////////////////////////////////////////////////////////////////////////
//  Author: Jason Adam
//  Purpose: Individual custom markers that will be most likely stored in a local db
////////////////////////////////////////////////////////////////////////////////////////////////
package com.dodocrusaiders.utilitytracker;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;
import java.sql.SQLException;

public class CustomMarkers extends AppCompatActivity
{
    public static LatLng latLng;
    public static int marker_id = 0;
    private SQLiteDBHelper dbHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_marker_form);
        dbHelper = new SQLiteDBHelper(this, 1);

        Button createMarkerButton = findViewById(R.id.createCustomMarkerButton);
        EditText markerName = findViewById(R.id.customMarkerName);
        EditText markerInfo = findViewById(R.id.customMarkerInfo);

        createMarkerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MarkerData data = new MarkerData(
                    markerInfo.getText().toString(),
                        markerName.getText().toString(),
                        null,
                        latLng,
                        marker_id
                );
                CreateMarker(data);
                if(dbHelper.insertMarker(marker_id, (float)latLng.latitude, (float)latLng.longitude, markerInfo.getText().toString(), markerName.getText().toString(), null))
                {
                    marker_id++;
                }
                finish();
            }
        });
    }

    public static void CreateMarker(MarkerData markerData)
    {
        // TODO: Look into changing the img for the marker?
        Marker marker = MapsActivity.mMap.addMarker(new MarkerOptions().
                position(markerData.latLng).
                title(markerData.name).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        marker.setTag(markerData);
        // Add the marker data to the database.

    }

    private void CreateMarker(MarkerData markerData, int MarkerID)
    {
        // TODO: Create popup page so that we can have users enter their info for the markers.
        // TODO: Look into changing the img for the marker?
        Marker marker = MapsActivity.mMap.addMarker(new MarkerOptions().
                position(markerData.latLng).
                title(markerData.name).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        marker.setTag(markerData);
        // Add the marker data to the database.
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  Author: Jason Adam
    //  Purpose: Add markers to map from a database
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void AddMarkerToMap(int MarkerID)
    {
        MarkerData markerData = new MarkerData(); // Call query here instead of doing the new marker data
        CreateMarker(markerData, MarkerID);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  Author: Jason Adam
    //  Purpose: MarkerData required for custom markers to store basic info such as why the marker
    //           is there, its name, a bitmap, etc.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static class MarkerData
    {
        private String info;
        private String name;
        private Bitmap bitmap;
        private LatLng latLng;
        private int marker_id;

        MarkerData()
        {
            this.info = "";
            this.name = "";
            this.bitmap = null;
            this.latLng = null;
        }
        MarkerData(String info, String name, Bitmap bitmap, LatLng latLng, int marker_id)
        {
            this.info = info;
            this.name = name;
            this.bitmap = bitmap;
            this.latLng = latLng;
            this.marker_id = marker_id;
        }

        public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }
        public void setInfo(String info) { this.info = info; }
        public void setName(String name) { this.name = name; }
        public void setLatLng(LatLng latLng) { this.latLng = latLng; }
        public Bitmap getBitmap() { return bitmap; }
        public String getInfo() { return info; }
        public String getName() { return name; }
        public LatLng getLatLng() { return latLng; }
        public int getMarker_id() { return marker_id; }
        public void setMarker_id(int marker_id) { this.marker_id = marker_id; }
    }
}

