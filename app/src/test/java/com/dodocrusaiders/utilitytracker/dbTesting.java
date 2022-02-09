package com.dodocrusaiders.utilitytracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.FrameLayout;

import androidx.appcompat.view.ContextThemeWrapper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;

import org.junit.Test;
import org.w3c.dom.Text;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;


//**********************************************************
// Author: Tyler Kukkola
// Purpose: Add unit tests for database
public class dbTesting
{
    SQLiteDBHelper dbhelper = new SQLiteDBHelper(null, 1);
    @Test
    public void InsertAnnotationDataBadIdReturnFalse()
    {
        boolean ret = dbhelper.InsertAnno(null, -1);
        assertFalse(ret);
    }

    @Test
    public void InsertDataBadIdReturnFalse()
    {
        boolean ret = dbhelper.insertBitmapData(-1,
                1,
                1,
                1,
                1,
                1,
                "test",
                null);
        assertFalse(ret);
    }

    @Test
    public void InsertBitmapDataBadIdReturnFalse()
    {
        boolean ret = dbhelper.insertData(-1,
                1,
                1,
                1,
                1,
                1,
                "test");
        assertFalse(ret);
    }

    /* "getReadableDatabase not mocked", must mock to run this type of test
    @Test
    public void LoadAnnotationDataBadIdReturnNull()
    {
        AnnotationData ret = null;
        try
        {
            ret = dbhelper.LoadAnno(-1);
        } catch (IOException e)
        {
        } catch (ClassNotFoundException e)
        {
        }
        assertNull(ret);
    }
    */

}