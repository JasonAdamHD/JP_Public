package com.dodocrusaiders.utilitytracker;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.IOException;
import java.sql.SQLException;

public class ColorPicker extends AppCompatActivity
{
    private ImageView imageView;
    private TextView textView;
    private View colorView;
    private Button previewButton;
    private Button saveButton;
    private static int pixelUnderCursor = -1;
    private static Bitmap bitmap = null;
    private static int db_index;
    private Bitmap tempBitmap = null;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_picker);

        imageView = findViewById(R.id.mapForColorSelect);
        textView = findViewById(R.id.hex_color);
        colorView = findViewById(R.id.color);
        previewButton = findViewById(R.id.preview_button);
        saveButton = findViewById(R.id.save_color_picker_button);
        imageView.setImageBitmap(GetBitmap());

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);

        imageView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    Bitmap imageViewBitmap = imageView.getDrawingCache();
                    int xPixel = (int) event.getX();
                    int yPixel = (int) event.getY();
                    if(xPixel > 0 && xPixel < imageViewBitmap.getWidth() - 1 && yPixel > 0 && yPixel < imageViewBitmap.getHeight() - 1)
                    {
                        pixelUnderCursor = imageViewBitmap.getPixel(xPixel, yPixel);
                        int r = Color.red(pixelUnderCursor);
                        int g = Color.green(pixelUnderCursor);
                        int b = Color.blue(pixelUnderCursor);
                        String hex = "#" + Integer.toHexString((pixelUnderCursor));
                        colorView.setBackgroundColor(Color.rgb(r, g, b));
                        textView.setText(hex);
                    }
                }
                return true;
            }
        });
        previewButton.setOnClickListener(new View.OnClickListener()
        {
            int DEBUGGING = 9;
            @Override
            public void onClick(View v)
            {
                try
                {
                    tempBitmap = Bitmap.createBitmap(GetBitmap().getWidth(), GetBitmap().getHeight(), Bitmap.Config.ARGB_8888);
                    tempBitmap = ImageProcessing.ConvertFromBitmapToBinaryImage(GetBitmap());
                    imageView.setImageBitmap(tempBitmap);
                } catch (SQLException throwables)
                {
                    throwables.printStackTrace();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                SQLiteDBHelper dbHelper;
                dbHelper = new SQLiteDBHelper(getApplicationContext(), 1);
                try
                {
                    if(tempBitmap == null)
                    {
                        SetBitmap(ImageProcessing.ConvertFromBitmapToBinaryImage(GetBitmap()));
                    }
                    else
                    {
                        SetBitmap(tempBitmap);
                    }
                    dbHelper.insertBitmapWithID(db_index, bitmap);
                    finish();
                } catch (SQLException throwables)
                {
                    throwables.printStackTrace();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Bitmap GetBitmap() { return bitmap; }
    public static void SetBitmap(Bitmap bm) { bitmap = bm; }
    public static int GetFilterColor() { return pixelUnderCursor; }
    public static void ResetFilterColor() { pixelUnderCursor = -1; }
    public static void setDb_index(int db_index) { ColorPicker.db_index = db_index; }
}
