/*-****************************************************************************
 * NativeStateMatrix.java
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

import java.io.IOException;

/**
 * A StateMatrix using JNI to store a StateMatrix with C.
 */
public class NativeStateMatrix implements StateMatrix
{
    /**
     * Used for retrieval of correct state matrix from native library.
     */
    int descriptor;

    /**
     * Load the required native library.
     * Remember to set java.library.path for this to work.
     */
    static {
        System.loadLibrary("statematrix");
    }

    /**
     * Native static method used to open a state matrix.
     *
     * @param filename The filename to open.
     * @return a descriptor used to retrieve values from the opened file
     * if successful. -1 if failed.
     */
    private static native int c_open(byte[] filename);

    /**
     * Native static method used to create a new state matrix.
     *
     * @param numStates The number of states of the new state matrix.
     * @param numActions The number of actions per state of the new
     * state matrix.
     * @return a descriptor used for retrieving values from the created
     * matrix.
     */
    private static native int c_create(byte[] filename, long numStates, int numActions);

    /**
     * Native static method used to retrieve Q values.
     *
     * @param fd The descriptor of the state matrix.
     * @param state The state part of the Q value.
     * @param action The action part of the Q value.
     * @return the corresponding state-action value.
     */
    private static native float c_getQ(int fd, long state, int action);

    /**
     * Native method used to set a Q value.
     * 
     * @param fd The descriptor of the state matrix.
     * @param state The state part of the Q value.
     * @param action The action part of the Q value.
     * @param value The new Q value.
     */
    private static native void c_setQ(int fd, long state, int action, float value);

    /**
     * Native method used to get the maximum Q value for a given state.
     *
     * @param fd The descriptor of the state matrix.
     * @param state The state in question.
     * @return The maximum value in the matrix for the given state.
     */
    private static native float c_getMaxValue(int fd, long state);

    /**
     * Native method used to retrieve the number of actions.
     *
     * @param fd The descriptor of the state matrix.
     * @return the number of actions for the particular state matrix.
     */
    private static native int c_getNumActions(int fd);

    /**
     * Native method used to retrieve the number of states.
     *
     * @param fd The descriptor of the state matrix.
     * @return The number of states in the corresponding state matrix.
     */
    private static native long c_getNumStates(int fd);

    /**
     * Native method for saving the matrix.
     *
     * @param fd The descriptor of the state matrix.
     * @return 0 on success, -1 on failure.
     */
    private static native int c_save(int fd);

    /**
     * Create a NativeStateMatrix.
     *
     * @param descriptor The descriptor to use in communication with
     * native lib.
     */
    private NativeStateMatrix(int descriptor)
    {
        this.descriptor = descriptor;
    }

    /**
     * Create a new state matrix.
     *
     * @param filename The filename of the file where to store the matrix.
     * @param numStates The number of states that this state matrix should
     * be able to hold.
     * @param numActions The number of actions available in each state.
     * @throws OutOfMemoryError if not enough memory could be allocated.
     * @return the NativeStateMatrix created.
     */
    public static NativeStateMatrix create(String filename, long numStates, int numActions)
    {
        int descriptor = NativeStateMatrix.c_create(filename.getBytes(), numStates, numActions);

        if (descriptor == -1)
            throw new OutOfMemoryError();

        return new NativeStateMatrix(descriptor);
    }

    /**
     * Open a previously saved state matrix.
     *
     * @param filename The filename to open.
     * @param numStates The number of states in the loaded matrix.
     * @param numActions The number of actions per state in the loaded matrix.
     * @throws IOException if there was an IO error while loading the matrix.
     * This exception may be thrown to indicate that the number of states/actions
     * of the loaded file was not the same as the number given as arguments.
     */
    public static NativeStateMatrix load(String filename,
            long numStates, int numActions) throws IOException
    {
        int descriptor = NativeStateMatrix.c_open(filename.getBytes());

        if (descriptor == -1
                || NativeStateMatrix.c_getNumStates(descriptor) != numStates
                || NativeStateMatrix.c_getNumActions(descriptor) != numActions)
            throw new IOException("Failed to open NativeStateMatrix file.");

        return new NativeStateMatrix(descriptor);
    }

    public float getQ(long state, int action)
    {
        return NativeStateMatrix.c_getQ(descriptor, state, action);
    }

    public void setQ(long state, int action, float value)
    {
        NativeStateMatrix.c_setQ(descriptor, state, action, value);
    }

    public float getMaxValue(long state)
    {
        return NativeStateMatrix.c_getMaxValue(descriptor, state);
    }

    public int getNumActions()
    {
        return NativeStateMatrix.c_getNumActions(descriptor);
    }

    public long getNumStates()
    {
        return NativeStateMatrix.c_getNumStates(descriptor);
    }

    public void shutdown()
    {
        if (NativeStateMatrix.c_save(descriptor) != 0) {
            // Nothing we can do here.
        }
    }
}
