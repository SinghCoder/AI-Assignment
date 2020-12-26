package dots.engine;

import dots.agent.Agent;
import java.util.*;


public class Scoring
{
    
    private class ScoringEntry
    {
        
        Agent agent;

        
        int score;

        
        int boxes;

        
        public ScoringEntry(Agent agent)
        {
            this.agent = agent;
            score = 0;
            boxes = 0;
        }
    }

    
    List<ScoringEntry> table;

    
    public Scoring()
    {
        table = new ArrayList<ScoringEntry>();
    }

    
    public void reset()
    {
        for (ScoringEntry entry : table) {
            entry.score = 0;
            entry.boxes = 0;
        }
    }

    
    public void resetBoxes()
    {
        for (ScoringEntry entry : table)
            entry.boxes = 0;
    }

    
    public void addAgent(Agent agent)
    {
        for (ScoringEntry entry : table)
            if (entry.agent == agent)
                return;

        table.add(new ScoringEntry(agent));
    }

    
    public void increment(Agent agent)
    {
        ScoringEntry target = null;

        for (ScoringEntry entry : table) {
            if (entry.agent == agent) {
                target = entry;
                break;
            }
        }

        if (target == null) {
            target = new ScoringEntry(agent);
            table.add(target);
        }

        ++target.score;
    }

    
    public void incrementBoxes(Agent agent, int num)
    {
        ScoringEntry target = null;

        for (ScoringEntry entry : table) {
            if (entry.agent == agent) {
                target = entry;
                break;
            }
        }

        if (target == null) {
            target = new ScoringEntry(agent);
            table.add(target);
        }

        target.boxes += num;
    }

    
    public int getScore(Agent agent)
    {
        for (ScoringEntry entry : table)
            if (entry.agent == agent)
                return entry.score;
        return -1;
    }

    
    public List<Agent> getWinners()
    {
        List<Agent> agents = new ArrayList<Agent>();
        int max = Integer.MIN_VALUE;

        for (ScoringEntry entry : table) {
            if (entry.boxes > max) {
                agents.clear();
                agents.add(entry.agent);
                max = entry.boxes;
            } else if (entry.boxes == max) {
                agents.add(entry.agent);
            }
        }

        return agents;
    }

    
    public int getNumBoxes(Agent agent)
    {
        for (ScoringEntry entry : table)
            if (entry.agent == agent)
                return entry.boxes;
        return -1;
    }
}
