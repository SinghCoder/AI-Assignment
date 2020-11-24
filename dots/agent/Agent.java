/*-****************************************************************************
 * Agent.java
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

import java.util.*;

/**
 * This is the common interface for agents in the game. It defines the most
 * basic operations the agents must support.
 */
public interface Agent
{
    /**
     * Given a state and a set of actions, return one of the actions.
     * 
     * @param state The state from which to move.
     * @param actions A set of possible actions in the given state.
     * @return the action to perform. If -1 is returned, its a signal to the engine to
     * stop execution.
     */
    public int getAction(long state, Set<Integer> actions);

    /**
     * Give feedback to the agent on the previous action.
     *
     * @param feedback Positive values are intercepted as a reward,
     * negative values as a punishment.
     * @param newState The current state, i.e.\ the state that the agent's
     * previous action and thereafter its opponents actions resulted in.
     * @param actions The actions that are available in the
     * <code>newState</code>.
     */
    public void giveFeedback(int feedback, long newState, Set<Integer> actions);
    
    /**
     * Called when the game is about to get shutdown, a good place to save 
     * the current state to file.
     */
    public void shutdown();

    /**
     * Tell the agent to observe the given Observable object.
     *
     * @param obj The object to observe.
     */
    public void observe(Observable obj);
}
