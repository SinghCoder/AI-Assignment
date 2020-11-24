/*-****************************************************************************
 * Scoring.java
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

import dots.agent.Agent;
import java.util.*;

/**
 * A scoring card representation.
 */
public class Scoring
{
    /**
     * Stores a Agent-Score pair.
     */
    private class ScoringEntry
    {
        /**
         * The agent of the entry.
         */
        Agent agent;

        /**
         * The number of games won.
         */
        int score;

        /**
         * The number of boxes created in this round.
         */
        int boxes;

        /**
         * Create a new ScoringEntry.
         *
         * @param agent The agent of the entry.
         */
        public ScoringEntry(Agent agent)
        {
            this.agent = agent;
            score = 0;
            boxes = 0;
        }
    }

    /**
     * Stores score of all agents.
     */
    List<ScoringEntry> table;

    /**
     * Create a new Scoring object. The corresponding scoring card will
     * be empty.
     */
    public Scoring()
    {
        table = new ArrayList<ScoringEntry>();
    }

    /**
     * Reset all counters, i.e.\ set all scores to zero.
     */
    public void reset()
    {
        for (ScoringEntry entry : table) {
            entry.score = 0;
            entry.boxes = 0;
        }
    }

    /**
     * Reset box counters.
     */
    public void resetBoxes()
    {
        for (ScoringEntry entry : table)
            entry.boxes = 0;
    }

    /**
     * Add an agent to the scoring card. If the agent is already present in the
     * scoring card, nothing happens.
     *
     * @param agent The agent to add to the scoring card.
     */
    public void addAgent(Agent agent)
    {
        for (ScoringEntry entry : table)
            if (entry.agent == agent)
                return;

        table.add(new ScoringEntry(agent));
    }

    /**
     * Increment the score for the given agent. If the agent does not
     * exist in the scoring card, it is added.
     *
     * @param agent The agent who's score is to be updated.
     */
    public void increment(Agent agent)
    {
        ScoringEntry target = null;

        for (ScoringEntry entry : table) {
            if (entry.agent == agent) {
                target = entry;
                break;
            }
        }

        if (target == null) {
            target = new ScoringEntry(agent);
            table.add(target);
        }

        ++target.score;
    }

    /**
     * Increment the score for the given agent. If the agent does not
     * exist in the scoring card, it is added.
     *
     * @param agent The agent who's score is to be updated.
     * @param num The number of points to increment with.
     */
    public void incrementBoxes(Agent agent, int num)
    {
        ScoringEntry target = null;

        for (ScoringEntry entry : table) {
            if (entry.agent == agent) {
                target = entry;
                break;
            }
        }

        if (target == null) {
            target = new ScoringEntry(agent);
            table.add(target);
        }

        target.boxes += num;
    }

    /**
     * Retrieve the score of the given agent.
     *
     * @param agent The agent in question.
     * @return the score of the given agent. If no such agent exists,
     * -1 is returned.
     */
    public int getScore(Agent agent)
    {
        for (ScoringEntry entry : table)
            if (entry.agent == agent)
                return entry.score;
        return -1;
    }

    /**
     * Find the winner(s) of the current game, i.e.\ the Agent with most
     * boxes.
     *
     * @return a list with the winner(s) of the round.
     */
    public List<Agent> getWinners()
    {
        List<Agent> agents = new ArrayList<Agent>();
        int max = Integer.MIN_VALUE;

        for (ScoringEntry entry : table) {
            if (entry.boxes > max) {
                agents.clear();
                agents.add(entry.agent);
                max = entry.boxes;
            } else if (entry.boxes == max) {
                agents.add(entry.agent);
            }
        }

        return agents;
    }

    /**
     * Retrieve the score in this current game, i.e.\ the number of boxes for
     * the given agent.
     *
     * @param agent The agent in question.
     * @return the number of boxes in the current game created by the given agent.
     * If the agent does not exist in this scoring card, -1 is returned.
     */
    public int getNumBoxes(Agent agent)
    {
        for (ScoringEntry entry : table)
            if (entry.agent == agent)
                return entry.boxes;
        return -1;
    }
}
