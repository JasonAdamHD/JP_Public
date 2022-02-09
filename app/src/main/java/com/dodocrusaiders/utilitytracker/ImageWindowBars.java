package com.dodocrusaiders.utilitytracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
//TODO: make width work
//TODO: add picture of map around anchor to background to allow user to align to map correctly
public class ImageWindowBars extends AppCompatActivity
{
    private Button ReturnButton;
    private SeekBar RotationBar;
    private SeekBar WidthBar;
    private ImageView pic;
    private ImageView background;
    private float angle;

    private int screenWidth;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_window_bars);
        ReturnButton = (Button) findViewById(R.id.ReturnButton);
        ReturnButton.setOnClickListener(this::ReturnToMap);
        RotationBar = (SeekBar) findViewById(R.id.RotationBar);

        RotationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                pic.setRotation(progress - 180);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }


        });
        WidthBar = (SeekBar) findViewById(R.id.WidthBar);
        WidthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                //pic.setMaxWidth(findViewById(R.id.mapImg).getWidth() * progress/100);
                pic.requestLayout();

                /*DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                float density = displayMetrics.density;*/
               // float widthDP = width/x
                if(progress == 0)
                    {
                        WidthBar.setProgress(1);
                        progress = 1;
                    }
                float scale = (float)progress/100;
                int width = (int) (screenWidth * scale);
                pic.getLayoutParams().width = width;///temp
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        pic = (ImageView) findViewById(R.id.mapImg);
        byte[] temp = getIntent().getByteArrayExtra("ImageWindow");
        Bitmap bmp = BitmapFactory.decodeByteArray(temp, 0, temp.length);
        pic.setImageBitmap(bmp);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);

        screenWidth = displayMetrics.widthPixels;
        pic.getLayoutParams().width = screenWidth;

    }

    protected void ReturnToMap(View view)
    {
        angle = pic.getRotation();
        Intent i = new Intent();
        i.putExtra("id", getIntent().getIntExtra("id", -1));
        i.putExtra("Rotation", angle);
        i.putExtra("Width", (float)WidthBar.getProgress());
        setResult(RESULT_OK, i);
        finish();
    }
}