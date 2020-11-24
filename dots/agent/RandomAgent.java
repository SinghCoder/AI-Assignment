/*-****************************************************************************
 * RandomAgent.java
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

import java.util.Set;
import java.util.Random;
import java.util.Observable;

/**
 * An agent with totally random behavior.
 *
 * This agent chooses action on a given state at random. The purpose of this
 * agent is to use it for training an instance of QLearningAgent.
 */
public class RandomAgent implements Agent
{
    /**
     * The random instance used to create random behavior.
     */
    private Random random;

    /**
     * Create a new RandomAgent. The random instance is seeded with the
     * current time.
     */
    public RandomAgent()
    {
        this(System.currentTimeMillis());
    }

    /**
     * Create a new RandomAgent.
     *
     * @param seed The seed for the random instance.
     */
    public RandomAgent(long seed)
    {
        random = new Random(seed);
    }

    /**
     * @see Agent
     */
    public int getAction(long state, Set<Integer> actions)
    {
        return (Integer) actions.toArray()[random.nextInt(actions.size())];
    }

    /**
     * @see Agent
     */
    public void giveFeedback(int feedback, long newState, Set<Integer> actions)
    {
        // Do nothing, as the RandomAgent has no memory.
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
