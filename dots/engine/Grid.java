package dots.engine;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


public class Grid
{
    
    public static final int DEFAULT_HEIGHT = 4;

    
    public static final int DEFAULT_WIDTH = 4;

    
    private int height;

    
    private int width;

    
    private boolean[] lines;

    
    private Logger logger;

    
    private Set<Integer> available;

    
    public Grid()
    {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    
    public Grid(int width, int height)
    {
        this.width = width;
        this.height = height;

        int numLines = height * (width - 1) + width * (this.height - 1);
        lines = new boolean[numLines];

        logger = Logger.getLogger(getClass().getPackage().getName());
        available = new HashSet<Integer>();
    }

    
    public long getIdentifier()
    {
        int sum = 0;

        for (int i = 0; i < lines.length; ++i)
            if (lines[i])
                sum += 1 << i;

        return sum;
    }

    
    public static long getIdentifier(long state, int action)
    {
        if (((state >> action) & 1 )== 1)
            return state;
        return state + (1 << action);
    }

    
    public boolean isFinished()
    {
        for (int i = 0; i < lines.length; ++i)
            if (!lines[i])
                return false;
        return true;
    }

    
    public Set<Integer> getAvailableLines()
    {
        available.clear();

        for (int i = 0; i < lines.length; ++i)
            if (!lines[i])
                available.add(i);

        return available;
    }

    
    public int setLine(int line)
    {
        if (line < 0 || line >= lines.length) {
            logger.severe(String.format("Invalid line number %d supplied. " +
                        "Valid interval is [%d, %d].", line, 0,
                        lines.length - 1));
            return -1;
        }

        lines[line] = true;
        int sum = 0;

        if (isHorizontal(line)) {
            if (line >= width
                    && lines[line - (2 * width - 1)]
                    && lines[line - width]
                    && lines[line - width + 1])
                ++sum;

            if (line < lines.length - width
                    && lines[line + width - 1]
                    && lines[line + width]
                    && lines[line + (2 * width - 1)])
                ++sum;
        } else {
            if ((line % (2 * width - 1)) - (width - 1) != (width - 1)
                    && lines[line + 1]
                    && lines[line - width + 1]
                    && lines[line + width])
                ++sum;

            if ((line % (2 * width - 1)) - (width - 1) != 0
                    && lines[line - 1]
                    && lines[line - width]
                    && lines[line + width - 1])
                ++sum;

        }

        String message = String.format("Line %02d is %s and gives %d point. " +
                "State is now %d.",
                line,
                (isHorizontal(line) ? "Horizontal" : "Vertical"),
                sum,
                getIdentifier()
        );

        logger.finest(message);

        return sum;
    }

    
    private boolean isHorizontal(int n)
    {
        return n % (2 * width - 1) < width - 1;
    }

    
    public void reset()
    {
        for (int i = 0; i < lines.length; ++i)
            lines[i] = false;
    }

    
    public int getSize()
    {
        return lines.length;
    }

    
    public int getHeight()
    {
        return height;
    }

    
    public int getWidth()
    {
        return width;
    }

    
    public long getMaxIdentifier()
    {
        int sum = 0;
        for (int i = 0; i < lines.length; ++i)
            sum += 1 << i;

        return sum;
    }
}
