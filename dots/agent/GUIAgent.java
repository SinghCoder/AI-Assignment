/*-****************************************************************************
 * GUIAgent.java
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

package dots.agent;

import dots.*;
import dots.agent.*;
import dots.agent.gui.*;
import dots.engine.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.logging.Logger;
import java.util.Observable;
import java.util.Observer;

/**
 * A basic GUI client agent.
 *
 * This agent gives the user a graphical user interface to the Dots and boxes
 * game. The interface consists of a scoreboard and a graphical representation
 * of the current grid.
 *
 * The purpose of this agent is to add the possibility for a user to test how
 * good an instance of QLearningAgent really is.
 */
public class GUIAgent extends Frame implements Agent, ActionListener,
       WindowListener, Observer
{
    /**
     * The logger to use for debug messages and the like.
     */
    final Logger logger;

    /**
     * The action to perform. This acts a shared var.
     */
    int action;

    /**
     * The button used to send the selected action.
     */
    Button button;

    /**
     * The object responsible for painting the current game plan.
     */
    GridPlan plan;

    /**
     * The label holding the current score.
     */
    Label score;

    /**
     * The number of points this agent has earned.
     */
    int points;

    /**
     * Create a GUIAgent.
     */
    public GUIAgent()
    {
        super("Dots & Boxes GUI Client");
        action = -1;
        points = 0;
        logger = Logger.getLogger(getClass().getPackage().getName());

        createInterface();
        addWindowListener(this);

        pack();
        setVisible(true);
    }

    /**
     * @see Agent
     */
    public synchronized int getAction(long state, Set<Integer> actions)
    {
        int returnValue = -1;

        while (returnValue == -1) {
            button.setEnabled(false);
            setEnabled(true);

            // First let the user pick a line.

            try {
                synchronized (plan) {
                    plan.wait();
                }
            } catch (InterruptedException e) {
                return -1;
            }

            button.setEnabled(true);

            // Then, when the button is pressed, send the line.

            try {
                wait();
            } catch (InterruptedException e) {
                return -1;
            }

            setEnabled(false);

            returnValue = plan.getNumber();
        }

        logger.fine(String.format("Got input: %d", returnValue));
        
        return returnValue;
    }

    /**
     * @see Agent
     */
    public void giveFeedback(int feedback, long newState, Set<Integer> actions)
    {
        // Nothing to do here.
    }

    /**
     * Create and set up the user interface.
     */
    public void createInterface()
    {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        setLayout(layout);

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 1.0;

        button = new Button("Ok");
        button.addActionListener(this);

        plan = new GridPlan();
    
        score = new Label("Score: 0");

        layout.setConstraints(plan, c);
        add(plan);

        add(score);
        add(button);

        setEnabled(false);
    }

    /**
     * If the ok-button has been pressed, then set the action to perform.
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == button) {
            synchronized (this) {
                notify();
            }
        }
    }

    /**
     * Exits the program if the window is to be closed.
     */
    public void windowClosing(WindowEvent e)
    {
        logger.fine("GUI Window closed, Asking Bootstrapper to terminate.");

        synchronized (Bootstrapper.getInstance()) {
            Bootstrapper.getInstance().quit();
        }
    }

    /**
     * No need to overload these.
     */
    public void windowActivated(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}

    public void shutdown()
    {
        logger.fine("Closing window.");
        dispose();
    }

    public void observe(Observable obj)
    {
        obj.addObserver(this);
        obj.addObserver(plan);
    }

    public synchronized void update(Observable obs, Object arg)
    {
        if (arg instanceof Scoring) {
            Scoring scoring = (Scoring) arg;
            score.setText(String.format("Score: %d", scoring.getNumBoxes(this)));
        }
    }
}
