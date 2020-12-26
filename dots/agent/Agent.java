package dots.agent;

import java.util.*;


public interface Agent
{
    
    public int getAction(long state, Set<Integer> actions);

    
    public void giveFeedback(int feedback, long newState, Set<Integer> actions);
    
    
    public void shutdown();

    public boolean toExplore();

    
    public void observe(Observable obj);
}
