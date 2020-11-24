/*-****************************************************************************
 * ArrayStateMatrix.java
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

package dots.agent.q;

import dots.agent.q.StateMatrix;
import java.io.*;

/**
 * Holds a StateMatrix in an array.
 *
 * This StateMatrix is written in pure Java. Memory usage is huge in comparison
 * with the NativeStateMatrix C implementation.
 */
public class ArrayStateMatrix implements StateMatrix, Serializable
{
    /**
     * The filename where to store the ArrayStateMatrix.
     */
    String filename;

    /**
     * The internal matrix of Q values.
     */
    float matrix[][];

    /**
     * Default value of Q-value.
     */
    static final float DEFAULT_Q_VALUE = -1.0f;

    /**
     * Create a new ArrayStateMatrix.
     *
     * @param filename The filename where to store the matrix.
     * @param matrix The internal matrix with Q values.
     */
    private ArrayStateMatrix(String filename, float matrix[][])
    {
        this.filename = filename;
        this.matrix = matrix;
    }

    public float getQ(long state, int action)
    {
        return matrix[(int)state][action];
    }

    public void setQ(long state, int action, float value)
    {
        matrix[(int)state][action] = value;
    }

    public float getMaxValue(long state)
    {
        float max = Float.MIN_VALUE;

        for (int i = 0; i < matrix[(int)state].length; ++i)
            if (matrix[(int)state][i] > max)
                max = matrix[(int)state][i];

        return max;
    }

    public int getNumActions()
    {
        return matrix[0].length;
    }

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

    /**
     * Create a new StateMatrix with the specified number of states.
     *
     * @param filename The filename from where to load the matrix.
     * @param numStates The number of states in the loaded matrix.
     * @param numActions The number of actions per state in the loaded matrix.
     * @throws OutOfMemoryError if not enough memory could be allocated.
     */
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

    /**
     * Load a StateMatrix from file.
     *
     * @param filename The filename from where to load the matrix.
     * @param numStates The number of states in the loaded matrix.
     * @param numActions The number of actions per state in the loaded matrix.
     * @throws IOException if there was an IO error while loading the matrix.
     * This exception may be thrown to indicate that the number of states/actions
     * of the loaded file was not the same as the number given as arguments.
     * @throws OutOfMemoryError if not enough memory could be allocated.
     */
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
