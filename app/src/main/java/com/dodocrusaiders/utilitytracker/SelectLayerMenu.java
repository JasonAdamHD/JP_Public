/***************************************************************************************************
 * Filename:	    SelectLayerMenu.java
 * Date Created:	2/14/21
 * Modifications:
 *
 * Class: SelectLayerMenu
 *
 * Purpose: This class is used to set up content views for the layer selection menu.
 **************************************************************************************************/
package com.dodocrusaiders.utilitytracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

public class SelectLayerMenu extends Activity {
    GridView m_grid;
    SQLiteDBHelper m_db;

    /***********************************************************************************************
     * Author: Brandon Toledo 2/14/21
     * Purpose: Sets up content view for select_layer_menu layout. Assigns adapter to grid view.
     ***********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_layer_menu);

        m_db = new SQLiteDBHelper(getApplicationContext(), 1);
        //gets grid to put buttons in
        m_grid = (GridView) findViewById(R.id.Grid);
        //sets grid's adapter to dynamically add imagebuttons
        m_grid.setAdapter(new ImageButtonAdapter(this, m_db.getAllImgs(), this));
    }
}