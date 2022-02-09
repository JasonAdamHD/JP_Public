package com.dodocrusaiders.utilitytracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

public class DBDeleteMenu extends Activity
{
    GridView m_grid;
    SQLiteDBHelper m_db;

    /***********************************************************************************************
     * Author: Brandon Toledo 2/14/21
     * Purpose: Sets up content view for select_layer_menu layout.
     ***********************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_layer_menu);

        m_db = new SQLiteDBHelper(getApplicationContext(), 1);
        m_grid = (GridView) findViewById(R.id.Grid); //gets grid to put buttons in
        m_grid.setAdapter(new DBDeleteAdapter(this, m_db.getAllImgs(), this)); //sets grid's adapter to dynamically add imagebuttons
    }
}