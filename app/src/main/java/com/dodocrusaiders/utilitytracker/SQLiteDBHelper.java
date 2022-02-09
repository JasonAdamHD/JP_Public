package com.dodocrusaiders.utilitytracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.ViewDebug;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class SQLiteDBHelper extends SQLiteOpenHelper
{
    private static final String DBName = "image_layers";
    private static final String tableName = "layer_table";
    private static final String dropTable = "DROP TABLE IF EXISTS " + tableName;
    private static final String UID = "ID";

    /***********************************************************************************************
     * Author: Shawn gibbons 1/4/21
     * Purpose: default CTOR for the SQLiteDBHelper and creates a database for use.
     ***********************************************************************************************/
    public SQLiteDBHelper(@Nullable Context context, int version)
    {
        super(context, DBName, null, version);
    }

    /***********************************************************************************************
     * Author: Shawn gibbons 1/8/21
     * Purpose: handles the onCreate case and creates a table for use.
     ***********************************************************************************************/
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        // Create database table
        database.execSQL("CREATE TABLE " + tableName + "(ID integer primary key, LAT FLOAT, LONG FLOAT, POS_X FLOAT, POS_Y FLOAT, BEARING FLOAT, IMG blob not null, IMG_NAME TEXT)");

        // Create a table for the annotations
        database.execSQL("CREATE TABLE annotations" + "(annotation BLOB not null, parent_id INT)");

        // Create a table for markers with custom data.
        database.execSQL("CREATE TABLE markers" + "(marker_id INT, lat FLOAT, lang FLOAT, name TEXT, info TEXT, image BLOB)");
    }

    /***********************************************************************************************
     * Author: Shawn gibbons 1/5/21
     * Purpose: handles the onUpgrade case and drops the previously created table if it exists.
     ***********************************************************************************************/
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        database.execSQL(dropTable);
        onCreate(database);
    }

    /***********************************************************************************************
     * Author: Shawn gibbons 1/13/21
     * Purpose: inserts data and image into database.
     ***********************************************************************************************/
    public boolean insertData(int idNumber, float latitude, float longitude, float posX, float posY, float bearing, String imgName)
    {
        if (idNumber < 0)
            return false;

        // Write data to database
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete previous data at idNumber, since adding new values at existing idNumbers don't overwrite
        db.execSQL("delete from " + tableName + " WHERE ID IS " + idNumber);

        try
        {
            FileInputStream FSInput = new FileInputStream(imgName); // Opens file input stream to gather file data to break into a byte array

            byte[] imgDataArr = new byte[FSInput.available()];
            FSInput.read(imgDataArr);
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", idNumber);
            contentValues.put("LAT", latitude);
            contentValues.put("LONG", longitude);
            contentValues.put("POS_X", posX);
            contentValues.put("POS_Y", posY);
            contentValues.put("BEARING", bearing);
            contentValues.put("IMG_NAME", imgName);
            contentValues.put("IMG", imgDataArr);
            db.insert(tableName, null, contentValues);
            FSInput.close();

            // Save the current annotations with a reference to this map
            //Annotations myAnnotations = Annotations.GetInstance();
            //myAnnotations.SaveAnnotations(idNumber);

            return true;
        }
        catch(IOException except)
        {
            except.printStackTrace();
            return false;
        }
    }

    /***********************************************************************************************
     * Author: Shawn gibbons 2/22/21
     * Purpose: inserts data and image that is in Bitmap format into database.
     ***********************************************************************************************/
    public boolean insertBitmapData(int idNumber, float latitude, float longitude, float posX, float posY, float bearing, String imgName, Bitmap img)
    {
        if (idNumber < 0)
            return false;

        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            byte[] imgDataArr = byteStream.toByteArray();

            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", idNumber);
            contentValues.put("LAT", latitude);
            contentValues.put("LONG", longitude);
            contentValues.put("POS_X", posX);
            contentValues.put("POS_Y", posY);
            contentValues.put("BEARING", bearing);
            contentValues.put("IMG", imgDataArr);
            contentValues.put("IMG_NAME", imgName);
            db.insert(tableName, null, contentValues);

            return true;
        }
        catch(BufferUnderflowException except)
        {
            except.printStackTrace();
            return false;
        }
    }

    /***********************************************************************************************
     * Author: Tyler Kukkola 2/05/21
     * Purpose: Inserts current annotations into the database
     ***********************************************************************************************/
    public boolean InsertAnno(byte[] bytes, int parent_id)
    {
        if (parent_id < 0)
            return false;

        SQLiteDatabase db = this.getWritableDatabase();

        try
        {
            // Delete the previous annotations at this id
            db.execSQL("delete from annotations WHERE parent_id IS " + parent_id);

            // Create content value obj and insert it into database
            ContentValues cv = new ContentValues();
            cv.put("annotation", bytes); // This column gets the blob that is our AnnotationData class
            cv.put("parent_id", parent_id); // This column gets the parent_id of the image these annotations are attached to
            db.insertOrThrow("annotations", null, cv);
        }
        catch(SQLException except)
        {
            except.printStackTrace();
            return false;
        }
        return true;
    }


    /***********************************************************************************************
     * Author: Tyler Kukkola 2/28/21
     * Purpose: Loads annotations at parent_id key from database
     *          3/06/21 - confirmed working
     ***********************************************************************************************/
    public AnnotationData LoadAnno(int parent_id) throws IOException, ClassNotFoundException
    {
        // Get database
        SQLiteDatabase db = this.getReadableDatabase();

        // This is what we are getting out of database
        AnnotationData myData = null;

        if (parent_id < 0)
            return myData;

        // Create a cursor object which helps navigate the query
        Cursor cursor = db.query("annotations", null, "parent_id="+parent_id, null, null, null, null);

        // Make sure query isn't empty by using moveToNext() to check
        if (cursor.moveToNext())
        {
            // Get the bytes from the blob holding our data
            byte[] buff = cursor.getBlob(0);

            ByteArrayInputStream baip = new ByteArrayInputStream(buff);
            ObjectInputStream ois = new ObjectInputStream(baip);
            myData = (AnnotationData) ois.readObject(); // Read bytes back into AnnotationData
        }
        else
        {
            // No saved annotation data found in database (This should never be able to execute)
            myData = null;
        }

        // Close the cursor
        cursor.close();

        return myData;
    }


    /***********************************************************************************************
     * Author: Shawn gibbons 1/25/21
     * Purpose: retrieves data and image into database
     ***********************************************************************************************/
    public Bitmap getData(int idNumber)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Bitmap btmp = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE ID=?", new String[]{String.valueOf(idNumber)});
        if (cursor.moveToNext())
        {
            byte[] imag = cursor.getBlob(6);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            btmp = BitmapFactory.decodeByteArray(imag, 0, imag.length, opts);
        }
        return btmp;
    }

    /***********************************************************************************************
     * Author: Shawn gibbons 2/12/21
     * Purpose: retrieves all images from database
     ***********************************************************************************************/
    public Bitmap[] getAllImgs()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        int size = getSize(),
        i = 0;
        Bitmap[] btmpArr = new Bitmap[size];
        Cursor res = db.rawQuery("SELECT * FROM " + tableName, null);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

        res.moveToFirst();
        while(!res.isAfterLast())
        {
            byte[] temp = res.getBlob(6);
            btmpArr[i] = BitmapFactory.decodeByteArray(temp, 0, temp.length, opts);
            i++;
            res.moveToNext();
        }

        return btmpArr;
    }

    /***********************************************************************************************
     * Author: Shawn gibbons 2/25/21
     * Purpose: delete an item from the database
     ***********************************************************************************************/
    public void deleteItem(int toDelete)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, "ID=" + toDelete, null);
        db.execSQL("UPDATE " + tableName + " SET " + UID + " = (" + UID + "-1) WHERE " + UID + " > " + Integer.toString(toDelete));

        // Tyler Kukkola April 30, 2021
        // When deleting a map from the database, should also delete the related annotations
        db.delete("annotations", "parent_id=" + toDelete, null);
        db.execSQL("UPDATE " + "annotations" + " SET " + "parent_id" + " = (" + "parent_id" + "-1) WHERE " + "parent_id" + " > " + Integer.toString(toDelete));

        // Tyler Kukkola May 15, 2021
        // Need to also check if we need to update the current map id in the annotations
        Annotations annotations = Annotations.GetInstance();
        if (annotations.GetCurrentMapID() > toDelete)
            annotations.SetLastAccessedTableID(annotations.GetCurrentMapID() - 1);
    }

    /***********************************************************************************************
     * Author: Shawn gibbons 2/22/21
     * Purpose: get size of database
     ***********************************************************************************************/
    public int getSize()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return (int)DatabaseUtils.queryNumEntries(db, tableName);
    }

    public void populateMarkers() throws android.database.CursorIndexOutOfBoundsException
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM markers",null);
        if(cursor.getCount() != 0)
        {
            cursor.moveToFirst();
            while(cursor.getPosition() != cursor.getCount())
            {
                CustomMarkers.MarkerData data = new CustomMarkers.MarkerData(
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("info")),
                        null,
                        new LatLng(cursor.getFloat(cursor.getColumnIndex("lat")),
                                cursor.getFloat(cursor.getColumnIndex("lang"))),
                        cursor.getPosition()
                );
                CustomMarkers.CreateMarker(data);
                CustomMarkers.marker_id++;
                cursor.moveToNext();
            }
        }
    }

    public boolean insertMarker(int idNumber, float latitude, float longitude, String name, String info, Bitmap img)
    {
        //database.execSQL("CREATE TABLE markers" + "(marker_id INT, lat FLOAT, lang FLOAT, name TEXT, info TEXT, image BLOB)");
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            byte[] imgDataArr;
            if (img != null)
            {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                imgDataArr = byteStream.toByteArray();
            }
            else
            {
                imgDataArr = null;
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put("marker_id", idNumber);
            contentValues.put("lat", latitude);
            contentValues.put("lang", longitude);
            contentValues.put("name", name);
            contentValues.put("info", info);
            contentValues.put("image", imgDataArr);
            db.insert("markers", null, contentValues);
            return true;
        }
        catch(BufferUnderflowException except)
        {
            except.printStackTrace();
            return false;
        }
    }
    public void deleteMarker(int toDelete)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("markers", "marker_id=" + toDelete, null);
        db.execSQL("UPDATE markers SET marker_id = (marker_id-1) WHERE marker_id  > " + Integer.toString(toDelete));
    }

    /***********************************************************************************************
     * Author: Shawn gibbons 2/22/21
     * Purpose: inserts data and image that is in Bitmap format into database.
     ***********************************************************************************************/
    public void insertBitmapWithID(int idNumber, Bitmap img)
    {
        float latitude = 0;
        float longitude = 0;
        float posX = 0;
        float posY = 0;
        float bearing = 0;
        String imgName = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Bitmap btmp = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE ID=?", new String[]{String.valueOf(idNumber)});
        //(ID integer primary key 0, LAT FLOAT 1, LONG FLOAT 2, POS_X FLOAT 3, POS_Y FLOAT 4, BEARING FLOAT 5, IMG blob not null 6, IMG_NAME TEXT 7)
        if (cursor.moveToNext())
        {
            latitude = cursor.getFloat(cursor.getColumnIndex("LAT"));
            longitude = cursor.getFloat(cursor.getColumnIndex("LONG"));
            posX = cursor.getFloat(cursor.getColumnIndex("POS_X"));
            posY = cursor.getFloat(cursor.getColumnIndex("POS_Y"));
            bearing = cursor.getFloat(cursor.getColumnIndex("BEARING"));
            imgName = cursor.getString(cursor.getColumnIndex("IMG_NAME"));
        }
        try
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            byte[] imgDataArr = byteStream.toByteArray();
            //String encoded = Base64.encodeToString(imgDataArr, Base64.DEFAULT);

            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", idNumber);
            contentValues.put("LAT", latitude);
            contentValues.put("LONG", longitude);
            contentValues.put("POS_X", posX);
            contentValues.put("POS_Y", posY);
            contentValues.put("BEARING", bearing);
            contentValues.put("IMG", imgDataArr);
            contentValues.put("IMG_NAME", imgName);
            db.execSQL("delete from " + tableName + " WHERE ID IS " + idNumber);
            db.insert(tableName, null, contentValues);
        }
        catch(BufferUnderflowException except)
        {
            except.printStackTrace();
        }
    }

}

// the bearing goes clockwise if + counter-clockwise if -
// include the difficulties of creating a UI for the database
// include difficulties of accessing database from android studio
//  - include difficulties of certain API not working
