package dots.agent.q;

import dots.agent.q.StateMatrix;
import java.io.*;


public class ArrayStateMatrix implements StateMatrix, Serializable
{
    
    String filename;

    
    float matrix[][];

    
    static final float DEFAULT_Q_VALUE = -1.0f;

    
    private ArrayStateMatrix(String filename, float matrix[][])
    {
        this.filename = filename;
        this.matrix = matrix;
    }

    @Override
    public float getQ(long state, int action)
    {
        return matrix[(int)state][action];
    }

    @Override
    public void setQ(long state, int action, float value)
    {
        matrix[(int)state][action] = value;
    }

    @Override
    public float getMaxValue(long state)
    {
        float max = Float.MIN_VALUE;

        for (int i = 0; i < matrix[(int)state].length; ++i)
            if (matrix[(int)state][i] > max)
                max = matrix[(int)state][i];

        return max;
    }

    @Override
    public int getNumActions()
    {
        return matrix[0].length;
    }

    @Override
    public long getNumStates()
    {
        return matrix.length;
    }

    public void shutdown()
    {
        try {
            ObjectOutputStream objStream = new ObjectOutputStream(
                    new FileOutputStream(filename));
            objStream.writeObject(this);
            objStream.close();
        } catch (Exception e) {
            // We can't recover here.
        }
    }

    
    public static StateMatrix create(String filename, long numStates,
            int numActions) throws OutOfMemoryError
    {
        float matrix[][] = new float[(int)numStates][numActions];

        for (int i = 0; i < numStates; ++i) {
            for (int j = 0; j < numActions; ++j) {
                matrix[i][j] = DEFAULT_Q_VALUE;
            }
        }

        return new ArrayStateMatrix(filename, matrix);
    }

    
    public static StateMatrix load(String filename, long numStates,
            int numActions) throws IOException, OutOfMemoryError
    {
        StateMatrix stateMatrix;
        ObjectInputStream objStream = new ObjectInputStream(
                new FileInputStream(filename));

        try {
            stateMatrix = (ArrayStateMatrix)
                objStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        } finally {
            objStream.close();
        }

        if (stateMatrix.getNumActions() != numActions
                || stateMatrix.getNumStates() != numStates)
            throw new IOException("Invalid number of states/actions " +
                    "in the loaded file.");

        return stateMatrix;
    } 
}
