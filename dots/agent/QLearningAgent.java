package dots.agent;
import dots.agent.q.StateMatrix;
import java.util.*;
import java.util.logging.*;

public class QLearningAgent implements Agent
{
    
    public static final float DEFAULT_DISCOUNT_FACTOR = 0.5f;
    
    public static final float DEFAULT_LEARNING_RATE = 0.8f;
    
    public static final float DEFAULT_EXPLORATION_QUOTIENT = -0.1f;
    
    private StateMatrix stateMatrix;
    
    private long lastState;
    
    private int lastAction;
    
    private int numPoints;
    
    private final float discountFactor;
    
    private final float learningRate;
    
    public final float explorationQuotient;
    
    boolean training;
    
    Logger logger = Logger.getLogger(getClass().getPackage().getName());
    
    public QLearningAgent(boolean training, StateMatrix matrix)
    {
        this(training, matrix, DEFAULT_DISCOUNT_FACTOR, DEFAULT_LEARNING_RATE, DEFAULT_EXPLORATION_QUOTIENT);
    }
    
    public QLearningAgent(boolean training, StateMatrix matrix,
            float discountFactor, float learningRate, float explorationQuotient)
    {
        this.stateMatrix = matrix;
        this.discountFactor = discountFactor;
        this.learningRate = learningRate;
        this.training = training;
        this.numPoints = 0;
        this.explorationQuotient = explorationQuotient;
    }
    
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
            										        												            
            stateMatrix.setQ(lastState, lastAction, newQValue);
        } else {
            logger.finest("Training mode disabled, matrix not updated.");
        }
    }
    
    @Override
    public void shutdown()
    {
        System.out.println(String.format(" | QLearning score: %d ", numPoints));
    }
    
    @Override
    public void observe(Observable obj)
    {
            }
    @Override
    public boolean toExplore()
    {
        Random rand = new Random();        
        float randNo = rand.nextFloat();
        if(explorationQuotient > randNo) {
           return true; 
        }
        return false;
    }
}
