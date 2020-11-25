/*-****************************************************************************
 * QLearningAgent.java
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

import dots.agent.q.StateMatrix;
import java.util.*;
import java.util.logging.*;


/**
 * An agent using the Q-learning algorithm.
 *
 * This agent uses Q-learning for choosing the action to make. It does not
 * implement storage of Q values, so any type of storage could be used,
 * including artificial neural networks.
 */
public class QLearningAgent implements Agent
{
    /**
     * The default discount factor used in the algorithm.
     */
    public static final float DEFAULT_DISCOUNT_FACTOR = 0.5f;

    /**
     * The default learning rate used in the algorithm.
     */
    public static final float DEFAULT_LEARNING_RATE = 0.8f;

    /**
     * The state matrix used to store all Q values.
     */
    private StateMatrix stateMatrix;

    /**
     * The last state.
     */
    private long lastState;

    /**
     * The last action.
     */
    private int lastAction;

    /**
     * The sum of feedback received.
     */
    private int numPoints;

    /**
     * The discount factor used in the algorithm.
     */
    private final float discountFactor;

    /**
     * The learning rate used in the algorithm.
     */
    private final float learningRate;

    /**
     * Status of training mode. True iff the agent should work read/write,
     * false if the agent should work readonly (with respect to state matrix).
     */
    boolean training;

    /**
     * The logger responsible for logging
     */
    Logger logger = Logger.getLogger(getClass().getPackage().getName());

    /**
     * Create a new QLearningAgent. Use default values for learning
     * rate and discount factor.
     *
     * @param training True iff the agent should be in training mode.
     * @param matrix The state matrix to use.
     */
    public QLearningAgent(boolean training, StateMatrix matrix)
    {
        this(training, matrix, DEFAULT_DISCOUNT_FACTOR, DEFAULT_LEARNING_RATE);
    }

    /**
     * Create a new QLearningAgent.
     *
     * @param training True iff the agent should be in training mode.
     * @param matrix The state matrix to use.
     * @param discountFactor The discount factor to use.
     * @param learningRate The learning rate to use.
     */
    public QLearningAgent(boolean training, StateMatrix matrix,
            float discountFactor, float learningRate)
    {
        this.stateMatrix = matrix;
        this.discountFactor = discountFactor;
        this.learningRate = learningRate;
        this.training = training;
        this.numPoints = 0;
    }

    /**
     * @see Agent#getAction
     */
    @Override
    public int getAction(long state, Set<Integer> actions)
    {
        lastState = state;

        float max = -Float.MAX_VALUE;
        lastAction = -1;


        for (Integer i : actions) {
            if (training && stateMatrix.getQ(state, i) == 0) {
                logger.finest(String.format("Exploring action %d for state %d.",
                            i, state));
                max = stateMatrix.getQ(state, i);
                lastAction = i;
                break;
            }

            if (stateMatrix.getQ(state, i) > max) {
                max = stateMatrix.getQ(state, i);
                lastAction = i;
            }
        }

        logger.finest(String.format("Using action %d with Q-value %f",
                    lastAction, stateMatrix.getQ(state, lastAction)));

        return lastAction;
    }

    /**
     * @see Agent#giveFeedback
     */
    @Override
    public void giveFeedback(int feedback, long newState, Set<Integer> actions)
    {
        numPoints += feedback;

        logger.entering(QLearningAgent.class.getName(), "giveFeedback",
            new Object[] {feedback, newState});
        
        if (training) {
            float lastQ = stateMatrix.getQ(lastState, lastAction);

            float max = 0;

            if (actions != null) {
                int maxAction = -1;
                max = -Float.MAX_VALUE;
                for (Integer i : actions) {
                    if (stateMatrix.getQ(newState, i) > max) {
                        maxAction = i;
                        max = stateMatrix.getQ(newState, i);
                    }
                }
            }

            float newQValue =
                lastQ
                + learningRate
                    * (feedback + discountFactor
                        * max - lastQ);

            logger.finest(String.format("Saving Q-value (%d, %d, %f) %f", lastState,
                        lastAction, newQValue, max));
            // FileWriter myWriter;
			// try {
			// 	myWriter = new FileWriter("filename.txt");
			// 	myWriter.write(String.format("(%d, %d, %f) %f", lastState, lastAction, newQValue, max));
	        //     myWriter.close();
			// } catch (IOException e) {
			// 	// TODO Auto-generated catch block
			// 	e.printStackTrace();
			// }
            

            stateMatrix.setQ(lastState, lastAction, newQValue);
        } else {
            logger.finest("Training mode disabled, matrix not updated.");
        }
    }

    /**
     * @see Agent#shutdown
     */
    @Override
    public void shutdown()
    {
        // long numActions = 
        // logger.info()
        logger.info(String.format(" | QLearning score: %d ", numPoints));
    }

    /**
     * @see Agent#observe
     */
    @Override
    public void observe(Observable obj)
    {
        // No need to observe.
    }
}
