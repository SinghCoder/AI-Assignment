package dots;

import dots.agent.*;
import dots.agent.q.*;
import dots.engine.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.*;


public class ArgHandler
{
    
    private static Logger logger = Logger.getLogger(ArgHandler.class.getName());

    
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

                if (params.length != 3 && params.length != 6) {
                    logger.severe("Expected --agent " +
                            "QlearningAgent:<training>:<filename>" +
                            "[:<discount factor>:<learning rate>:<exploration quotient>]");
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
                float explorationQuotient = QLearningAgent.DEFAULT_EXPLORATION_QUOTIENT;

                if (params.length == 6) {
//                	logger.info(String.format("got params3,4 = %s, %s", params[3], params[4]));
                    try {
//                        logger.info(String.format("got params3,4 = %s, %s", params[3], params[4]));
                        discountFactor = Float.parseFloat(params[3]);
                        learningRate = Float.parseFloat(params[4]);
                        explorationQuotient = Float.parseFloat(params[5]);
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
                    } else if (explorationQuotient < 0 || explorationQuotient >= 1) {
                        logger.severe(String.format("Exploration Quotient must " +
                                "be in range [0, 1), got %f.", explorationQuotient));
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
                        learningRate, explorationQuotient);
            } else {
                try {
                    agent = (Agent) classLoader.loadClass(className).newInstance();
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
