package com.dodocrusaiders.utilitytracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MapSelectorAdapter extends RecyclerView.Adapter<MapSelectorAdapter.MapSelectorViewHolder>
{
    private ArrayList<MapSelectorItem> m_MapSelectorItemList;
    public static class MapSelectorViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView m_MapImageView;
        public TextView m_MapNameTextView;
        public TextView m_isActiveTextView;

        public MapSelectorViewHolder(@NonNull View itemView)
        {
            super(itemView);
            m_MapImageView = itemView.findViewById(R.id.imageView);
            m_MapNameTextView = itemView.findViewById(R.id.MapNameTextView);
            m_isActiveTextView = itemView.findViewById(R.id.IsActiveTextView);
        }
    }

    public MapSelectorAdapter(ArrayList<MapSelectorItem> mapSelectorItemArrayList)
    {
        m_MapSelectorItemList = mapSelectorItemArrayList;
    }

    @NonNull
    @Override
    public MapSelectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_selector_item, parent, false);
        MapSelectorViewHolder mapSelectorViewHolder = new MapSelectorViewHolder(view);
        return mapSelectorViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MapSelectorViewHolder holder, int position)
    {
        MapSelectorItem currItem = m_MapSelectorItemList.get(position);
        holder.m_MapImageView.setImageResource(currItem.getImageResource());
        holder.m_MapNameTextView.setText(currItem.getMapName());
        holder.m_isActiveTextView.setText(currItem.getIsActive());
        holder.itemView.setTag(currItem.getImageResource());
    }

    @Override
    public int getItemCount()
    {
        return m_MapSelectorItemList.size();
    }
}
