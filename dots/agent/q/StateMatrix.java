/*-****************************************************************************
 * StateMatrix.java
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

package dots.agent.q;

import dots.util.ShutdownHook;

/**
 * A StateMatrix is used to hold a number of state-value pairs for use with the
 * Q-learning agent.
 */
public interface StateMatrix extends ShutdownHook
{
    /**
     * Retrive the Q value for the given state and action.
     *
     * @param state The state part of the Q value.
     * @param action The action part of the Q value.
     * @return the Q value associated with the given state and action.
     */
    public float getQ(long state, int action);

    /**
     * Set the Q value for the given state and action.
     *
     * @param state The state part of the Q value.
     * @param action The action part of the Q value.
     * @param value The new Q value for the state and action pair.
     */
    public void setQ(long state, int action, float value);

    /**
     * Return the maximum Q value for the given state.
     *
     * @param state The state in question.
     */
    public float getMaxValue(long state);

    /**
     * @return The number of actions available per state.
     */
    public int getNumActions();

    /**
     * @return The number of states in the StateMatrix.
     */
    public long getNumStates();
}
