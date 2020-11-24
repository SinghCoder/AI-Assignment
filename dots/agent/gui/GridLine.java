/*-****************************************************************************
 * GridLine.java
 ******************************************************************************
 * Copyright (C) 2010 Oskar Arvidsson, Linus Wallgren
 *
 * This file is part of dotsnboxes.
 *
 * dotsnboxes is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * dotsnboxes is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * dotsnboxes. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/

package dots.agent.gui;

import java.awt.geom.*;

/**
 * Represents a line in the GridPlan.
 *
 * This class is used by the GridPlan class for representing lines on the grid.
 */
public class GridLine extends Rectangle2D.Double
{
    /**
     * The number of this line.
     */
    int number;

    /**
     * Tells whether this line is set or unset.
     */
    boolean is_set;

    /**
     * Creates a new GridLine with the given number and is set.
     *
     * @param number The number of the line.
     */
    public GridLine(int number)
    {
        this(number, true);
    }

    /**
     * Creates a new GridLine.
     *
     * @param number The number of tbe line.
     * @param set True iff the GridLine should be set.
     */
    public GridLine(int number, boolean set)
    {
        this.number = number;
        this.is_set = set;
    }

    /**
     * @return true iff this line is set.
     */
    public boolean isSet()
    {
        return is_set;
    }

    /**
     * @return the number of this line.
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * Set this line.
     *
     * @param is_set True iff this line should be set.
     */
    public void set(boolean is_set)
    {
        this.is_set = is_set;
    }

    /**
     * Set this line. Shorthand for set(true).
     */
    public void set()
    {
        set(true);
    }

    /**
     * Unset this line. Shorthand for set(false).
     */
    public void unset()
    {
        set(false);
    }
}
