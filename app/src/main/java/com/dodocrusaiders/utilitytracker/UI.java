/*
 Author:    Jason Adam
 Purpose:
 Modifications:
*/
package com.dodocrusaiders.utilitytracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.model.GroundOverlay;
import java.util.ArrayList;

public class UI extends AppCompatActivity
{
    private Button ReturnButton;
    private RecyclerView m_RecyclerView;
    private RecyclerView.Adapter m_Adapter;
    private RecyclerView.LayoutManager m_LayoutManager;
    // TODO: Work on function that reads from db to pull images to add to overlay
    // TODO: Make it so clicking on the button actually enables the overlay on the map

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overlay_selector);

        ArrayList<MapSelectorItem> mapSelectorList = new ArrayList<>();
        // TODO: THIS NEEDS TO BECOME DYNAMICALLY GENERATED FROM THE DATABASE IN THE NEXT SPRINT.
        mapSelectorList.add(new MapSelectorItem(R.mipmap.overlay_test_img1, "\"Gas Lines\""));
        mapSelectorList.add(new MapSelectorItem(R.mipmap.overlay_test_img2, "\"Water Lines\""));

        m_RecyclerView = findViewById(R.id.recyclerView);
        m_RecyclerView = m_RecyclerView.findViewById(R.id.recyclerView);
        m_RecyclerView.setHasFixedSize(true);
        m_LayoutManager = new LinearLayoutManager(this);
        m_Adapter = new MapSelectorAdapter(mapSelectorList);
        m_RecyclerView.setLayoutManager(m_LayoutManager);
        m_RecyclerView.setAdapter(m_Adapter);
        ReturnButton = (Button) findViewById(R.id.ReturnBut);
        ReturnButton.setOnClickListener(this::ReturnToMap);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {
                //GroundOverlay overlay = Layer.addMap(MapsActivity.mMap, (int) viewHolder.itemView.getTag(), 0, 0, 0, 0, (float).5,(float).5, 0.5f);
                Toast.makeText(MapsActivity.temp_context, "Swipe successful", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(m_RecyclerView);

    }


    protected void ReturnToMap(View view)
    {
        Intent I = new Intent();
        I.putExtra("Returning to Map", 0);
        finish();
    }

    // Pretty sure this function is now useless with my new way of implementation.
    public void DisplayLayerSelectionMenu(Context context)
    {
        // This code will open a menu that will allow for the selection of images from shawns
        // local database. I will implement a test version of this soon (before 1/22/21) to just
        // read from the resource files as a stand in until i can implement the local db.
        // https://www.youtube.com/watch?v=5ISNPFmuOU8&list=PLrnPJCHvNZuBMJmll0xy2L2McYInT3aiu&ab_channel=CodinginFlow
        // ^^^ Tutorial like this will be useful for creating UI for selecting overlays
    }
}
