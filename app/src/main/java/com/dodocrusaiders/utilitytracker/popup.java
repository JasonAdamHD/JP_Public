package com.dodocrusaiders.utilitytracker;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class popup extends Activity
{
    Button btn_close;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        TextView description;
        TextView authors;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_pop_up);

        btn_close = (Button) findViewById(R.id.closeButton);
        btn_close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8), (int)(height*0.6));
        WindowManager.LayoutParams params = getWindow().getAttributes();

        //Set description text
        description = findViewById(R.id.description);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("UtilityTracker is an application designed to help people keep track "
                + "of their underground utilities by having a map of their utilities in one spot. "
                + "\n\nFor more help tap ");
        ssb.setSpan(new ImageSpan(this, R.drawable.ic_baseline_help),
                ssb.length() -1, ssb.length(), 0);
        ssb.append(".");
        description.setText(ssb);

        //Set author text
        authors = findViewById(R.id.authors);
        authors.setText("Authors:\nBrandon Toledo\nJason Adam\nNick Springer\nShawn Gibbons\nTyler "
                + "Kukkola");
    }
}
