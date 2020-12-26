package dots.agent;

import java.util.Observable;
import java.util.Random;
import java.util.Set;


public class RandomAgent implements Agent
{
    
    private Random random;

    
    public RandomAgent()
    {
        this(System.currentTimeMillis());
    }

    
    public RandomAgent(long seed)
    {
        random = new Random(seed);
    }

    
    @Override
    public int getAction(long state, Set<Integer> actions)
    {
        return (Integer) actions.toArray()[random.nextInt(actions.size())];
    }

    
    @Override
    public void giveFeedback(int feedback, long newState, Set<Integer> actions)
    {
        // Do nothing, as the RandomAgent has no memory.
    }

    @Override
    public void shutdown()
    {        
    }

    @Override
    public void observe(Observable obj)
    {
        // No need to observe.
    }
    @Override
    public boolean toExplore()
    {
        return false;
    }
}
