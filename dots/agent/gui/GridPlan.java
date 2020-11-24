/*-****************************************************************************
 * GridPlan.java
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

import dots.engine.Grid;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Observable;

/**
 * A game plan for displaying a grid.
 *
 * This component is used by the graphical agent to display the current game
 * configuration, also called the grid. The grid can be clicked, and when it's
 * clicked, the graphical agent is notified of the change.
 */
public class GridPlan extends Canvas implements MouseListener, Observer
{
    Logger logger;

    /**
     * The grid to print.
     */
    Grid grid;

    /**
     * True iff the image must be updated.
     */
    boolean updated;

    /**
     * The lines printed.
     */
    List<GridLine> lines;

    /**
     * The selected line.
     */
    GridLine selected;

    /**
     * The image to paint on.
     */
    BufferedImage image;

    /**
     * The width of the GridLines printed.
     */
    static final double WIDTH = 10.0;

    public GridPlan()
    {
        logger = Logger.getLogger(getClass().getPackage().getName());
        lines = new ArrayList<GridLine>();
        selected = null;
        grid = null;
        image = null;
        updated = true;
        
        addMouseListener(this);
    }

    /**
     * Paint the grid.
     *
     * @param _g The graphics object to paint with.
     */
    public synchronized void paint(Graphics _g)
    {
        Graphics2D g;

        if (grid == null) {
            logger.warning("Can't paint, grid is null.");
            return;
        }

        double width = (getWidth() - WIDTH) / (grid.getWidth() - 1);
        double height = (getHeight() - WIDTH) / (grid.getHeight() - 1);

        if (image == null || getWidth() != image.getWidth()
                || getHeight() != image.getHeight()) {
            image = new BufferedImage(getWidth(), getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            updated = true;
        }

        g = image.createGraphics();

        /**
         * Trick for not having to update the image if were printing the same
         * grid again. Methods changing the grid should set updated to true.
         */
        if (updated) {
            for (int row = 0; row < grid.getHeight(); ++row) {
                for (int col = 0; col < grid.getWidth(); ++col) {
                    Rectangle2D rect = new Rectangle2D.Double(col * width,
                            row * height, WIDTH, WIDTH);

                    g.setColor(Color.BLACK);
                    g.fill(rect);

                    if (col < grid.getWidth() - 1) {
                        int index = (2 * grid.getWidth() - 1) * row + col;
                        GridLine line = lines.get(index);

                        line.setRect(col * width + WIDTH, row * height,
                                width - WIDTH, WIDTH);
                    }

                    if (row < grid.getHeight() - 1) {
                        int index = (2 * grid.getWidth() - 1) * row +
                            (grid.getWidth() - 1) + col;
                        GridLine line = lines.get(index);

                        line.setRect(col * width, row * height + WIDTH,
                                WIDTH, height - WIDTH);
                    }
                }
            }

            updated = false;
        }

        for (GridLine line : lines) {
            if (line == selected) {
                g.setColor(Color.YELLOW);
            } else if (line.isSet()) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GRAY);
            }

            g.fill(line);
        }

        _g.drawImage(image, 0, 0, Color.WHITE, null);
    }

    /**
     * Find whether the user clicked a valid line or not.
     */
    public synchronized void mouseClicked(MouseEvent e)
    {
        selected = null;

        for (GridLine line : lines) {
            if (line.contains(e.getPoint())) {
                if (!line.isSet()) {
                    logger.fine(String.format("Selected line %d.",
                                line.getNumber()));

                    selected = line;
                    repaint();
                    notify();

                    return;
                }
            }
        }
    }

    /**
     * @return the number selected by the user.
     */
    public synchronized int getNumber()
    {
        if (selected != null)
            return selected.getNumber();
        return -1;
    }

    /**
     * @return the preferred size of the plan.
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(300, 300);
    }

    /**
     * Updates the interface so that it represents the new state.
     */
    public synchronized void update(Observable obs, Object arg)
    {
        if (arg == null) {
            logger.severe("arg is null!");
        } else if (arg instanceof Grid && arg != grid) {
            logger.fine("Updating grid");

            updated = true;
            grid = (Grid) arg;

            // Trick for not having to recreate the lines.

            while (lines.size() > grid.getSize())
                lines.remove(lines.size() - 1);
            while (lines.size() < grid.getSize())
                lines.add(new GridLine(lines.size()));

            for (Integer num : grid.getAvailableLines())
                lines.get(num).unset();

            repaint();
        } else {
            logger.fine("Updating lines");

            for (GridLine line : lines)
                line.set();

            for (Integer num : grid.getAvailableLines())
                lines.get(num).unset();

            updated = true;
            selected = null;

            repaint();
        }
    }

    /**
     * No need for these methods.
     */
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
}
