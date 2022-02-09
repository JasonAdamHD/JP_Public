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

import static android.app.Activity.RESULT_OK;

public class DBDeleteAdapter extends BaseAdapter
{
    private Context m_context;
    private Bitmap[] m_btmArr;
    private DBDeleteMenu m_SLM;
    private SQLiteDBHelper db;

    public DBDeleteAdapter(Context context, Bitmap[] btmArr, DBDeleteMenu slm)
    {
        m_context = context;
        m_btmArr = btmArr.clone();
        m_SLM = slm;
        db = new SQLiteDBHelper(context, 1);
    }

    @Override
    public int getCount() { return m_btmArr.length; }

    @Override
    public Object getItem(int position) { return null; }

    @Override
    public long getItemId(int position) { return 0; }

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
                    db.deleteItem(position + 1);
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
