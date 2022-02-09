package com.dodocrusaiders.utilitytracker;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class CustomMarkersView extends AppCompatActivity
{
    public static String name;
    public static String info;
    public static Bitmap tempBitmap;
    public static LatLng latLng;
    public static Marker marker;
    private Button deleteMarkerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        // TODO: Make the marker info be scrollable for if the info given is really long
        // TODO: Add functionality
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_custom_marker_info);
        TextView markerName = findViewById(R.id.customMarkerNameView);
        TextView markerInfo = findViewById(R.id.customMarkerInfoView);;
        deleteMarkerButton = findViewById(R.id.deleteCustomMarkerButton);
        //ImageView markerBitmap = findViewById(R.id.customMarkerBitmapView);
        //markerBitmap.setImageBitmap(tempBitmap);
        markerName.setText(name);
        markerInfo.setText(info);
        SQLiteDBHelper dbHelper = new SQLiteDBHelper(this, 1);

        deleteMarkerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CustomMarkers.MarkerData markerData = (CustomMarkers.MarkerData) marker.getTag();
                dbHelper.deleteMarker(markerData.getMarker_id());
                marker.remove(); // Replace this with a DB query using ID to remove the marker
                CustomMarkers.marker_id--;
                finish();
            }
        });
    }
}
