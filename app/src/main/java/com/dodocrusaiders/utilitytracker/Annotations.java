package com.dodocrusaiders.utilitytracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.core.widget.TextViewCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Singleton;


/***********************************************************************************************
 * Author: Tyler Kukkola 1/03/21
 * Purpose: Singleton object that handles map annotations
 *
 * This class is created by MainActivity when application starts up, can not be newed.
 * Must use a form of the static function Annotations.GetInstance() in order to get a
 * reference to this class.
 *
 **********************************************************************************************/
public class Annotations
{
    // Static variable of type Annotations ( This is for the singleton functionality )
    private static Annotations singleInstance = null;

    private InputMethodManager inputManager;
    private GoogleMap map;
    private FrameLayout frame_invisFrame;
    private EditText hiddenText;
    private SeekBar textSizeSeekBar;
    private TextView debugText;
    private Context parentContext;
    private TextToImageOBJ textToImage;

    private List<Object> myAnnotations = new ArrayList<>();
    private Hashtable<String, TextAnnotationDetails> overlayToImage = new Hashtable<>(); // <String id, String text>

    private List<LatLng> drawPoints = new ArrayList<>();
    private double latitude;
    private double longitude;

    private LatLng circleCenter;
    private float[] circleRadius = new float[1];

    private int textSize = 50;
    private int textSizeMinimum = 10;
    private int myColor = Color.rgb(0,0,0);

    private Boolean userAnnotating = false;
    private String annotationMode = "Delete"; // Default annotation mode is line

    // For annotation preview feature
    private Polyline previewLine = null;
    private Circle previewCircle = null;
    private GroundOverlay previewText = null;
    private LatLng previewStartPos = null;

    private SQLiteDBHelper dbhelper; // make the database a singleton?

    private int lastAccessedTableID = -1;

    /***********************************************************************************************
     * Author: Tyler Kukkola 1/03/21
     * Purpose: Private constructor
     **********************************************************************************************/
    private Annotations(GoogleMap map, Context mContext, FrameLayout frameLayout)
    {
        // Set variables for this class
        this.map = map;
        frame_invisFrame = frameLayout;
        inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        hiddenText = (EditText) frame_invisFrame.findViewById(R.id.hiddenText);
        debugText = (TextView) frame_invisFrame.findViewById(R.id.debugText);
        textSizeSeekBar = (SeekBar) frame_invisFrame.findViewById(R.id.seekbar_textSize);
        parentContext = mContext;
        textToImage = new TextToImageOBJ();

        // Set listeners to Polylines, Circles, GroundOverlays
        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) { AnnotationObjectClicked("Polyline", polyline); }
        });

        map.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) { AnnotationObjectClicked("Circle", circle); }
        });

        map.setOnGroundOverlayClickListener(new GoogleMap.OnGroundOverlayClickListener() {
            @Override
            public void onGroundOverlayClick(GroundOverlay groundOverlay) { AnnotationObjectClicked("GroundOverlay", groundOverlay); }
        });
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 2/26/21
     * Purpose: Returns the single instance of the Annotations class. If no instance exists, will
     *      instantiate an instance and return that.
     **********************************************************************************************/
    public static Annotations GetInstance(GoogleMap map, Context mContext, FrameLayout frameLayout)
    {
        if (singleInstance == null)
            singleInstance = new Annotations(map, mContext, frameLayout);

        return singleInstance;
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 2/26/21
     * Purpose: Returns the single instance of the Annotations class. Will return null if
     *      no instance is available.
     **********************************************************************************************/
    public static Annotations GetInstance()
    {
        return singleInstance;
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 2/08/21
     * Purpose: Deletes an object from our annotation list
     **********************************************************************************************/
    private void DeleteAnnotationFromList(Object o)
    {
        if (o.getClass() == Polyline.class)
        {
            // Convert obj to it's class
            Polyline obj = (Polyline) o;

            // For every annotation
            for (int i = 0; i < myAnnotations.size(); i++)
            {
                // If annotation is correct class
                if (myAnnotations.get(i).getClass() == o.getClass())
                {
                    // If this annotation is what we are looking for
                    if (((Polyline) myAnnotations.get(i)).getId().equals(obj.getId()))
                    {
                        myAnnotations.remove(i);
                        break;
                    }
                }
            }
            obj.remove();
        }
        else if (o.getClass() == Circle.class)
        {
            Circle obj = (Circle) o;

            for (int i = 0; i < myAnnotations.size(); i++)
            {
                if (myAnnotations.get(i).getClass() == o.getClass())
                {
                    if (((Circle) myAnnotations.get(i)).equals(obj))
                    {
                        myAnnotations.remove(i);
                        break;
                    }
                }
            }
            obj.remove();
        }
        else if (o.getClass() == GroundOverlay.class)
        {
            GroundOverlay obj = (GroundOverlay) o;
            overlayToImage.remove(obj.getId()); // Remove it's image from our hashtable

            for (int i = 0; i < myAnnotations.size(); i++)
            {
                if (myAnnotations.get(i).getClass() == o.getClass())
                {
                    if (((GroundOverlay) myAnnotations.get(i)).getId().equals(obj.getId()))
                    {
                        myAnnotations.remove(i);
                        break;
                    }
                }
            }
            obj.remove();
        }
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/23/21
     * Purpose: When a annotation is clicked, call this function
     *      Right now it is only for deleting objects.
     **********************************************************************************************/
    private void AnnotationObjectClicked(String objectType, Object o)
    {
        if (!userAnnotating || !annotationMode.equals("Delete")) return; // Only check while annotating

        switch (objectType)
        {
            case "Polyline":
            case "Circle":
                DeleteAnnotationFromList(o);

                break;

            //SetDebugText("circle removed");
            case "GroundOverlay":
                if (((GroundOverlay) o).getTag() == null) break; // tyler 1/31/21 this is sketchy
                DeleteAnnotationFromList(o);

                break;
            default:
                Log.i("annotations", "unknown annotation object attempted to be deleted");
        }
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/03/21
     * Purpose: Return whether the user is currently annotating
     **********************************************************************************************/
    public boolean isUserAnnotating()
    {
        return userAnnotating;
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/03/21
     * Purpose: Private function to draw on the map using the drawPoints
     **********************************************************************************************/
    private void DrawLine()
    {
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.jointType(0);
        lineOptions.color(myColor);
        lineOptions.width(4);
        lineOptions.clickable(true);

        // Add all our draw points
        lineOptions.addAll(drawPoints);

        // Draw on the map
        myAnnotations.add(map.addPolyline(lineOptions));

        // Remove a point in our drawPoints (seems shady)
        drawPoints.remove(0);
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/07/21
     * Purpose: Private function to draw a circle on the map
     **********************************************************************************************/
    private void DrawCircle()
    {
        CircleOptions co = new CircleOptions()
                .fillColor(Color.TRANSPARENT)
                .strokeWidth(3f)
                .strokeColor(myColor)
                .center(circleCenter)
                .radius(circleRadius[0])
                .zIndex(10)
                .clickable(true);

        // Add circle to map
        myAnnotations.add(map.addCircle(co));
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/06/21
     * Purpose: Private function to draw Line annotation on map
     **********************************************************************************************/
    private void LineAnnotate(int eventAction)
    {
        switch (eventAction)
        {
            case MotionEvent.ACTION_DOWN:
                // Finger touches the screen
                drawPoints.add(new LatLng(latitude, longitude));
                previewStartPos = new LatLng(latitude, longitude);

                if (previewLine == null)
                    previewLine = map.addPolyline(new PolylineOptions()
                            .jointType(0)
                            .color(myColor)
                            .width(2)
                            .clickable(false));
                break;

            case MotionEvent.ACTION_MOVE:
                // Finger moves on the screen
                List<LatLng> points = new ArrayList<>();
                points.add(previewStartPos);
                points.add(new LatLng(latitude, longitude));
                previewLine.setPoints(points);
                break;

            case MotionEvent.ACTION_UP:
                // Finger leaves the screen
                drawPoints.add(new LatLng(latitude, longitude));
                previewLine.remove();
                previewLine = null;
                DrawLine();

                drawPoints = new ArrayList<>(); // Clean the array with our draw points in it
                break;
        }
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/07/21
     * Purpose: Circle Annotation on map
     **********************************************************************************************/
    private void CircleAnnotate(int eventAction)
    {
        switch (eventAction)
        {
            case MotionEvent.ACTION_DOWN:
                // Finger touches the screen
                circleCenter = new LatLng(latitude, longitude);
                previewStartPos = circleCenter;

                if (previewCircle == null)
                    previewCircle = map.addCircle(new CircleOptions()
                            .fillColor(Color.TRANSPARENT)
                            .strokeWidth(1f)
                            .strokeColor(myColor)
                            .center(circleCenter)
                            .radius(1f)
                            .clickable(true));
                break;

            case MotionEvent.ACTION_MOVE:
                // Finger moves on the screen
                Location.distanceBetween(circleCenter.latitude, circleCenter.longitude, latitude, longitude, circleRadius);
                previewCircle.setRadius(circleRadius[0]);
                break;

            case MotionEvent.ACTION_UP:
                // Finger leaves the screen
                Location.distanceBetween(circleCenter.latitude, circleCenter.longitude, latitude, longitude, circleRadius);
                previewCircle.remove();
                previewCircle = null;
                DrawCircle();
                break;
        }
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/10/21
     * Purpose: Text Annotation on map
     **********************************************************************************************/
    private void TextAnnotate(int eventAction)
    {
        switch (eventAction)
        {
            case MotionEvent.ACTION_DOWN:
                // Finger touches the screen
                LatLng coordinate = new LatLng(latitude, longitude);
                GroundOverlayOptions gOO = new GroundOverlayOptions()
                        .clickable(true)
                        .position(coordinate, 86f, 100f)
                        .bearing(0)
                        .visible(true);

                // Pan the camera to center the text (maybe use display metrics for cleaner tween)
                map.animateCamera(CameraUpdateFactory.newLatLng(coordinate), 300, null);

                // Listen to key events to update the text preview
                TextWatcher myWatcher = new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    { }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                    { }

                    @Override
                    public void afterTextChanged(Editable editable)
                    {
                        LatLng coordinate = new LatLng(latitude, longitude);
                        GroundOverlayOptions gOO = new GroundOverlayOptions()
                                .clickable(true)
                                .position(coordinate, 86f, 100f)
                                .bearing(0)
                                .visible(true);

                        String text = hiddenText.getText().toString();
                        if (text.length() < 1) // Will crash if string value ""
                            text = " ";

                        // Get user text size from xml object
                        textSize = textSizeSeekBar.getProgress() + 1;

                        // Set the image of the ground overlay to text
                        BitmapDescriptor textImage = textToImage.CreatePureTextIcon(text, myColor);
                        gOO.image(textImage);
                        float sizeScale = 1/textToImage.textHeight;
                        float offset = textSize;
                        // we are normalizing the scale of the height and taking that to scale the text onto map
                        gOO.position(coordinate, sizeScale * textToImage.textWidth * offset, sizeScale * textToImage.textHeight * offset);

                        // Remove previous preview overlay, add new overlay
                        if (previewText != null) previewText.remove();
                        previewText = map.addGroundOverlay(gOO);
                    }
                };
                hiddenText.addTextChangedListener(myWatcher);

                // Create keyboard connections
                hiddenText.setOnEditorActionListener(new EditText.OnEditorActionListener()
                {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
                    {
                        if ( i == 6 )// TODO: find a list of action codes instead of hardcoding 6
                        {
                            if (hiddenText.getText().length() < 1) // Will crash if string value ""
                                return false;

                            // Get user text size from xml object
                            textSize = textSizeSeekBar.getProgress() + 1;

                            // Set the image of the ground overlay to text
                            BitmapDescriptor textImage = textToImage.CreatePureTextIcon(hiddenText.getText().toString(), myColor);
                            gOO.image(textImage);
                            float sizeScale = 1/textToImage.textHeight;
                            float offset = textSize;
                            // we are normalizing the scale of the height and taking that to scale the text onto map
                            gOO.position(coordinate, sizeScale * textToImage.textWidth * offset, sizeScale * textToImage.textHeight * offset);

                            // Create the ground overlay
                            GroundOverlay myOverlay = map.addGroundOverlay(gOO);
                            myOverlay.setTag("textAnnotation");
                            myAnnotations.add(myOverlay);

                            // Keep a reference to it's text
                            TextAnnotationDetails textDetails = new TextAnnotationDetails(hiddenText.getText().toString(), textSize, myColor);
                            overlayToImage.put(myOverlay.getId(), textDetails);

                            // Reset the hidden text
                            hiddenText.setText("");
                            hiddenText.setOnEditorActionListener(null);
                            hiddenText.removeTextChangedListener(myWatcher);
                        }
                        return false; // if you return true, handle the keyboard and focuses yourself
                    }
                });

                // open keyboard
                hiddenText.setFocusableInTouchMode(true);
                hiddenText.requestFocus();
                inputManager.showSoftInput(hiddenText, InputMethodManager.SHOW_IMPLICIT);

                break;

            case MotionEvent.ACTION_MOVE:
                // Finger moves on the screen
                break;

            case MotionEvent.ACTION_UP:
                // Finger leaves the screen
                break;
        }
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/03/21
     * Purpose: Private function allowing user to draw on the map
     *      1/06/21 Tyler Kukkola added if statement and moved switch statement into function calls
     **********************************************************************************************/
    @SuppressLint("ClickableViewAccessibility")
    private void EnableAnnotations()
    {
        // Start with a clean draw points array
        drawPoints = new ArrayList<>();

        // Set a listener on our invisible framelayout to capture user touch input
        // (View v, MotionEvent event)

        frame_invisFrame.setOnTouchListener((v, event) ->
        {
            //SetDebugText("On touch " + Integer.toString(event.getAction()));

            float x = event.getX();
            float y = event.getY();

            int x_co = Math.round(x);
            int y_co = Math.round(y);

            // Get latitude longitude
            LatLng latLng = map.getProjection().fromScreenLocation(new Point(x_co, y_co));
            latitude = latLng.latitude;
            longitude = latLng.longitude;


            // Check what we are doing
            int eventAction = event.getAction();
            switch (annotationMode)
            {
                case "Line":
                    LineAnnotate(eventAction);
                    break;
                case "Circle":
                    CircleAnnotate(eventAction);
                    break;
                case "Text":
                    TextAnnotate(eventAction);
                    break;
                case "Delete":
                    return false; // Has been Bug-fixed: must return false for event to fall deeper
                default:
                    //SetDebugText("invalid annotation mode");
                    break;
            }

            return true;
        });

        // For lines/circles to update while dragging
        frame_invisFrame.setOnDragListener(new View.OnDragListener()
        {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent)
            {
               // SetDebugText("On drag " + Integer.toString(dragEvent.getAction()));

                Log.i(null,"dragging some object rn");
                return false;
            }
        });
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/03/21
     * Purpose: Private function to disable user annotations
     **********************************************************************************************/
    @SuppressLint("ClickableViewAccessibility")
    private void DisableAnnotations()
    {
        frame_invisFrame.setOnTouchListener(null);
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/03/21
     * Purpose: Public function toggling user ability to annotate the map
     **********************************************************************************************/
    public void ToggleUserAnnotating()
    {
        if (!userAnnotating)
        {
            userAnnotating = true;
            map.getUiSettings().setAllGesturesEnabled(false); // Disable map interactions
            EnableAnnotations();
            //SetDebugText("annotations enabled");
        }
        else
        {
            userAnnotating = false;
            map.getUiSettings().setAllGesturesEnabled(true); // Enable map interactions
            DisableAnnotations();
            //SetDebugText("annotations disabled");
        }
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/07/21
     * Purpose: public function to change the current annotation mode
     *      This is accessed by MainActivity to change annotation modes on button click
     **********************************************************************************************/
    public void ChangeAnnotationMode(String newMode)
    {
        annotationMode = newMode;
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 1/07/21
     * Purpose: public function to change the current annotation mode
     *      This is accessed by MainActivity to change annotation modes on button click
     **********************************************************************************************/
    public void SetAnnotateColor(int newColor)
    {
        myColor = newColor;
    }

    /***********************************************************************************************
     * Author: Tyler Kukkola 5/2/21
     * Purpose: public function to change the last accessed table ID
     *      This is so save annotations and load annotations knows what the current map is
     **********************************************************************************************/
    public void SetLastAccessedTableID(int id)
    {
        lastAccessedTableID = id;
    }

    /***********************************************************************************************
     * Author: Tyler Kukkola 5/2/21
     * Purpose: public function to get the current map id in the annotations module
     *      was mostly for debugging, now actually being used
     **********************************************************************************************/
    public int GetCurrentMapID()
    {
        return lastAccessedTableID;
    }

    /***********************************************************************************************
     * Author: Tyler Kukkola 2/07/21
     * Purpose: public function to clear all annotations from screen
     **********************************************************************************************/
    public void ClearAllAnnotations()
    {
        // For every annotation, remove it from the google map
        for (int i = 0; i < myAnnotations.size(); i++)
        {
            if (myAnnotations.get(i).getClass() == Polyline.class)
            {
                ((Polyline) myAnnotations.get(i)).remove();
            }
            else if (myAnnotations.get(i).getClass() == Circle.class)
            {
                ((Circle) myAnnotations.get(i)).remove();
            }
            else if (myAnnotations.get(i).getClass() == GroundOverlay.class)
            {
                ((GroundOverlay) myAnnotations.get(i)).remove();
            }
        }

        // Clear everything from annotations
        myAnnotations.clear();
        overlayToImage.clear();
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 2/07/21
     * Purpose: Public function to save annotations to database
     *      Called when clicking save drawing button on xml
     * Returns: 0 on success, non-zero on fail
     **********************************************************************************************/
    // https://stackoverflow.com/questions/1243181/how-to-store-object-in-sqlite-database
    public int SaveAnnotations(int imageId)
    {
        // Needs valid ID
        if (imageId < 0)
            return -1;

        // Get object describing all the annotations
        AnnotationData myAnnotationData = new AnnotationData(myAnnotations, overlayToImage);

        // Save this object to the database
        try
        {
            // Convert object to bytes idk
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(myAnnotationData);
            byte[] bytes = baos.toByteArray();

            // Get db object
            dbhelper = new SQLiteDBHelper(parentContext, 1);

            // Insert annotations to database
            dbhelper.InsertAnno(bytes, imageId); // get db to insert some annotations
        }
        catch (IOException e)
        {
            Log.i(null, "no");
        }

        return 0;
    }

    /***********************************************************************************************
     * Author: Tyler Kukkola 2/07/21
     * Purpose: Public function to load annotations from database
     *      This is should be auto called when loading images from database
     **********************************************************************************************/
    public int LoadAnnotations(int parent_id)
    {
        // Needs valid ID
        if (parent_id < 0)
            return -1;

        if (dbhelper == null)
            dbhelper = new SQLiteDBHelper(parentContext, 1);

        // This is what we are getting out of database
        AnnotationData myData = null;

        // Attempt to pull data from database
        try
        {
            myData = dbhelper.LoadAnno(parent_id);
        }
        catch(IOException except)
        {
            Log.i(null, "no");
        }
        catch (ClassNotFoundException except)
        {
            Log.i(null, "no");
        }

        // Clear every annotation on the screen
        ClearAllAnnotations();

        // Stop here if nothing was found
        if (myData == null)
            return -1;

        // Unpack the descriptions from AnnotationData
        // while setting the myAnnotations object and
        // drawing relevant annotations to screen.
        List<Object> returnedData = myData.LoadData(map);

        myAnnotations = (List<Object>) returnedData.get(0);
        overlayToImage = (Hashtable<String, TextAnnotationDetails>) returnedData.get(1);

        return 0;
    }
}


/***********************************************************************************************
 * Author: Tyler Kukkola 1/07/21
 * Purpose: Object that handles turning annotations into a savable format
 *
 * this object takes the annotation list and creates a "description" for
 * every annotation. We need to do this because the annotation objects
 * (Polyline, Circle, GroundOverlay, etc) are not "Serializable", basically
 * meaning they can not be written to the database as a blob. This class
 * allows for saving annotations in the easiest way possible.
 *
 * there is another class in this class, it's suppose to be a struct but this
 * is java so it's a class.
 *
 * public methods:
 *      AnnotationData(List<Object> myAnnotations)
 *      List<Object> LoadData(GoogleMap map)
 *
 * private methods:
 *      none
 **********************************************************************************************/
class AnnotationData implements Serializable
{
    // List of every annotation description
    private List<dataMember> myData;

    // This class describes a Polyline/Circle/GroundOverlay
    static class dataMember implements Serializable
    {
        public String objClassName;

        public int color;
        public float width;
        public double line_lat_1; // First point of polyline
        public double line_long_1;
        public double line_lat_2; // Second point of polyline
        public double line_long_2;

        public double center_lat;
        public double center_long;
        public double radius;

        public double overlay_Lat;
        public double overlay_Long;
        public float bearing;
        public String text;
        public int text_size;

        dataMember(Object obj, Hashtable<String, TextAnnotationDetails> texts)
        {
            if (obj.getClass() == Polyline.class)
            {
                Polyline poly = (Polyline) obj;

                // Fill class members with obj data
                objClassName = "Polyline";
                color = poly.getColor();
                width = poly.getWidth();
                line_lat_1 = poly.getPoints().get(0).latitude;
                line_long_1 = poly.getPoints().get(0).longitude;
                line_lat_2 = poly.getPoints().get(1).latitude;
                line_long_2 = poly.getPoints().get(1).longitude;
            }
            else if (obj.getClass() == Circle.class)
            {
                Circle cir = (Circle) obj;

                // Fill class members with obj data
                objClassName = "Circle";
                color = cir.getStrokeColor();
                center_lat = cir.getCenter().latitude;
                center_long = cir.getCenter().longitude;
                radius = cir.getRadius();
                width = cir.getStrokeWidth();
            }
            else if (obj.getClass() == GroundOverlay.class)
            {
                GroundOverlay go = (GroundOverlay) obj;

                // Fill class members with obj data
                objClassName = "GroundOverlay";
                overlay_Lat = go.getPosition().latitude;
                overlay_Long = go.getPosition().longitude;
                bearing = go.getBearing();

                // Pull the related text from the hashtable
                text = texts.get(go.getId()).text;
                text_size = texts.get(go.getId()).textSize;
                color = texts.get(go.getId()).textColor;
            }
        }
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 2/07/21
     * Purpose: Constructor
     *      Requires the myAnnotations list from Annotations object
     *      Requires the hashtable that connects text annotations to their actual text
     **********************************************************************************************/
    AnnotationData(List<Object> myAnnotations, Hashtable<String, TextAnnotationDetails> texts)
    {
        // Initialize our list
        myData = new ArrayList<>();

        // Walk through every annotation and create an object that describes it
        for (int i = 0; i < myAnnotations.size(); i++)
        {
            // Create object describing this polyline
            dataMember member = new dataMember(myAnnotations.get(i), texts);

            // Put member object into annotation data list
            myData.add(member);
        }
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 2/07/21
     * Purpose: Public function to "Unpack" the data into google maps and returns obj that holds
     *          our annotations and the hashtable of the text annotations.
     **********************************************************************************************/
    public List<Object> LoadData(GoogleMap map)
    {
        // We are returning both the annotations and the hashtable associated with the texts annotations
        List<Object> retVal = new ArrayList<>();
        List<Object> listOfAnnotations = new ArrayList<>();
        Hashtable<String, TextAnnotationDetails> hashOfTexts = new Hashtable<>();

        retVal.add(0, listOfAnnotations);
        retVal.add(1, hashOfTexts);

        // Grab the object that does text to image
        TextToImageOBJ textToImage = new TextToImageOBJ();

        // For every data
        for (int i = 0; i < myData.size(); i++)
        {
            String cName = myData.get(i).objClassName;
            switch (cName)
            {
                case "Polyline":
                {
                    dataMember data = myData.get(i);

                    // Get the saved points
                    List<LatLng> coords = new ArrayList<>();
                    coords.add(new LatLng(data.line_lat_1, data.line_long_1));
                    coords.add(new LatLng(data.line_lat_2, data.line_long_2));

                    // Create object options from data
                    PolylineOptions polyOpts = new PolylineOptions()
                            .addAll(coords)
                            .jointType(0)
                            .color(data.color)
                            .width(data.width)
                            .clickable(true);

                    // Add to map
                    Polyline poly = map.addPolyline(polyOpts);

                    // Put this into our list of objects
                    listOfAnnotations.add(poly);
                    break;
                }
                case "Circle":
                {
                    dataMember data = myData.get(i);

                    // Get the saved points
                    LatLng center = new LatLng(data.center_lat, data.center_long);

                    // Create object options from data
                    CircleOptions co = new CircleOptions()
                            .fillColor(Color.TRANSPARENT)
                            .strokeWidth(data.width)
                            .strokeColor(data.color)
                            .center(center)
                            .radius(data.radius)
                            .zIndex(10)
                            .clickable(true);

                    // Add to map
                    Circle circle = map.addCircle(co);

                    // Put this into our list of objects
                    listOfAnnotations.add(circle);
                    break;
                }
                case "GroundOverlay":
                {
                    dataMember data = myData.get(i);

                    // Get the saved points
                    LatLng pos = new LatLng(data.overlay_Lat, data.overlay_Long);

                    // The text image
                    BitmapDescriptor textImage = textToImage.CreatePureTextIcon(data.text, data.color);

                    // Create object options from data
                    GroundOverlayOptions oo = new GroundOverlayOptions()
                            .clickable(true)
                            .position(pos, textToImage.textWidth, textToImage.textHeight)
                            .bearing(data.bearing)
                            .visible(true)
                            .image(textImage);

                    // Set text size
                    float sizeScale = 1/textToImage.textHeight;
                    float offset = data.text_size;
                    // we are normalizing the scale of the height and taking that to scale the text onto map
                    oo.position(pos, sizeScale * textToImage.textWidth * offset, sizeScale * textToImage.textHeight * offset);

                    // Add to map
                    GroundOverlay overlay = map.addGroundOverlay(oo);
                    overlay.setTag("textAnnotation");

                    // Add this to our returning texts
                    TextAnnotationDetails details = new TextAnnotationDetails(data.text, data.text_size, data.color);
                    hashOfTexts.put(overlay.getId(), details);

                    // Put this into our list of objects
                    listOfAnnotations.add(overlay);
                    break;
                }
                default:
                {
                    Log.i("Annotations", "unknown object being read from database");
                    break;
                }
            }
        }

        return retVal;
    }
}


/***********************************************************************************************
 * Author: Tyler Kukkola 2/14/21
 * Purpose: Object that converts text to images
 *
 * This was made into a class because both Annotations and AnnotationData needs this code
 *
 **********************************************************************************************/
class TextToImageOBJ
{
    public float textHeight;
    public float textWidth;
    public int textColor;

    TextToImageOBJ()
    { textHeight = 0; textWidth = 0; textColor = 0;}

    public BitmapDescriptor CreatePureTextIcon(String text, int myColor)
    {
        Paint textPaint = new Paint();
        textPaint.setTextSize(50);

        textColor = myColor;
        textPaint.setColor(textColor);

        Paint.FontMetrics fm = textPaint.getFontMetrics();
        textWidth = textPaint.measureText(text);
        textHeight = fm.descent - fm.ascent;

        Bitmap image = Bitmap.createBitmap((int) textWidth, (int) textHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        canvas.translate(0, textHeight - fm.descent);

        canvas.drawText(text, 0, 0, textPaint);

        return BitmapDescriptorFactory.fromBitmap(image);
    }
}


/***********************************************************************************************
 * Author: Tyler Kukkola 2/20/21
 * Purpose: Object that holds information for a specific text annotation
 *
 * This is a class because text annotations needs to somehow store it's string and textSize,
 * eventually needing more parameters such as fontType and textColor so making an object that
 * stores them all in one place seems like the best option.
 *
 **********************************************************************************************/
class TextAnnotationDetails
{
    public String text;
    public int textSize;
    public int textColor;

    TextAnnotationDetails(String text, int textSize, int textColor)
    {
        this.text = text;
        this.textSize = textSize;
        this.textColor = textColor;
    }
}