/***************************************************************************************************
 * Filename:	    ImageButtonAdapter.java
 * Date Created:	2/14/21
 * Modifications:
 *
 * Class: ImageButtonAdapter
 *
 * Purpose: This class is used to implement functionality of ui buttons for layer selection.
 **************************************************************************************************/
package com.dodocrusaiders.utilitytracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class ImageButtonAdapter extends BaseAdapter
{
    private Context m_context;
    private Bitmap[] m_btmArr;
    private SelectLayerMenu m_SLM;

    public ImageButtonAdapter(Context context, Bitmap[] btmArr, SelectLayerMenu slm)
    {
        m_context = context;
        m_btmArr = btmArr.clone();
        m_SLM = slm;
    }

    @Override
    public int getCount() { return m_btmArr.length; }

    @Override
    public Object getItem(int position) { return null; }

    @Override
    public long getItemId(int position) { return 0; }

    /***********************************************************************************************
     * Author: Brandon Toledo 2/14/21
     * Purpose: Sets up adapter for grid view, assigns click function to image buttons.
     * Modified: Tyler Kukkola 5/2/21 - store the id of the data in the db we are accessing
     ***********************************************************************************************/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) //position is array index
    {
        ImageButton imageButton;

        if(convertView == null)
        {
            imageButton  = new ImageButton(m_context);
            imageButton.setLayoutParams(new GridView.LayoutParams(250,250));
            imageButton.setAdjustViewBounds(true);
            imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
            imageButton.setPadding(0,0,0,0);
            imageButton.setBackground(Drawable.createFromPath("@android:color/transparent"));
            imageButton.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Intent intentImageWindow = new Intent();

                    Bitmap bmp = m_btmArr[position];
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();
                    //bmp.recycle();

                    intentImageWindow.putExtra("id", position + 1);

                    intentImageWindow.putExtra("image", image);
                    ((Activity)m_context).setResult(Activity.RESULT_OK, intentImageWindow);
                    ((Activity)m_context).finish();
                }
            });
        }
        else
        {
            imageButton = (ImageButton) convertView;
        }
        imageButton.setImageBitmap(m_btmArr[position]);

        return imageButton;
    }
}
