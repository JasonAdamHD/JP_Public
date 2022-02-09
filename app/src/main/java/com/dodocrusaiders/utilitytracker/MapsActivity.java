/***********************************************************************************************
 * Author:
 * Purpose:
 * Modifications:
 *      Jason Adam 1/19/21: Updated the overlay function to add tested functionality of overlaying
 *                          multiple layers.
 **********************************************************************************************/
package com.dodocrusaiders.utilitytracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    protected static final long MIN_UPDATE_LOCATION_TIME_MS = 1000;
    protected static final float MIN_DISTANCE_FOR_UPDATE_METERS = 5.0f;
    private static final int PICK_IMAGE = 100;
    private static final int COLOR_PICKER_ACTIVITY_RESULT_CODE = 512;
    private static final int GALLERY_REQUEST = 0;
    public static GoogleMap mMap;
    public static Context temp_context;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Location gpsLocation;
    protected static LatLng latLng;
    protected Marker currentLocationMarker;
    protected Marker defaultTestMarker;
    protected Maps mapsModule = new Maps();
    protected Annotations annotationsModule;
    protected SQLiteDBHelper dbHelper;
    protected UI UIHelper = new UI();
    protected Intent intent;
    protected int idTracker = 0;
    protected int id = 0;
    protected LatLng anchor = null;
    protected LatLng bearing = null;

    protected byte[] image;
    protected Bitmap capture;

    protected ImageProcessing ip = new ImageProcessing();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Set the content view to that of the xml used for the layout
        setContentView(R.layout.activity_maps);
        dbHelper = new SQLiteDBHelper(this, 1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    /***********************************************************************************************
     * Author: Shawn gibbons 11/16/20
     * Purpose: creates menu
     ***********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">" + getString(R.string.app_name) + "</font>"));
        return true;
    }

    /***********************************************************************************************
     * Author: Shawn gibbons 11/16/20
     * Purpose: handles the cases on which item in the menu is selected
     ***********************************************************************************************/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId())
        {
            /***********************************************************************************
             * * * * * * FOR ITEMS NOT IN THE 'ELLIPSIS' PART OF THE MENU ONLY * * * * * *
             ***********************************************************************************/
            case R.id.saveToDatabase:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://media/internal/images/media"));
                startActivityForResult(photoPickerIntent, PICK_IMAGE);
                break;
            case R.id.tooltips:
                Intent popTut = new Intent(this, PopUpTutorial.class);
                startActivity(popTut);
                break;

            /***********************************************************************************
             * * * * * * FOR ITEMS IN THE 'ELLIPSIS' PART OF THE MENU ONLY * * * * * *
             *
             * * * FOR ITEMS IN THE 'MARKER OPTIONS' SUBMENU ONLY * * *
             ***********************************************************************************/
            case R.id.addManualMarker:
                Toast.makeText(this, "Add a marker to map by clicking", Toast.LENGTH_SHORT).show();
                mapsModule.SetManualLocationMarker(mMap);
                //anchor = mapsModule.GetAnchor();
                //bearing = anchor;
                break;
            case R.id.removeAllMarkers:
                Toast.makeText(this, "Removed all markers", Toast.LENGTH_SHORT).show();
                mapsModule.LocationToggle(false, this);
                if (mapsModule.manualLocationMarker != null)
                    mapsModule.manualLocationMarker.remove();
                if (currentLocationMarker != null)
                    currentLocationMarker.remove();
                if (defaultTestMarker != null)
                    defaultTestMarker.remove();
                mapsModule.manualLocationMarker = null;
                break;
            case R.id.addCustomMarker:
                Activity activity = this;
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
                {
                    @Override
                    public void onMapClick(LatLng point)
                    {
                        CustomMarkers.latLng = point;
                        Intent intentCustomMarker = new Intent(activity, CustomMarkers.class);
                        startActivity(intentCustomMarker);
                        mMap.setOnMapClickListener(null);
                    }
                });
                break;

            /***********************************************************************************
             * * * * * * FOR ITEMS IN THE 'ELLIPSIS' PART OF THE MENU ONLY * * * * * *
             *
             * * * FOR ITEMS IN THE 'Settings' SUBMENU ONLY * * *
             ***********************************************************************************/
            case R.id.terrainView:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            /*case R.id.test:
                Toast.makeText(this, "Added map overlay", Toast.LENGTH_SHORT).show();
                LatLng test = new LatLng(42.2543, -121.7869);
                defaultTestMarker = mMap.addMarker(new MarkerOptions().position(test).title("test overlay"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(test));
                //GroundOverlay testoverlay = Layer.addMap(mMap, R.mipmap.test, 0,0,0,0,(float).5,(float).5, 0.5f);
                //Layer.toggleVisibility(mMap, testoverlay);
                //Layer.toggleVisibility(mMap, testoverlay);
                break;*/
            case R.id.automaticLocationTracking:
                Toast.makeText(this, "Toggling location tracking", Toast.LENGTH_SHORT).show();
                mapsModule.LocationToggle(!mapsModule.isLocationTrackingOn, this);
                break;
            case R.id.colorPicker:
                // TODO: MAKE IT SO THE SetBitmap opens up to picker menu
                Intent intentColorPickerSelector = new Intent(this, DBSelectMenu.class);
                startActivityForResult(intentColorPickerSelector, COLOR_PICKER_ACTIVITY_RESULT_CODE);
                break;

            /***********************************************************************************
             * * * * * * FOR ITEMS IN THE 'ELLIPSIS' PART OF THE MENU ONLY * * * * * *
             ***********************************************************************************/
            case R.id.toggleMapView:
                if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                else
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                break;
            case R.id.about:
                Intent pop = new Intent(this, popup.class);
                startActivity(pop);
                break;
            case R.id.deleteFromDatabase:
                Intent intentDelete = new Intent(this, DBDeleteMenu.class);
                startActivity(intentDelete);
                break;
            case R.id.addImageLayer:
                anchor = mapsModule.GetAnchor();
                if(anchor != null)
                {
                    Intent intentLayer = new Intent(this, SelectLayerMenu.class);
                    //intentLayer.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivityForResult(intentLayer, 700);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Set anchor point before selecting image", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        // Set map as the googleMap
        mMap = googleMap;
        temp_context = this;
        // Enable the +/- on the bottom right of the map for easy testing
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Start the GPSLocation
        mapsModule.GPSLocation(this);
        mMap.setOnMarkerClickListener(this);
        // (Tyler Kukkola 1/03/21) Setup the annotations system
        annotationsModule = Annotations.GetInstance(mMap, this, findViewById(R.id.invisible_framelayout));
        dbHelper.populateMarkers();
    }

    /***********************************************************************************************
     * Author: Shawn Gibbons 1/20/21
     * Purpose: setting onActivityResult method, calls the getPath function for getting path of
     *          image and calls insertData to insert the path along with other data into the database
     * Updates: Nick Springer 2/10/21
     *          Moved Bearing calculation to separate function
     *
     *          Shawn Gibbons 2/22/21
     *          made inserting data more automated
     ***********************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
            Uri uri = data.getData();
            String path = getPath(uri);

            idTracker = dbHelper.getSize();
            insertDataHelper(idTracker + 1, 1, 2, 3, 4, 5, path); // the hardcoded numbers are just in so function gets called to make it easier to input images
            // until latitude, longitude, position X, position Y, and bearing are grabbed and put in instead.
        }
        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                assert data != null;
                float[] temp = data.getFloatArrayExtra("ImageValues");
                float angle = GetBearing(temp[0], temp[1], temp[2], temp[3]);
                float bearing = GetBearing((float)mapsModule.GetAnchor().latitude, (float)mapsModule.GetAnchor().longitude, (float)mapsModule.GetBearing().latitude, (float)mapsModule.GetBearing().longitude);
                float width = CalcImgWidth(mapsModule.GetAnchor(), mapsModule.GetBearing(), bearing, temp[0], temp[1], temp[2], temp[3], temp[4]);
                /*TODO: for future reference dynamic ID's in case retrieving images from the database ends up making the resource ID's not stay constant */

                String mDrawableName = "test";
                int resID = getResources().getIdentifier(mDrawableName, "mipmap", getPackageName());


                //GroundOverlay testoverlay = Layer.addMap(mMap, R.mipmap.test,(float)mapsModule.GetAnchor().latitude, (float)mapsModule.GetAnchor().longitude,temp[0]/1100,temp[1]/2000, bearing, angle, 0f, width);
                //Layer.toggleVisibility(mMap, testoverlay);
                //Layer.toggleVisibility(mMap, testoverlay);
            }
            else if(resultCode == RESULT_CANCELED)
            {

            }
        }
        else if(requestCode == 200)
        {
            String filename = new String(data.getStringExtra("filename"));
            Intent intentImageWindow = new Intent(this, ImageWindow.class);
            intentImageWindow.putExtra("ImageWindow", filename);
        }
        else if(requestCode == 300)
        {
            Bitmap bitmap = null; // ************ this is a place holder, if testing the insertBitmapDataHelper function, be sure to replace it with something else ********************

            idTracker = dbHelper.getSize();
            insertBitmapDataHelper(idTracker + 1, 2, 3, 4, 5, 6, bitmap /* **** be sure to replace this **** */);
        }
        else if (requestCode == 500)
        {
            assert data != null;
            //TODO: will change, is set up for demo
            VisibleRegion region = mMap.getProjection().getVisibleRegion();

            // using this id value, we can tell the annotations object which map we are accessing from the database
            int id = data.getIntExtra("id", -1);
            annotationsModule.SetLastAccessedTableID(id);

            int eRadius = 6371;                                                                 //radius of earth
            double midLat = region.latLngBounds.northeast.latitude - region.latLngBounds.southwest.latitude;
            double lat1 = midLat;//.farRight.latitude *Math.PI/180;                                       //setting up variables for haversine formula
            double lat2 = midLat;//.farLeft.latitude * Math.PI/180;
            double deltaLat = 0;//(region.farLeft.latitude - region.farRight.latitude) * Math.PI/180;
            double deltaLong = (region.farLeft.longitude - region.farRight.longitude) * Math.PI/180;

            //starting haversine formula
            double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLong/2) * Math.sin(deltaLong/2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double dist = eRadius * c * 1000;

            float tempRotation = data.getFloatExtra("Rotation", 0);
            float tempWidth = data.getFloatExtra("Width", 0);
            float width = (float) (dist * (tempWidth/100));

            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);

            GroundOverlay testOverlay2 = Layer.addMap(mMap, bmp,(float)mapsModule.GetAnchor().latitude, (float)mapsModule.GetAnchor().longitude, (float).5, (float).5, 0, tempRotation, 0f, width);

            // auto clear annotations when loading a map?
            annotationsModule.ClearAllAnnotations();
        }
        else if (requestCode == 700)
        {
            // create bitmap screen capture
            if(data != null) {
                int idOfImage = data.getIntExtra("id", -1);

                //send background into imagewindowbars and set to background

                image = data.getExtras().getByteArray("image");
                System.arraycopy(data.getExtras().getByteArray("image"), 0, image, 0, data.getExtras().getByteArray("image").length);
                //image = data.getByteArrayExtra("image");
                Intent intentImageWindow = new Intent(this, ImageWindowBars.class);
                intentImageWindow.putExtra("id", idOfImage);
                intentImageWindow.putExtra("ImageWindow", image);
                startActivityForResult(intentImageWindow, 500);
            }
        }
        else if(requestCode == COLOR_PICKER_ACTIVITY_RESULT_CODE && resultCode == RESULT_OK)
        {
            Intent intentColorPicker = new Intent(this, ColorPicker.class);
            startActivity(intentColorPicker);
        }
    }

    public void CaptureMapScreen()
    {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback()
        {
            @Override
            public void onSnapshotReady(Bitmap bitmap)
            {
                capture = bitmap;
            }
        };
        mMap.snapshot(callback);
    }

    /***********************************************************************************************
     * Author: Nick Springer 2/10/21
     * Purpose: calculates the bearing between two points, the first point being the anchor point,
     *          the second being the relative anchor
     * Updates: Nick Springer 2/13/21
     *          Fixed bearing calculation logic for determining the appropriate quadrant
     *          and fixed math error (originally initial bearing was calculated in radians, added
     *          conversion to degrees)
     ***********************************************************************************************/
    protected float GetBearing(float x1, float y1, float x2, float y2)
    {
        float xDist = Math.abs(x1 - x2);
        float yDist = Math.abs(y1 - y2);

        //find angle
        double angle = Math.atan(yDist / xDist) * (180 / 3.14);
        //determine quadrent of angle
        if (x1 > x2)//left
        {
            if (y1 > y2)//bottom
            {
                angle = 270 - angle;
            }
            else//top
            {
                angle = 270 + angle;
            }
        }
        else//right
        {
            if (y1 < y2)//top
            {
                angle = 90 - angle;
            }
            else
            {
                angle = 90 + angle;
            }
        }

        return (float) angle;
    }

    /***********************************************************************************************
     * Author: Shawn Gibbons 2/22/21
     * Purpose: Helps with testing database's original insert function.
     ***********************************************************************************************/
    public void insertDataHelper(int id, float lat, float lon, float posX, float posY, float bearing, String path)
    {
        if (dbHelper.insertData(id, lat, lon, posX, posY, bearing, path))
        {
            Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
            idTracker++; // currently just to help keep track of IDs and how many images are in database -- is sequential
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Not Successful", Toast.LENGTH_SHORT).show();
        }
    }

    /***********************************************************************************************
     * Author: Shawn Gibbons 2/22/21
     * Purpose: Helps with testing bitmap version of database's insert function.
     ***********************************************************************************************/
    public void insertBitmapDataHelper(int id, float lat, float lon, float posX, float posY, float bearing, Bitmap img)
    {
        if (dbHelper.insertBitmapData(id, lat, lon, posX, posY, bearing, "", img))
        {
            Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
            idTracker++; // currently just to help keep track of IDs and how many images are in database -- is sequential
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Not Successful", Toast.LENGTH_SHORT).show();
        }
    }

    /***********************************************************************************************
     * Author: Nick Springer 2/14/21
     * Purpose: Calculates the width of the image being used for the groudOverlay
     ***********************************************************************************************/
    protected float CalcImgWidth(LatLng mapAnchr, LatLng mapBearing, float bearing, float imgAnchorX, float imgAnchorY, float imgBearingX, float imgBearingY, float imgWidth)
    {
        int eRadius = 6371;                                                                 //radius of earth
        double lat1 = mapAnchr.latitude *Math.PI/180;                                       //setting up variables for haversine formula
        double lat2 = mapBearing.latitude * Math.PI/180;
        double deltaLat = (mapBearing.latitude - mapAnchr.latitude) * Math.PI/180;
        double deltaLong = (mapBearing.longitude - mapAnchr.longitude) * Math.PI/180;

        //starting haversine formula
        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLong/2) * Math.sin(deltaLong/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double dist = eRadius * c * 1000;

        float imgDistX = Math.abs(imgAnchorX - imgBearingX);                                //horizontal distnce between points in image
        float imgDistY = Math.abs(imgAnchorY - imgBearingY);                                //vertical distnace between points in image
        float imgDist = (float) Math.sqrt((imgDistX * imgDistX) + (imgDistY + imgDistY));   //linear distance between point in image

        //relative horizontal distance between anchor and bearing of globe
        dist = (dist * imgDistX)/imgDist;

        //relative width of image on globe
        dist = (dist * imgWidth)/imgDistX;

        return (float) dist;
    }

    /***********************************************************************************************
     * Author: Shawn Gibbons 1/23/21
     * Purpose: fetch location of the image.
     ***********************************************************************************************/
    public String getPath(Uri uri)
    {
        if(uri == null)
        {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor != null)
        {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        return uri.getPath();
    }

    /***********************************************************************************************
     * Author: Tyler Kukkola 1/03/21
     * Purpose: UI element click function that toggles the users' ability to annotate
     ***********************************************************************************************/
    public void ToggleDrawState(View view)
    {
        if (annotationsModule.isUserAnnotating())
        {
            annotateOff();
            Toast.makeText(this, "Annotations disabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            annotateOn();
            Toast.makeText(this, "Annotations enabled, try drawing", Toast.LENGTH_SHORT).show();
        }
        annotationsModule.ToggleUserAnnotating();
    }

    /***********************************************************************************************
     * Author: Shawn Gibbons 4/26/21
     * Purpose: Hides annotation buttons when annotate mode is turned off
     ***********************************************************************************************/
    public void annotateOff()
    {
        Button btn;
        SeekBar seekBar;
        btn = (Button) findViewById(R.id.annotate_Line);
        btn.setVisibility(View.GONE);

        btn = (Button) findViewById(R.id.annotate_Circle);
        btn.setVisibility(View.GONE);

        btn = (Button) findViewById(R.id.annotate_Delete);
        btn.setVisibility(View.GONE);

        btn = (Button) findViewById(R.id.annotate_clearAll);
        btn.setVisibility(View.GONE);

        btn = (Button) findViewById(R.id.annotate_load);
        btn.setVisibility(View.GONE);

        btn = (Button) findViewById(R.id.annotate_Text);
        btn.setVisibility(View.GONE);

        btn = (Button) findViewById(R.id.annotate_save);
        btn.setVisibility(View.GONE);

        btn = (Button) findViewById(R.id.annotate_black);
        btn.setVisibility(View.GONE);

        btn = (Button) findViewById(R.id.annotate_red);
        btn.setVisibility(View.GONE);

        btn = (Button) findViewById(R.id.annotate_green);
        btn.setVisibility(View.GONE);

        btn = (Button) findViewById(R.id.annotate_blue);
        btn.setVisibility(View.GONE);

        seekBar = (SeekBar) findViewById(R.id.seekbar_textSize);
        seekBar.setVisibility(View.GONE);
    }

    /***********************************************************************************************
     * Author: Shawn Gibbons 4/26/21
     * Purpose: Displays annotation buttons when annotate mode is turned on
     ***********************************************************************************************/
    public void annotateOn()
    {
        Button btn;
        SeekBar seekBar;
        btn = (Button) findViewById(R.id.annotate_Line);
        btn.setVisibility(View.VISIBLE);

        btn = (Button) findViewById(R.id.annotate_Circle);
        btn.setVisibility(View.VISIBLE);

        btn = (Button) findViewById(R.id.annotate_Delete);
        btn.setVisibility(View.VISIBLE);

        btn = (Button) findViewById(R.id.annotate_clearAll);
        btn.setVisibility(View.VISIBLE);

        btn = (Button) findViewById(R.id.annotate_load);
        btn.setVisibility(View.VISIBLE);

        btn = (Button) findViewById(R.id.annotate_Text);
        btn.setVisibility(View.VISIBLE);

        btn = (Button) findViewById(R.id.annotate_save);
        btn.setVisibility(View.VISIBLE);

        btn = (Button) findViewById(R.id.annotate_black);
        btn.setVisibility(View.VISIBLE);

        btn = (Button) findViewById(R.id.annotate_red);
        btn.setVisibility(View.VISIBLE);

        btn = (Button) findViewById(R.id.annotate_green);
        btn.setVisibility(View.VISIBLE);

        btn = (Button) findViewById(R.id.annotate_blue);
        btn.setVisibility(View.VISIBLE);

        seekBar = (SeekBar) findViewById(R.id.seekbar_textSize);
        seekBar.setVisibility(View.VISIBLE);
    }

    /***********************************************************************************************
     * Author: Tyler Kukkola 1/09/21
     * Purpose: UI element click function that changes the annotation modes
     ***********************************************************************************************/
    public void ChangeAnnotation_NewAnnotationMode(View view)
    {
        Button button = (Button) view;
        String mode = button.getText().toString();

        switch (mode)
        {
            case "text":
                annotationsModule.ChangeAnnotationMode("Text");
                break;
            case "line":
                annotationsModule.ChangeAnnotationMode("Line");
                break;
            case "circle":
                annotationsModule.ChangeAnnotationMode("Circle");
                break;
            case "delete":
                annotationsModule.ChangeAnnotationMode("Delete");
                break;
            default:
                Log.i("annotations", "not a valid mode?");
        }
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 2/05/21
     * Purpose: Testing clear all feature of annotations
     *      Change to more formal later
     *      todo: make more formal if button still being used
     ***********************************************************************************************/
    public void dev_button(View view)
    {
        annotationsModule.ClearAllAnnotations();
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 4/10/21
     * Purpose: Change the color of annotations
     * https://stackoverflow.com/questions/8089054/get-the-background-color-of-a-button-in-android
     ***********************************************************************************************/
    public void ChangeAnnotationColor(View view)
    {
        ColorDrawable colorDrawable = (ColorDrawable)view.getBackground();
        annotationsModule.SetAnnotateColor(colorDrawable.getColor());

        switch(view.getId())
        {
            case R.id.annotate_black:
                Toast.makeText(this, "Color changed to black!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.annotate_red:
                Toast.makeText(this, "Color changed to red!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.annotate_green:
                Toast.makeText(this, "Color changed to green!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.annotate_blue:
                Toast.makeText(this, "Color changed to blue!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 2/05/21
     * Purpose: save/load annotations
     *
     *      3/06/21 - These functions are now used for testing saving annotations along with images
     *
     *      5/05/21 - These functions connect the xml save/load annotations buttons to the save/load
     *                functions.
     ***********************************************************************************************/
    public void dev_save(View view)
    {
        int ret = annotationsModule.SaveAnnotations(annotationsModule.GetCurrentMapID());
        if (ret != 0)
            Toast.makeText(this, "Save annotation failed", Toast.LENGTH_SHORT).show();
    }
    public void dev_load(View view)
    {
        int ret = annotationsModule.LoadAnnotations(annotationsModule.GetCurrentMapID());
        if (ret != 0)
            Toast.makeText(this, "Load annotation failed", Toast.LENGTH_SHORT).show();
    }

    public void showImage(Bitmap bitmap)
    {
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  Author: Jason Adam
    //  Purpose: Return latLng
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public LatLng GetLatLng() { return latLng; }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  Author: Jason Adam
    //  Purpose: Handles what happens when a marker is clicked on
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onMarkerClick(final Marker marker)
    {
        if(marker.getTag() instanceof CustomMarkers.MarkerData)
        {
            // TODO: Instead of doing this I will have to make a page view or something where the user can actually view the info about the marker
            CustomMarkers.MarkerData customMarkerTag = (CustomMarkers.MarkerData) marker.getTag();
            CustomMarkersView.info = customMarkerTag.getInfo();
            CustomMarkersView.name = customMarkerTag.getName();
            CustomMarkersView.tempBitmap = customMarkerTag.getBitmap();
            CustomMarkersView.latLng = customMarkerTag.getLatLng();
            CustomMarkersView.marker = marker;
            Intent intentCustomMarker = new Intent(this, CustomMarkersView.class);
            startActivity(intentCustomMarker);
        }
        else
        {
            Toast.makeText(this, "Marker doesn't have embedded information.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}