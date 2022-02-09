/*
 * Author:    Jason Adam
 * Purpose:   Create map selection items used for the UI of choosing which overlay maps are currently
 *            active or inactive
 * Modifications:
 */
package com.dodocrusaiders.utilitytracker;

public class MapSelectorItem
{
    private int m_ImageResource;
    private String m_MapName;
    private String m_IsActive;

    /*
     * Author: Jason Adam 1/24/21
     * Purpose: CTOR for the map selector item. I think with our current implementation that it
     *          would be better to always initialize m_isActive to "inactive" as we currently
     *          don't plan on saving user map selections for when they close and reopen the map.
     */
    public MapSelectorItem(int imageResource, String mapName)
    {
        m_ImageResource = imageResource;
        m_MapName = mapName;
        m_IsActive = "inactive";
    }

    /*
     * Author: Jason Adam 1/24/21
     * Purpose: CTOR for the map selector item. This one gets an input for m_isActive, not sure
     *          if we will ever use this one but it is better to have it than not have it.
     */
    public MapSelectorItem(int imageResource, String mapName, String isActive)
    {
        m_ImageResource = imageResource;
        m_MapName = mapName;
        m_IsActive = isActive;
    }

    /*
     * Author: Jason Adam 1/24/21
     * Purpose: Getter for m_ImageResource
     */
    public int getImageResource() { return m_ImageResource; }

    /*
     * Author: Jason Adam 1/24/21
     * Purpose: Getter for m_MapName
     */
    public String getMapName() { return m_MapName; }

    /*
     * Author: Jason Adam 1/24/21
     * Purpose: Getter for m_isActive
     */
    public String getIsActive() { return m_IsActive; }
}
