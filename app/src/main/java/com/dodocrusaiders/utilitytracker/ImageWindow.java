package com.dodocrusaiders.utilitytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ImageWindow extends AppCompatActivity
{
private Button ReturnButton;
private Button next;
private TextView Msg;

private ImageView pic;
private float left;
private float right;
private float top;
private float bottom;
private int[] viewCoords = new int[2];

private Boolean First = false;
private Boolean Second = false;

private Boolean flag = false;

private float[] results = new float[5];
private enum Results
{
    x1, y1, x2, y2, width
}
private String ResultString = "ImageValues";
private String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_window);
        next = (Button) findViewById(R.id.pins);
        ReturnButton = (Button) findViewById(R.id.ReturnBut);
        Msg = (TextView) findViewById(R.id.textView2);
        ReturnButton.setOnClickListener(this::ReturnToMap);
        next.setOnClickListener(this::firstPin);

        pic = (ImageView) findViewById(R.id.imageView);
        //pic.getLocationOnScreen(viewCoords);
        /*viewCoords[0] = pic.getLeft();
        viewCoords[1] = pic.getRight();
        viewCoords[2] = pic.getTop();
        viewCoords[3] = pic.getBottom();*/
        //filename = getIntent().getStringExtra("ImageWindow");
        //int resID = getResources().getIdentifier(filename, "mipmap", getPackageName());
        //displayImage = (ImageView) findViewById(R.id.imageView);
        //displayImage.setImageResource(resID);
    }


    protected void ReturnToMap(View view)
    {
        if(First==true && Second == true)
        {
            results[4] = findViewById(R.id.imageView).getWidth();
            Intent I = new Intent();
            I.putExtra(ResultString, results);
            setResult(RESULT_OK, I);
            finish();
        }
        else
            Toast.makeText(getBaseContext(),"Select both Points", Toast.LENGTH_LONG).show();
    }


    protected void firstPin(View view)
    {

        if(flag)
        {
            First = true;
            flag = false;
            Msg.setText("Select First Pin\nThen Hit Next");
            ReturnToMap(view);
        }
        else
            {
            Second = true;
            flag = true;
                Msg.setText("Select Second Pin\nThen Hit Next or Return To Map");

            }
    }

    protected void setPoint1(float x, float y)
    {
        results[0] = x;
        results[1] = y;
    }

    protected void setPoint2(float x, float y)
    {
        results[2] = x;
        results[3] = y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        if (flag)
        {
            setPoint1(e.getX()/* - viewCoords[0]*/, e.getY()/* - viewCoords[1]*/);
        }
        else
            {
                setPoint2(e.getX()/* - viewCoords[0]*/, e.getY()/* - viewCoords[1]*/);
            }

        return true;
    }

}