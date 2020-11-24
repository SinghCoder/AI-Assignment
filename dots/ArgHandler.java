/*-****************************************************************************
 * ArgHandler.java
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

package dots;

import java.util.*;
import java.util.logging.*;
import java.io.IOException;

import dots.agent.*;
import dots.agent.q.*;
import dots.engine.*;

public class ArgHandler
{
    /**
     * The logger for this class
     */
    private static Logger logger = Logger.getLogger(ArgHandler.class.getName());

    /**
     * This method handles the arguments passed to the program
     * 
     * @param args The arguments that are going to be parsed
     */
    public static void handleArgs(String[] args)
    {
        int numGames = 1;
        List<String> agentParams = new ArrayList<String>();
        List<Agent> agents = new ArrayList<Agent>();
        Map<String, StateMatrix> matrices = new HashMap<String, StateMatrix>();

        int width = Grid.DEFAULT_WIDTH;
        int height = Grid.DEFAULT_HEIGHT;
        int resultsInterval = -1;
        String logLevel = null;

        try {
            for (int i = 0; i < args.length; ++i)
            {
                if (args[i].equals("--num"))
                {
                    numGames = Integer.parseInt(args[++i]);
                }
                else if (args[i].equals("--agent"))
                {
                    agentParams.add(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("--loglevel"))
                {
                    logLevel = args[++i];
                }
                else if (args[i].equalsIgnoreCase("--width"))
                {
                    width = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("--height"))
                {
                    height = Integer.parseInt(args[++i]);
                }
                else if (args[i].equalsIgnoreCase("--results-interval"))
                {
                    resultsInterval = Integer.parseInt(args[++i]);
                }
                else
                {
                    logger.severe(String.format("Illegal parameter \"%s\".", args[i]));
                    return;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.severe("Need more arguments!");
            return;
        }

        Bootstrapper bootstrapper = Bootstrapper.getInstance();

        // In case we got a loglevel from the argument list
        if (logLevel != null)
            bootstrapper.setLogLevel(Level.parse(logLevel));

        Grid grid = new Grid(width, height);

        for (String param : agentParams) {
            String[] params = param.split(":");
            String className = "dots.agent." + params[0];

            ClassLoader classLoader = ArgHandler.class.getClassLoader();
            Agent agent = null;

            if (className.equals("dots.agent.QLearningAgent")) {
                StateMatrix matrix = null;

                if (params.length != 3 && params.length != 5) {
                    logger.severe("Expected --agent " +
                            "QlearningAgent:<training>:<filename>" +
                            "[:<discount factor>:<learning rate>]");
                    return;
                }

                if (!(params[1].equals("false")
                            || params[1].equals("true"))) {
                    logger.severe("Argument training can only have values " +
                            "`true` | `false`.");
                    return;
                }

                boolean training = params[1].equals("true");
                String filename = params[2].trim();

                if (filename.length() == 0) {
                    logger.severe(String.format("Invalid filename '%s'.",
                                filename));
                    return;
                }

                float learningRate = QLearningAgent.DEFAULT_LEARNING_RATE;
                float discountFactor = QLearningAgent.DEFAULT_DISCOUNT_FACTOR;

                if (params.length == 5) {
                    try {
                        discountFactor = Float.parseFloat(params[3]);
                        learningRate = Float.parseFloat(params[4]);
                    } catch (NumberFormatException e) {
                        logger.severe("Incorrect format on learning rate or " +
                                "discount factor.");
                        return;
                    }

                    if (learningRate <= 0 || learningRate > 1) {
                        logger.severe(String.format("Learning rate must be " +
                                    "in range (0, 1], got %f.", learningRate));
                        return;
                    } else if (discountFactor < 0 || discountFactor >= 1) {
                        logger.severe(String.format("Discount factor must " +
                                "be in range [0, 1), got %f.", discountFactor));
                        return;
                    }
                }

                if (matrices.containsKey(filename)) {
                    matrix = matrices.get(filename);
                } else {
                    logger.fine(String.format("Loading file name '%s'.", filename));

                    try {
                        matrix = NativeStateMatrix.load(filename,
                                grid.getMaxIdentifier() + 1, grid.getSize());
                    } catch (IOException e) {
                        logger.fine("File not found. Creating new matrix.");

                        try {
                            matrix = NativeStateMatrix.create(filename,
                                    grid.getMaxIdentifier() + 1, grid.getSize());
                        } catch (OutOfMemoryError e2) {
                            logger.severe("Out of memory. Terminating.");
                            return;
                        }
                    } catch (OutOfMemoryError e) {
                        logger.severe("Out of memory. Terminating.");
                        return;
                    }

                    matrices.put(filename, matrix);
                    bootstrapper.addShutdownHook(matrix);
                }

                agent = new QLearningAgent(training, matrix, discountFactor,
                        learningRate);
            } else {
                try {
                    agent = (Agent) classLoader.
                        loadClass(className).newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    return;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            }

            agents.add(agent);
        }

        if (agents.size() == 0) {
            logger.severe("At least two agents needed.");
            return;
        } else if (agents.size() % 2 != 0) {
            logger.severe("An even number of agents required.");
            return;
        }

        for (int i = 0; i < agents.size(); i += 2) {
            DotsNBoxes game = new DotsNBoxes(grid, agents.get(i),
                    agents.get(i + 1), numGames, resultsInterval);
            bootstrapper.startGame(game);
            grid = new Grid(width, height);
        }
    }
}
