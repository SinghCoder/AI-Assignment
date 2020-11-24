/*-****************************************************************************
 * DotsNBoxes.java
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

import dots.agent.*;
import dots.engine.*;

import java.util.*;
import java.util.logging.Logger;

/**
 * This class is the engine for the Dots and boxes game. An instance of
 * this class simulates a number of games between two or more agents. It also
 * logs statistics about the game played.
 */
public class DotsNBoxes extends Thread {
    /**
     * A class used to circumvent the protected access mode to
     * Observable.setChanged.
     */
    private class Obs extends Observable {
        public void change() {
            setChanged();
        }
    }

    /**
     * The set of agents in the game. This must contain exactly two agents.
     */
    List<Agent> agents;

    /**
     * The current scoring card.
     */
    Scoring scoring;

    /**
     * The random object used by this instance of DotsNBoxes.
     */
    Random random;

    /**
     * The grid the engine should work with.
     */
    Grid grid;

    /**
     * The Logger used for logging.
     */
    Logger logger;

    /**
     * The number of rounds to run.
     */
    int rounds;

    /**
     * The number of rounds to run between emitting results.
     */
    int roundsBetweenResults;

    /**
     * An observable object.
     */
    Obs obs;

    /**
     * Calculate reward to give for a turn, based on how many boxes that was
     * completed.
     *
     * @param numBoxes The number of boxes completed this turn.
     * @return the reward to give to the client.
     */
    private static int turnReward(int numBoxes)
    {
        return 0;
    }

    /**
     * Calculate reward to give for winning/losing a game.
     *
     * @param win True iff the calculation should be based on a win.
     */
    private static int endReward(boolean win)
    {
        return win ? 10 : -10;
    }

    /**
     * Reward given for incorrect setting of bar, e.g.\ if the bar cannot be
     * set or there is no such bar.
     */
    private static final int FAIL_REWARD = -100;

    /**
     * Create a new DotsNBoxes instance. The random instance is seeded with the
     * current time.
     * 
     * @param grid The grid to work with.
     * @param agent1 The first agent.
     * @param agent2 The second agent.
     * @param rounds The number of rounds to run.
     */
    public DotsNBoxes(Grid grid, Agent agent1, Agent agent2, int rounds) {
        this(grid, agent1, agent2, rounds, -1);
    }

    /**
     * Create a new DotsNBoxes instance.
     * 
     * @param grid The grid to work with.
     * @param agent1 The first agent.
     * @param agent2 The second agent.
     * @param rounds The number of rounds to run.
     * @param roundsBetweenResults The number of rounds between emission of
     * results. A value &lt;= 0 means that emission of results is only done
     * after all rounds.
     */
    public DotsNBoxes(Grid grid, Agent agent1, Agent agent2, int rounds,
            int roundsBetweenResults) {
        logger = Logger.getLogger(getClass().getPackage().getName());
        agents = new Vector<Agent>(2);
        scoring = new Scoring();

        agents.add(agent1);
        agents.add(agent2);
        scoring.addAgent(agent1);
        scoring.addAgent(agent2);

        obs = new Obs();

        agent1.observe(obs);
        agent2.observe(obs);

        random = new Random(System.currentTimeMillis());

        this.grid = grid;
        this.rounds = rounds;
        this.roundsBetweenResults = roundsBetweenResults;

        obs.change();
        obs.notifyObservers(grid);
    }

    /**
     * Creates a new game and lets the agents play it. The start agent is chosen
     * randomly.
     * 
     * @throws InterruptedException May be thrown to indicate that the agent
     *             wants to quit the game.
     */
    public void playRound() throws InterruptedException {
        Agent previous = null;
        int numPoints = 0;
        int startIndex = random.nextInt(agents.size());
        int index = startIndex;
        Set<Integer> available = null;

        int[] previousNumBoxes = new int[agents.size()];

        for (int i = 0; i < previousNumBoxes.length; ++i)
            previousNumBoxes[i] = -1;

        grid.reset();
        scoring.resetBoxes();

        while (!grid.isFinished()) {
            Agent current = agents.get(index);

            int numBoxes;

            int action;
            long state = grid.getIdentifier();
            available = grid.getAvailableLines();

            if (previousNumBoxes[index] != -1) {
                current.giveFeedback(
                        turnReward(previousNumBoxes[index]), state, available);
            }

            action = current.getAction(state, available);
            while (!available.contains(action)) {
                if (action == -1)
                    throw new InterruptedException("Interrupted by agent.");

                current.giveFeedback(FAIL_REWARD, state, available);
                action = current.getAction(state, available);
            }

            numBoxes = grid.setLine(action);
            numPoints += numBoxes;

            previousNumBoxes[index] = numBoxes;

            if (numBoxes > 0) {
                scoring.incrementBoxes(current, numBoxes);
            } else {
                previous = current;
                index = (index + 1) % agents.size();
                numPoints = 0;
            }

            obs.change();
            obs.notifyObservers(grid);
            obs.change();
            obs.notifyObservers(scoring);
        }

        List<Agent> winners = scoring.getWinners();

        if (winners.size() == 1) {
            // Give positive feedback to agents with the highest score on the
            // round, and negative to the rest.
            for (int i = 0; i < agents.size(); ++i) {
                Agent agent = agents.get(i);
                if (winners.contains(agent)) {
                    agent.giveFeedback(endReward(true),
                            grid.getIdentifier(), null);
                    scoring.increment(agent);
                    logger.fine(String.format(
                            "Agent %s won the round. Boxes: %d", agent
                                    .getClass().getSimpleName(), scoring
                                    .getNumBoxes(agent)));
                } else {
                    agent.giveFeedback(endReward(false),
                            grid.getIdentifier(), null);
                }
            }
        } else {
            // Give positive feedback to the agent that started last.
            for (int i = 0; i < agents.size(); ++i) {
                Agent agent = agents.get(i);

                if ((startIndex - 1 + agents.size()) % agents.size() == i) {
                    scoring.increment(agent);
                    agent.giveFeedback(endReward(true),
                            grid.getIdentifier(), null);
                } else {
                    agent.giveFeedback(endReward(false),
                            grid.getIdentifier(), null);
                }
            }
        }
    }

    /**
     * Run all games.
     */
    public void run() {
        int i = 0;
        try {
            for (; i < rounds; ++i) {
                if (i > 0 && roundsBetweenResults > 0
                        && i % roundsBetweenResults == 0) {
                    for (String result : generateResults(i)) {
                        logger.info(result);
                    }
                }

                logger.finest("Playing new round.");
                playRound();
            }
        } catch (InterruptedException e) {
            logger.finer(e.getMessage());
            logger.fine("Exiting game.");
        }

        for (String result : generateResults(i)) {
            logger.info(result);
        }
    }

    /**
     * Generate current results for the current standing in the game.
     *
     * @param numRounds The number of rounds run so far.
     * @return a list of strings holding the results.
     */
    private List<String> generateResults(int numRounds) {
        List<String> results = new LinkedList<String>();

        for (int i = 0; i < agents.size(); ++i) {
            Agent agent = agents.get(i);
            int wins = scoring.getScore(agent);
            
            results.add(String.format("#%d %s: %d wins, %d losses. Share: %f", (i + 1),
                        agent.getClass().getSimpleName(), wins, numRounds - wins, (double)wins/numRounds));

        }

        return results;
    }

    /**
     * Called when the game is about to be shut down.
     * 
     * XXX In order for the Bootstrapper to be able to tell this class when its
     * time to quit
     */
    public void shutdown() {
        logger.entering(getClass().getName(), "shutdown");
        for (Agent agent : agents) {
            agent.shutdown();
        }
        logger.exiting(getClass().getName(), "shutdown");
    }
}
