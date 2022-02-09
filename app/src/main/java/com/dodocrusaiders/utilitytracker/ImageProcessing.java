////////////////////////////////////////////////////////////////////////////////////////////////////
// Author:  Jason Adam (2/5/2021)
// Purpose: Process images so that it strips their background color and replaces it with transparency
//
////////////////////////////////////////////////////////////////////////////////////////////////////
package com.dodocrusaiders.utilitytracker;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class ImageProcessing extends AppCompatActivity
{
    // TODO: NEED TO ADD ABILITY TO INCREASE TOLERANCE!!!
    private Button ReturnButton;
    private ImageView imageView;
    private static final int rThresholdFinal = 245;
    private static final int gThresholdFinal = 245;
    private static final int bThresholdFinal = 245;
    private static final int tolerance = 10;
    private static int rThreshold = rThresholdFinal; //0x00E10000; //0xE1; //225;
    private static int gThreshold = gThresholdFinal; //0x0000E100;//0xE1; //225;
    private static int bThreshold = bThresholdFinal; //0x000000E1;//0xE1; //225;
    private static int thresholdColor = 0xFF000000 + rThreshold + gThreshold + bThreshold;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  Author: Jason Adam
    //  Purpose: Given a Bitmap return a bitmap that has the background set to transparent and the
    //  rest of the foreground set to the same color.
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static Bitmap ConvertFromBitmapToBinaryImage(Bitmap bitmapOrig) throws SQLException, IOException
    {

        Bitmap bitmap = Bitmap.createBitmap(bitmapOrig.getWidth(), bitmapOrig.getHeight(), Bitmap.Config.ARGB_8888);
        int red = 0;
        int green = 0;
        int blue = 0;
        int[] bitmapPixelArray = new int[bitmap.getHeight() * bitmap.getWidth()]; // create an array the size of the image
        bitmapOrig.getPixels(bitmapPixelArray, 0, bitmapOrig.getWidth(), 0, 0, bitmapOrig.getWidth(), bitmapOrig.getHeight());
        //set the pixel array to the pixels from the image so that we can change them.
        int imgWidth = bitmapOrig.getWidth(); //get the image width for later
        int imgHeight = bitmapOrig.getHeight();
        // Run through all of the pixels for the bitmap and generate a new image based off of the
        // original bitmap with the background color set to transparent and all other foreground
        //  elements as black (or the same color need to add setting for that.)
        //String DEBUG_ARRAY_STRING = "[";
        for (int width = 0; width < imgWidth; width++)
        {
            //DEBUG_ARRAY_STRING.concat("[");
            for (int height = 0; height < imgHeight; height++)
            {
                int pixelIndex = width + (height * imgWidth); //get the index of the pixel that will be looked at
                // Get the individual colors of the pixel and store them in their own value
                red = Color.red(bitmapPixelArray[pixelIndex]);
                green = Color.green(bitmapPixelArray[pixelIndex]);
                blue = Color.blue(bitmapPixelArray[pixelIndex]);
                // If the color picker filter is color is -1 then their is no custom filter set
                // If their is a custom filter set make sure we are using the latest version
                if(ColorPicker.GetFilterColor() != -1)
                {
                    rThreshold = Color.red(ColorPicker.GetFilterColor());
                    gThreshold = Color.green(ColorPicker.GetFilterColor());
                    bThreshold = Color.blue(ColorPicker.GetFilterColor());
                }
                // Check if the pixel is within the threshold range and its tolerance.
                if (red > rThreshold - tolerance && red < rThreshold + tolerance &&
                    green > gThreshold - tolerance && green < gThreshold + tolerance &&
                    blue > bThreshold - tolerance && blue < bThreshold + tolerance)
                {   // Set to transparent if in range (in range means its the background.)
                    bitmapPixelArray[pixelIndex] = 0x00000000;
                }
                else // Currently commented out to allow for same color of foreground as before the image processing began.
                {   // Set to black if not in range (not in range means the foreground.)
                    //bitmapPixelArray[pixelIndex] = 0xFF000000;
                }
            }
            bitmap.setPixels(bitmapPixelArray, 0, bitmapOrig.getWidth(), 0, 0, bitmapOrig.getWidth(), bitmapOrig.getHeight());

        }
        return bitmap;
    }

    // This will be used for part of the UI for where the user is able to change the removal
    // color so that they can revert to the default if they want to.
    public void ResetColorFilterToDefault()
    {
        rThreshold = rThresholdFinal;
        gThreshold = gThresholdFinal;
        bThreshold = bThresholdFinal;
        ColorPicker.ResetFilterColor();
    }
}
