package dots.engine;

import dots.agent.*;
import dots.engine.*;
import java.util.*;
import java.util.logging.Logger;



public class DotsNBoxes extends Thread {
    
    private class Obs extends Observable {
        public void change() {
            setChanged();
        }
    }
    
    List<Agent> agents;

    Scoring scoring;
    
    Random random;
    
    Grid grid;
    
    Logger logger;
    
    int rounds;
    
    int roundsBetweenResults;
    
    Obs obs;
    
    private static int turnReward(int numBoxes)
    {
        return 0;
    }

    
    private static int endReward(boolean win)
    {
        return win ? 10 : -10;
    }

    
    private static final int FAIL_REWARD = -100;

    
    public DotsNBoxes(Grid grid, Agent agent1, Agent agent2, int rounds) {
        this(grid, agent1, agent2, rounds, -1);
    }

    
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
            int not_to_choose = action;
            if(current.toExplore())
            {                
                int size = available.size();
                int item = new Random().nextInt(size);
                int i = 0;
                for(Integer possible_action : available)
                {
                    if (i == item && possible_action != not_to_choose){
                        action = possible_action;
                        break;
                    }
                    i++;
                }
            }

            while (!available.contains(action)) {
                if (action == -1)
                    throw new InterruptedException("Interrupted by agent.");

                current.giveFeedback(FAIL_REWARD, state, available);
                action = current.getAction(state, available);
                if(current.toExplore())
                {                
                    int size = available.size();
                    int item = new Random().nextInt(size);
                    int i = 0;
                    for(Integer possible_action : available)
                    {
                        if (i == item && possible_action != not_to_choose){
                            action = possible_action;
                            break;
                        }
                        i++;
                    }
                }
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

    
    @Override
    public void run() {
        int i = 0;
        try {
            for (; i < rounds; ++i) {
                if (i > 0 && roundsBetweenResults > 0
                        && i % roundsBetweenResults == 0) {
                    for (String result : generateResults(i)) {
                        System.out.print(result);
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
            System.out.print(result);
        }
    }

    
    private List<String> generateResults(int numRounds) {
        List<String> results = new LinkedList<String>();

        for (int i = 0; i < agents.size(); ++i) {
            Agent agent = agents.get(i);
            int wins = scoring.getScore(agent);
            
            results.add(String.format("#%d %s: %d wins, %d losses. | Share: %f | ", (i + 1),
                        agent.getClass().getSimpleName(), wins, numRounds - wins, (double)wins/numRounds));

        }

        return results;
    }

    
    public void shutdown() {
        logger.entering(getClass().getName(), "shutdown");
        for (Agent agent : agents) {
            agent.shutdown();
        }
        logger.exiting(getClass().getName(), "shutdown");
    }
}
