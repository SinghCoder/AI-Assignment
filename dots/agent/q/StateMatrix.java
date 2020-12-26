package dots.agent.q;

import dots.util.ShutdownHook;


public interface StateMatrix extends ShutdownHook
{
    
    public float getQ(long state, int action);

    
    public void setQ(long state, int action, float value);

    
    public float getMaxValue(long state);

    
    public int getNumActions();

    
    public long getNumStates();
}
