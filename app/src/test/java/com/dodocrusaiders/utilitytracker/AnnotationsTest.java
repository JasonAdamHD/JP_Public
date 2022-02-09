package com.dodocrusaiders.utilitytracker;

import android.graphics.Color;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;

import org.junit.Test;
import org.w3c.dom.Text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


//**********************************************************
// Author: Tyler Kukkola
// Purpose: Add unit tests for the annotations
//
// reference video about unit tests/how to get started:
//      https://www.youtube.com/watch?v=W0ag98EDhGc
public class AnnotationsTest
{
    Annotations myAnnotations = Annotations.GetInstance();
    TextToImageOBJ textToImageOBJ = new TextToImageOBJ();
    TextAnnotationDetails annoDetails = new TextAnnotationDetails("hello world", 10, Color.CYAN);

    @Test
    public void BeforeAnnotationsInstancedReturnsNull()
    {
        assertNull(myAnnotations);
    }

    @Test
    public void TextToImageObjStartsWith0AsDefaultValues()
    {
        assertEquals(0, textToImageOBJ.textColor);
        assertEquals(0, textToImageOBJ.textHeight, 1);
        assertEquals(0, textToImageOBJ.textWidth, 1);
    }

    @Test
    public void TextAnnotationDetailMembersStartAsExpected()
    {
        assertEquals("hello world", annoDetails.text);
        assertEquals(10, annoDetails.textSize);
        assertEquals(Color.CYAN, annoDetails.textColor);
    }

    @Test
    public void NoCurrentMapReturnsNeg()
    {
        int val = myAnnotations.GetCurrentMapID();

        assertEquals(-1, val);
    }

}