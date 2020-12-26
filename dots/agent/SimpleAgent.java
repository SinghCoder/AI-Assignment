package dots.agent;

import java.util.Collections;
import java.util.Observable;
import java.util.Set;


public class SimpleAgent implements Agent
{
    
    @Override
    public int getAction(long state, Set<Integer> actions)
    {
        return Collections.min(actions);
    }

    
    @Override
    public void giveFeedback(int feedback, long newState, Set<Integer> actions)
    {
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
