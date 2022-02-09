/***********************************************************************************************
 * Author:
 * Purpose:
 * Modifications:
 *      Jason Adam 1/19/21 - Updated the groundOverlay function to include a transparency param.
 **********************************************************************************************/
package com.dodocrusaiders.utilitytracker;

import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;


public class Layer<map>
{
    private static LatLng pos;
    private static GroundOverlayOptions newMap;
    private static float overlayWidth;
    private static float bearing;
    private static float img_x;
    private static float img_y;
    private static float imageHeight;


    /*******************************************************
     * Author: Nick Springer 11/15/2020
     * Purpose: toggle visibility of overlay
     ********************************************************/
    protected static void toggleVisibility(GoogleMap map, GroundOverlay overlay)
    {
        if(overlay.isVisible())
        {
            overlay.setVisible(false);
        }
        else
        {
            overlay.setVisible(true);
        }
    }
    /*******************************************************
     * Author: Nick Springer 11/15/2020
     * Purpose: remove overlay from map
     * may need to be done in main activity
     ********************************************************/
    protected static void removeImage(GroundOverlay overlay)
    {
        overlay.remove();
    }

    /*******************************************************
     * Author: Nick Springer 11/15/2020
     * Purpose: Overlay Image onto map
     *
     ********************************************************/
    protected static GroundOverlay addMap(GoogleMap Map, Bitmap img, float xMap, float yMap, float xImg, float yImg, float bearingMap, float bearingImg, float transparency, float width)
    {
        //TODO: maybe find bearing/width by using anchors on map and how they correspond with the image? LatLng gotten by placed marker

        img_x = xImg;
        img_y = yImg;

        //getting rotation needed for image to match rotation on map
        bearing = (bearingImg - bearingMap);

        //position of image on map
        pos = new LatLng(xMap, yMap);
        //width of image
        overlayWidth = width;
        //rotation of image in degrees, positive=clockwise negative=counter-clockwise
        newMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(img))           //images stored as png's in res\mipmap-hdpi
                .position(pos, overlayWidth)                                               //lat long for image anchor
                .bearing(bearing)                                                   //the angle of the image relative to north
                .visible(true)                                                      //visibility of image
                .anchor(img_x,img_y)                                                //position of anchor on image from 0.0 to 1.0 (starting at left or top respectively)
                .zIndex(imageHeight);                                                //order images are placed on map(lower numbers drawn first, will be under higher numbers)

        return Map.addGroundOverlay(newMap);
    }
}
