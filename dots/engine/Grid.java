/*-****************************************************************************
 * Grid.java
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

package dots.engine;

import java.util.Set;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * The purpose of this class is to hold the current game configuration
 * and give access to functions related to the game configuration.
 */
public class Grid
{
    /**
     * The default height of a grid.
     */
    public static final int DEFAULT_HEIGHT = 4;

    /**
     * The default width of a grid.
     */
    public static final int DEFAULT_WIDTH = 4;

    /**
     * The height of the grid in number of dots.
     */
    private int height;

    /**
     * The width of the grid in number of dots.
     */
    private int width;

    /**
     * The lines set in this grid.
     */
    private boolean[] lines;

    /**
     * The logger to print log messages to.
     */
    private Logger logger;

    /**
     * The set of available lines.
     */
    private Set<Integer> available;

    /**
     * Create a new Grid with default dimensions.
     */
    public Grid()
    {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Create a new grid with the given dimension.
     *
     * @param width The width of the grid.
     * @param height The height of the grid.
     */
    public Grid(int width, int height)
    {
        this.width = width;
        this.height = height;

        int numLines = height * (width - 1) + width * (this.height - 1);
        lines = new boolean[numLines];

        logger = Logger.getLogger(getClass().getPackage().getName());
        available = new HashSet<Integer>();
    }

    /**
     * Calculates and return a unique id for this grid.
     *
     * @return a unique number for this grid.
     */
    public long getIdentifier()
    {
        int sum = 0;

        for (int i = 0; i < lines.length; ++i)
            if (lines[i])
                sum += 1 << i;

        return sum;
    }

    /**
     * Calculates the new state when an action is performed on a state
     * If the action already is performed on state the same state will be
     * returned
     * @param state     The current state on which the action will be performed
     * @param action    The action to be performed on the state
     *
     * @return The new state
     */
    public static long getIdentifier(long state, int action)
    {
        if (((state >> action) & 1 )== 1)
            return state;
        return state + (1 << action);
    }

    /**
     * @return true iff the game is considered finished, that is if the current
     * state is the final state.
     */
    public boolean isFinished()
    {
        for (int i = 0; i < lines.length; ++i)
            if (!lines[i])
                return false;
        return true;
    }

    /**
     * @return the set of possible lines to set.
     */
    public Set<Integer> getAvailableLines()
    {
        available.clear();

        for (int i = 0; i < lines.length; ++i)
            if (!lines[i])
                available.add(i);

        return available;
    }

    /**
     * Sets a line in the grid and returns the number of boxes was created
     * upon setting the line.
     *
     * @param line The line to set.
     * @throws IndexOutOfBoundsException on invalid line.
     * @return number of boxes created on line set.
     */
    public int setLine(int line)
    {
        if (line < 0 || line >= lines.length) {
            logger.severe(String.format("Invalid line number %d supplied. " +
                        "Valid interval is [%d, %d].", line, 0,
                        lines.length - 1));
            return -1;
        }

        lines[line] = true;
        int sum = 0;

        if (isHorizontal(line)) {
            if (line >= width
                    && lines[line - (2 * width - 1)]
                    && lines[line - width]
                    && lines[line - width + 1])
                ++sum;

            if (line < lines.length - width
                    && lines[line + width - 1]
                    && lines[line + width]
                    && lines[line + (2 * width - 1)])
                ++sum;
        } else {
            if ((line % (2 * width - 1)) - (width - 1) != (width - 1)
                    && lines[line + 1]
                    && lines[line - width + 1]
                    && lines[line + width])
                ++sum;

            if ((line % (2 * width - 1)) - (width - 1) != 0
                    && lines[line - 1]
                    && lines[line - width]
                    && lines[line + width - 1])
                ++sum;

        }

        String message = String.format("Line %02d is %s and gives %d point. " +
                "State is now %d.",
                line,
                (isHorizontal(line) ? "Horizontal" : "Vertical"),
                sum,
                getIdentifier()
        );

        logger.finest(message);

        return sum;
    }

    /**
     * Tests if the given line is horisontal.
     *
     * @param n The line number.
     * @return true iff the line is horisontal.
     */
    private boolean isHorizontal(int n)
    {
        return n % (2 * width - 1) < width - 1;
    }

    /**
     * Reset the Grid. This will effectively clean all actions done.
     */
    public void reset()
    {
        for (int i = 0; i < lines.length; ++i)
            lines[i] = false;
    }

    /**
     * @return the number of lines in the grid.
     */
    public int getSize()
    {
        return lines.length;
    }

    /**
     * @return the height of the grid.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @return the width of the grid.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * @return the maximum possible identifier.
     */
    public long getMaxIdentifier()
    {
        int sum = 0;
        for (int i = 0; i < lines.length; ++i)
            sum += 1 << i;

        return sum;
    }
}
