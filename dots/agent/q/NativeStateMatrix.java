package dots.agent.q;

import java.io.File;
import java.io.IOException;


public class NativeStateMatrix implements StateMatrix
{
    
    int descriptor;

    
    static {
        File f = new File("c/libstatematrix.so");
        System.load(f.getAbsolutePath());
    }

    
    private static native int c_open(byte[] filename);

    
    private static native int c_create(byte[] filename, long numStates, int numActions);

    
    private static native float c_getQ(int fd, long state, int action);

    
    private static native void c_setQ(int fd, long state, int action, float value);

    
    private static native float c_getMaxValue(int fd, long state);

    
    private static native int c_getNumActions(int fd);

    
    private static native long c_getNumStates(int fd);

    
    private static native int c_save(int fd);

    
    private NativeStateMatrix(int descriptor)
    {
        this.descriptor = descriptor;
    }

    
    public static NativeStateMatrix create(String filename, long numStates, int numActions)
    {
        int descriptor = NativeStateMatrix.c_create(filename.getBytes(), numStates, numActions);

        if (descriptor == -1)
            throw new OutOfMemoryError();

        return new NativeStateMatrix(descriptor);
    }

    
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

    @Override
    public float getQ(long state, int action)
    {
        return NativeStateMatrix.c_getQ(descriptor, state, action);
    }

    @Override
    public void setQ(long state, int action, float value)
    {
        NativeStateMatrix.c_setQ(descriptor, state, action, value);
    }

    @Override
    public float getMaxValue(long state)
    {
        return NativeStateMatrix.c_getMaxValue(descriptor, state);
    }

    @Override
    public int getNumActions()
    {
        return NativeStateMatrix.c_getNumActions(descriptor);
    }

    @Override
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
