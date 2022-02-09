package com.dodocrusaiders.utilitytracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PopUpTutorial extends Activity
{
    private final String next = "Next";
    private final String prev = "Prev";
    private final String close = "Close";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tooltips_start);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout(width, height);

        TextView tooltips_start_head = findViewById(R.id.welcome);
        String welcomeStr = "Welcome to UtilityTracker";
        tooltips_start_head.setText(welcomeStr);
        TextView tooltips_start = findViewById(R.id.welcome1);
        String welcomeStr1 = "\n\n\n\n\nUtilityTracker is an interactive "
                + "application that allows users to keep track of their underground utilities. "
                + "To get started using Utility Tracker, locate your property on the map. "
                + "Once you locate your property on the map you can begin."
                + "\n\n\n\n\n\nTap 'Next' to continue, or tap 'Close' to exit at any time.";
        tooltips_start.setText(welcomeStr1);
        Button closeStart = findViewById(R.id.closeButtonStart);
        closeStart.setText(close);
        Button nextStart = findViewById(R.id.nextButtonStart);
        nextStart.setText(next);
    }

    /***********************************************************************************************
     * Author: Shawn Gibbons 4/18/2021
     * Purpose: To change view of tutorial
     ***********************************************************************************************/
    public void changeTutorialViewNext(View view)
    {
        switch (view.getId())
        {
            case R.id.nextButtonStart:
                setContentView(R.layout.tooltips_01);
                TextView tooltips1 = findViewById(R.id.tt1);
                String tt1Str = new String(
                        "Once you have found your property you can begin annotating. "
                        + "However, if you wish to change the map view to satellite view, tap on "
                        + "the ellipses in the top right corner."
                        + "\n\n\n"
                        + "Tap 'Prev' to review previous material.");
                tooltips1.setText(tt1Str);
                Button prev1 = findViewById(R.id.prevButton1);
                prev1.setText(prev);
                Button close1 = findViewById(R.id.closeButton1);
                close1.setText(close);
                Button next1 = findViewById(R.id.nextButton1);
                next1.setText(next);
                break;
            case R.id.nextButton1:
                setContentView(R.layout.tooltips_02);
                TextView tooltips2 = findViewById(R.id.tt2);
                String tt2 = "Tap on 'Toggle Map View' to set the view to look like this.";
                tooltips2.setText(tt2);
                Button prev2 = findViewById(R.id.prevButton2);
                prev2.setText(prev);
                Button close2 = findViewById(R.id.closeButton2);
                close2.setText(close);
                Button next2 = findViewById(R.id.nextButton2);
                next2.setText(next);
                break;
            case R.id.nextButton2:
                setContentView(R.layout.tooltips_03);
                TextView tooltips3 = findViewById(R.id.tt3);
                String tt3 = "You can change the view back at any time by tapping on 'Toggle "
                        + "Map View'.";
                tooltips3.setText(tt3);
                Button prev3 = findViewById(R.id.prevButton3);
                prev3.setText(prev);
                Button close3 = findViewById(R.id.closeButton3);
                close3.setText(close);
                Button next3 = findViewById(R.id.nextButton3);
                next3.setText(next);
                break;
            case R.id.nextButton3:
                setContentView(R.layout.tooltips_04);
                TextView tooltips4 = findViewById(R.id.tt4);
                String tt4 = "To start annotating, tap on the highlighted button.\n\nNote that "
                        + "the highlighted button will read 'annotations off' when you "
                        + "are not annotating, and 'annotations on' when you are.";
                tooltips4.setText(tt4);
                Button prev4 = findViewById(R.id.prevButton4);
                prev4.setText(prev);
                Button close4 = findViewById(R.id.closeButton4);
                close4.setText(close);
                Button next4 = findViewById(R.id.nextButton4);
                next4.setText(next);
                break;
            case R.id.nextButton4:
                setContentView(R.layout.tooltips_05);
                TextView tooltips5 = findViewById(R.id.tt5);
                String tt5 = "If you wish to add text to the map,\ntap on 'Text'. To change "
                        + "the size of\nthe text, slide the slider left or right to make "
                        + "the text smaller or larger.\n\nTo change color of the text, tap "
                        + "on any of the four colors highlighted in yellow.";
                tooltips5.setText(tt5);
                Button prev5 = findViewById(R.id.prevButton5);
                prev5.setText(prev);
                Button close5 = findViewById(R.id.closeButton5);
                close5.setText(close);
                Button next5 = findViewById(R.id.nextButton5);
                next5.setText(next);
                break;
            case R.id.nextButton5:
                setContentView(R.layout.tooltips_06);
                TextView tooltips6 = findViewById(R.id.tt6);
                String tt6 = "If you wish to draw a line, simply tap on 'Line' and choose "
                        + "a color from one of the four colors highlighted in yellow.";
                tooltips6.setText(tt6);
                Button prev6 = findViewById(R.id.prevButton6);
                prev6.setText(prev);
                Button close6 = findViewById(R.id.closeButton6);
                close6.setText(close);
                Button next6 = findViewById(R.id.nextButton6);
                next6.setText(next);
                break;
            case R.id.nextButton6:
                setContentView(R.layout.tooltips_07);
                TextView tooltips7 = findViewById(R.id.tt7);
                String tt7 = "If you wish to draw a circle, simply tap on 'Circle' and "
                        + "choose a color from one of the four colors highlighted "
                        + "in yellow.";
                tooltips7.setText(tt7);
                Button prev7 = findViewById(R.id.prevButton7);
                prev7.setText(prev);
                Button close7 = findViewById(R.id.closeButton7);
                close7.setText(close);
                Button next7 = findViewById(R.id.nextButton7);
                next7.setText(next);
                break;
            case R.id.nextButton7:
                setContentView(R.layout.tooltips_08);
                TextView tooltips8 = findViewById(R.id.tt8);
                String tt8 = "Should there be an annotation on the map that you wish to remove, "
                        + "in this case the circle highlighted below, "
                        + "simply tap on 'Delete' and then tap on the "
                        + "annotation you wish to delete.";
                tooltips8.setText(tt8);
                Button prev8 = findViewById(R.id.prevButton8);
                prev8.setText(prev);
                Button close8 = findViewById(R.id.closeButton8);
                close8.setText(close);
                Button next8 = findViewById(R.id.nextButton8);
                next8.setText(next);
                break;
            case R.id.nextButton8:
                setContentView(R.layout.tooltips_09);
                TextView tooltips9 = findViewById(R.id.tt9);
                String tt9 = "To quickly save the drawing\nyou've made on the map, tap\n'Save Drawing'.";
                tooltips9.setText(tt9);
                Button prev9 = findViewById(R.id.prevButton9);
                prev9.setText(prev);
                Button close9 = findViewById(R.id.closeButton9);
                close9.setText(close);
                Button next9 = findViewById(R.id.nextButton9);
                next9.setText(next);
                break;
            case R.id.nextButton9:
                setContentView(R.layout.tooltips_10);
                TextView tooltips10 = findViewById(R.id.tt10);
                String tt10 = "To clear everything you've\ndrawn on the map, tap 'Clear'.";
                tooltips10.setText(tt10);
                Button prev10 = findViewById(R.id.prevButton10);
                prev10.setText(prev);
                Button close10 = findViewById(R.id.closeButton10);
                close10.setText(close);
                Button next10 = findViewById(R.id.nextButton10);
                next10.setText(next);
                break;
            case R.id.nextButton10:
                setContentView(R.layout.tooltips_11);
                TextView tooltips11 = findViewById(R.id.tt11);
                String tt11 = "If you accidentally cleared your drawing but saved it previously, "
                        + "or if you wish to put a previously saved drawing on the map, tap "
                        + "'Load Drawing'.";
                tooltips11.setText(tt11);
                Button prev11 = findViewById(R.id.prevButton11);
                prev11.setText(prev);
                Button close11 = findViewById(R.id.closeButton11);
                close11.setText(close);
                Button next11 = findViewById(R.id.nextButton11);
                next11.setText(next);
                break;
            case R.id.nextButton11:
                setContentView(R.layout.tooltips_12);
                TextView tooltips12 = findViewById(R.id.tt12);
                String tt12 = "If you have an already completed map of your utilities, you can "
                        + "simply take a picture of the map, and add it to the app to later "
                        + "put on the map.\n\nTo add an image to the app tap the camera icon "
                        + "that has a plus in it, and select the image you wish to add.";
                tooltips12.setText(tt12);
                Button prev12 = findViewById(R.id.prevButton12);
                prev12.setText(prev);
                Button close12 = findViewById(R.id.closeButton12);
                close12.setText(close);
                Button next12 = findViewById(R.id.nextButton12);
                next12.setText(next);
                break;
            case R.id.nextButton12:
                setContentView(R.layout.tooltips_13);
                TextView tooltips13 = findViewById(R.id.tt13);
                String tt13 = "To add that image you already added to the map, you will "
                        + "need to set an anchoring point, do this by tapping the "
                        + "ellipses in the top right corner, and tap 'marker options.";
                tooltips13.setText(tt13);
                Button prev13 = findViewById(R.id.prevButton13);
                prev13.setText(prev);
                Button close13 = findViewById(R.id.closeButton13);
                close13.setText(close);
                Button next13 = findViewById(R.id.nextButton13);
                next13.setText(next);
                break;
            case R.id.nextButton13:
                setContentView(R.layout.tooltips_14);
                TextView tooltips14 = findViewById(R.id.tt14);
                String tt14 = "Once you are in the marker options tap 'Set Anchor Marker' "
                        + "and tap where you wish to center the image on the map.";
                tooltips14.setText(tt14);
                Button prev14 = findViewById(R.id.prevButton14);
                prev14.setText(prev);
                Button close14 = findViewById(R.id.closeButton14);
                close14.setText(close);
                Button next14 = findViewById(R.id.nextButton14);
                next14.setText(next);
                break;
            case R.id.nextButton14:
                setContentView(R.layout.tooltips_14_5);
                TextView tooltips14_5 = findViewById(R.id.tt14_5);
                String tt14_5 = "You can also remove 'portions' of the image by going "
                              + "into the app settings, and tapping 'Exclude Color  "
                              + "From Map' and then tap anywhere on the image to "
                              + "select a color to remove and tap 'save filtered "
                              + "image' once you select the color you wish to remove.";
                tooltips14_5.setText(tt14_5);
                Button prev14_5 = findViewById(R.id.prevButton14_5);
                prev14_5.setText(prev);
                Button close14_5 = findViewById(R.id.closeButton14_5);
                close14_5.setText(close);
                Button next14_5 = findViewById(R.id.nextButton14_5);
                next14_5.setText(next);
                break;
            case R.id.nextButton14_5:
                setContentView(R.layout.tooltips_15);
                TextView tooltips15 = findViewById(R.id.tt15);
                String tt15 = "Once you have an anchor marker set tap "
                        + "'Add Image Layer' and select the image you wish to add to the map.";
                tooltips15.setText(tt15);
                Button prev15 = findViewById(R.id.prevButton15);
                prev15.setText(prev);
                Button close15 = findViewById(R.id.closeButton15);
                close15.setText(close);
                Button next15 = findViewById(R.id.nextButton15);
                next15.setText(next);
                break;
            case R.id.nextButton15:
                setContentView(R.layout.tooltips_16);
                TextView tooltips16 = findViewById(R.id.tt16);
                String tt16 = "After selecting the image you wish to add to the map, "
                        + "you can modify the rotation of the image by sliding "
                        + "the 'Rotation' slider left or right to rotate the "
                        + "image left or right.\n\nYou can modify the size of "
                        + "the image by sliding the 'Scale' slider left or right "
                        + "where the furthest right is the largest size it can be. "
                        + "\n\nOnce you are done modifying simply tap 'Return to map'.";
                tooltips16.setText(tt16);
                Button prev16 = findViewById(R.id.prevButton16);
                prev16.setText(prev);
                Button close16 = findViewById(R.id.closeButton16);
                close16.setText(close);
                Button next16 = findViewById(R.id.nextButton16);
                next16.setText(next);
                break;
            case R.id.nextButton16:
                setContentView(R.layout.tooltips_end);
                TextView tooltipsEnd = findViewById(R.id.ttEnd);
                String end = "This is the end of the tutorial for UtilityTracker.\n\n"
                        + "As a reminder, you can tap 'prev' to review "
                        + "previous material, or tap close to exit the tutorial."
                        + "\n\nThank you for using UtilityTracker.";
                tooltipsEnd.setText(end);
                Button prevEnd = findViewById(R.id.prevButtonEnd);
                prevEnd.setText(prev);
                Button closeEnd = findViewById(R.id.closeButtonEnd);
                closeEnd.setText(close);
                break;
            default:
                break;
        }
    }

    /***********************************************************************************************
     * Author: Shawn Gibbons 4/18/2021
     * Purpose: To change view of tutorial
     ***********************************************************************************************/
    public void changeTutorialViewPrev(View view)
    {
        switch (view.getId())
        {
            case R.id.prevButton1:
                setContentView(R.layout.tooltips_start);
                TextView tooltips_start_head = findViewById(R.id.welcome);
                String welcomeStr = "Welcome to UtilityTracker";
                tooltips_start_head.setText(welcomeStr);
                TextView tooltips_start = findViewById(R.id.welcome1);
                String welcomeStr1 = "\n\n\n\n\nUtilityTracker is an interactive "
                        + "application that allows users to keep track of their underground utilities. "
                        + "To get started using Utility Tracker, locate your property on the map. "
                        + "Once you locate your property on the map you can begin."
                        + "\n\n\n\n\n\nTap 'Next' to continue, or tap 'Close' to exit at any time.";
                tooltips_start.setText(welcomeStr1);
                Button closeStart = findViewById(R.id.closeButtonStart);
                closeStart.setText(close);
                Button nextStart = findViewById(R.id.nextButtonStart);
                nextStart.setText(next);
                break;
            case R.id.prevButton2:
                setContentView(R.layout.tooltips_01);
                TextView tooltips1 = findViewById(R.id.tt1);
                String tt1Str = new String(
                        "Once you have found your property you can begin annotating. "
                                + "However, if you wish to change the map view to satellite view, tap on "
                                + "the ellipses in the top right corner."
                                + "\n\n\n"
                                + "Tap 'Prev' to review previous material.");
                tooltips1.setText(tt1Str);
                Button prev1 = findViewById(R.id.prevButton1);
                prev1.setText(prev);
                Button close1 = findViewById(R.id.closeButton1);
                close1.setText(close);
                Button next1 = findViewById(R.id.nextButton1);
                next1.setText(next);
                break;
            case R.id.prevButton3:
                setContentView(R.layout.tooltips_02);
                TextView tooltips2 = findViewById(R.id.tt2);
                String tt2 = "Tap on 'Toggle Map View' to set the view to look like this.";
                tooltips2.setText(tt2);
                Button prev2 = findViewById(R.id.prevButton2);
                prev2.setText(prev);
                Button close2 = findViewById(R.id.closeButton2);
                close2.setText(close);
                Button next2 = findViewById(R.id.nextButton2);
                next2.setText(next);
                break;
            case R.id.prevButton4:
                setContentView(R.layout.tooltips_03);
                TextView tooltips3 = findViewById(R.id.tt3);
                String tt3 = "You can change the view back at any time by tapping on 'Toggle "
                        + "Map View'.";
                tooltips3.setText(tt3);
                Button prev3 = findViewById(R.id.prevButton3);
                prev3.setText(prev);
                Button close3 = findViewById(R.id.closeButton3);
                close3.setText(close);
                Button next3 = findViewById(R.id.nextButton3);
                next3.setText(next);
                break;
            case R.id.prevButton5:
                setContentView(R.layout.tooltips_04);
                TextView tooltips4 = findViewById(R.id.tt4);
                String tt4 = "To start annotating, tap on the highlighted button.\n\nNote that "
                        + "the highlighted button will read 'annotations off' when you "
                        + "are not annotating, and 'annotations on' when you are.";
                tooltips4.setText(tt4);
                Button prev4 = findViewById(R.id.prevButton4);
                prev4.setText(prev);
                Button close4 = findViewById(R.id.closeButton4);
                close4.setText(close);
                Button next4 = findViewById(R.id.nextButton4);
                next4.setText(next);
                break;
            case R.id.prevButton6:
                setContentView(R.layout.tooltips_05);
                TextView tooltips5 = findViewById(R.id.tt5);
                String tt5 = "If you wish to add text to the map,\ntap on 'Text'. To change "
                        + "the size of\nthe text, slide the slider left or right to make "
                        + "the text smaller or larger.\n\nTo change color of the text, tap "
                        + "on any of the four colors highlighted in yellow.";
                tooltips5.setText(tt5);
                Button prev5 = findViewById(R.id.prevButton5);
                prev5.setText(prev);
                Button close5 = findViewById(R.id.closeButton5);
                close5.setText(close);
                Button next5 = findViewById(R.id.nextButton5);
                next5.setText(next);
                break;
            case R.id.prevButton7:
                setContentView(R.layout.tooltips_06);
                TextView tooltips6 = findViewById(R.id.tt6);
                String tt6 = "If you wish to draw a line, simply tap on 'Line' and choose "
                        + "a color from one of the four colors highlighted in yellow.";
                tooltips6.setText(tt6);
                Button prev6 = findViewById(R.id.prevButton6);
                prev6.setText(prev);
                Button close6 = findViewById(R.id.closeButton6);
                close6.setText(close);
                Button next6 = findViewById(R.id.nextButton6);
                next6.setText(next);
                break;
            case R.id.prevButton8:
                setContentView(R.layout.tooltips_07);
                TextView tooltips7 = findViewById(R.id.tt7);
                String tt7 = "If you wish to draw a circle, simply tap on 'Circle' and "
                        + "choose a color from one of the four colors highlighted "
                        + "in yellow.";
                tooltips7.setText(tt7);
                Button prev7 = findViewById(R.id.prevButton7);
                prev7.setText(prev);
                Button close7 = findViewById(R.id.closeButton7);
                close7.setText(close);
                Button next7 = findViewById(R.id.nextButton7);
                next7.setText(next);
                break;
            case R.id.prevButton9:
                setContentView(R.layout.tooltips_08);
                TextView tooltips8 = findViewById(R.id.tt8);
                String tt8 = "Should there be an annotation on the map that you wish to remove, "
                        + "in this case the circle highlighted below, "
                        + "simply tap on 'Delete' and then tap on the "
                        + "annotation you wish to delete.";
                tooltips8.setText(tt8);
                Button prev8 = findViewById(R.id.prevButton8);
                prev8.setText(prev);
                Button close8 = findViewById(R.id.closeButton8);
                close8.setText(close);
                Button next8 = findViewById(R.id.nextButton8);
                next8.setText(next);
                break;
            case R.id.prevButton10:
                setContentView(R.layout.tooltips_09);
                TextView tooltips9 = findViewById(R.id.tt9);
                String tt9 = "To quickly save the drawing\nyou've made on the map, tap\n'Save Drawing'.";
                tooltips9.setText(tt9);
                Button prev9 = findViewById(R.id.prevButton9);
                prev9.setText(prev);
                Button close9 = findViewById(R.id.closeButton9);
                close9.setText(close);
                Button next9 = findViewById(R.id.nextButton9);
                next9.setText(next);
                break;
            case R.id.prevButton11:
                setContentView(R.layout.tooltips_10);
                TextView tooltips10 = findViewById(R.id.tt10);
                String tt10 = "To clear everything you've\ndrawn on the map, tap 'Clear'.";
                tooltips10.setText(tt10);
                Button prev10 = findViewById(R.id.prevButton10);
                prev10.setText(prev);
                Button close10 = findViewById(R.id.closeButton10);
                close10.setText(close);
                Button next10 = findViewById(R.id.nextButton10);
                next10.setText(next);
                break;
            case R.id.prevButton12:
                setContentView(R.layout.tooltips_11);
                TextView tooltips11 = findViewById(R.id.tt11);
                String tt11 = "If you accidentally cleared your drawing but saved it previously, "
                        + "or if you wish to put a previously saved drawing on the map, tap "
                        + "'Load Drawing'.";
                tooltips11.setText(tt11);
                Button prev11 = findViewById(R.id.prevButton11);
                prev11.setText(prev);
                Button close11 = findViewById(R.id.closeButton11);
                close11.setText(close);
                Button next11 = findViewById(R.id.nextButton11);
                next11.setText(next);
                break;
            case R.id.prevButton13:
                setContentView(R.layout.tooltips_12);
                TextView tooltips12 = findViewById(R.id.tt12);
                String tt12 = "If you have an already completed map of your utilities, you can "
                        + "simply take a picture of the map, and add it to the app to later "
                        + "put on the map.\n\nTo add an image to the app tap the camera icon "
                        + "that has a plus in it, and select the image you wish to add.";
                tooltips12.setText(tt12);
                Button prev12 = findViewById(R.id.prevButton12);
                prev12.setText(prev);
                Button close12 = findViewById(R.id.closeButton12);
                close12.setText(close);
                Button next12 = findViewById(R.id.nextButton12);
                next12.setText(next);
                break;
            case R.id.prevButton14:
                setContentView(R.layout.tooltips_13);
                TextView tooltips13 = findViewById(R.id.tt13);
                String tt13 = "To add that image you already added to the map, you will "
                        + "need to set an anchoring point, do this by tapping the "
                        + "ellipses in the top right corner, and tap 'marker options.";
                tooltips13.setText(tt13);
                Button prev13 = findViewById(R.id.prevButton13);
                prev13.setText(prev);
                Button close13 = findViewById(R.id.closeButton13);
                close13.setText(close);
                Button next13 = findViewById(R.id.nextButton13);
                next13.setText(next);
                break;
            case R.id.prevButton14_5:
                setContentView(R.layout.tooltips_14);
                TextView tooltips14 = findViewById(R.id.tt14);
                String tt14 = "Once you are in the marker options tap 'Set Anchor Marker' "
                        + "and tap where you wish to center the image on the map.";
                tooltips14.setText(tt14);
                Button prev14 = findViewById(R.id.prevButton14);
                prev14.setText(prev);
                Button close14 = findViewById(R.id.closeButton14);
                close14.setText(close);
                Button next14 = findViewById(R.id.nextButton14);
                next14.setText(next);
                break;
            case R.id.prevButton15:
                setContentView(R.layout.tooltips_14_5);
                TextView tooltips14_5 = findViewById(R.id.tt14_5);
                String tt14_5 = "You can also remove 'portions' of the image by going "
                        + "into the app settings, and tapping 'Exclude Color  "
                        + "From Map' and then tap anywhere on the image to "
                        + "select a color to remove and tap 'save filtered "
                        + "image' once you select the color you wish to remove.";
                tooltips14_5.setText(tt14_5);
                Button prev14_5 = findViewById(R.id.prevButton14_5);
                prev14_5.setText(prev);
                Button close14_5 = findViewById(R.id.closeButton14_5);
                close14_5.setText(close);
                Button next14_5 = findViewById(R.id.nextButton14_5);
                next14_5.setText(next);
                break;
            case R.id.prevButton16:
                setContentView(R.layout.tooltips_15);
                TextView tooltips15 = findViewById(R.id.tt15);
                String tt15 = "Once you have an anchor marker set tap "
                        + "'Add Image Layer' and select the image you wish to add to the map.";
                tooltips15.setText(tt15);
                Button prev15 = findViewById(R.id.prevButton15);
                prev15.setText(prev);
                Button close15 = findViewById(R.id.closeButton15);
                close15.setText(close);
                Button next15 = findViewById(R.id.nextButton15);
                next15.setText(next);
                break;
            case R.id.prevButtonEnd:
                setContentView(R.layout.tooltips_16);
                TextView tooltips16 = findViewById(R.id.tt16);
                String tt16 = "After selecting the image you wish to add to the map, "
                        + "you can modify the rotation of the image by sliding "
                        + "the 'Rotation' slider left or right to rotate the "
                        + "image left or right.\n\nYou can modify the size of "
                        + "the image by sliding the 'Scale' slider left or right "
                        + "where the furthest right is the largest size it can be. "
                        + "\n\nOnce you are done modifying simply tap 'Return to map'.";
                tooltips16.setText(tt16);
                Button prev16 = findViewById(R.id.prevButton16);
                prev16.setText(prev);
                Button close16 = findViewById(R.id.closeButton16);
                close16.setText(close);
                Button next16 = findViewById(R.id.nextButton16);
                next16.setText(next);
                break;
            default:
                break;
        }
    }

    /***********************************************************************************************
     * Author: Shawn Gibbons 4/18/2021
     * Purpose: To exit tutorial
     ***********************************************************************************************/
    public void exitTut(View view)
    {
        finish();
    }
}