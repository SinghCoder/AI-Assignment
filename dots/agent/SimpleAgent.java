/*-****************************************************************************
 * SimpleAgent.java
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

import java.util.Observable;
import java.util.Set;
import java.util.Collections;

/**
 * An agent with a very basic strategy.
 *
 * This agent always chooses the smallest action number possible, i.e.\ if
 * action 5,7,8 are available, 5 will be chosen.
 */
public class SimpleAgent implements Agent
{
    /**
     * @see Agent
     */
    public int getAction(long state, Set<Integer> actions)
    {
        return Collections.min(actions);
    }

    /**
     * Nothing is done here science our SimpleAgent does not have a memory
     *
     * @see Agent
     */
    public void giveFeedback(int feedback, long newState, Set<Integer> actions)
    {
    }

    /** 
     * @see dots.agent.Agent#shutdown()
     */
    @Override
    public void shutdown()
    {        
    }

    public void observe(Observable obj)
    {
        // No need to observe.
    }
}
