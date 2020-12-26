package dots.util.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class ColorFormatter extends java.util.logging.Formatter
{
    String defaultColor;
    Map<Level, String> colors;

    public ColorFormatter()
    {
        defaultColor = "34";

        colors = new HashMap<Level, String>();
        colors.put(Level.INFO, "32");
        colors.put(Level.WARNING, "33");
        colors.put(Level.SEVERE, "31");
    }

    @Override
    public String format(LogRecord record)
    {
        String color = colors.get(record.getLevel());

        if (color == null)
            color = defaultColor;

        Object[] params = record.getParameters();
        StringBuffer buffer = new StringBuffer();

        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; ++i) {
                if (i != 0)
                    buffer.append(", ");
                buffer.append(params[i]);
            }
        }

        return String.format("\033[3m%s::%s(%s): \033[0m\033[%s;1m%s \033[0m\n",
                record.getSourceClassName(),
                record.getSourceMethodName(),
                buffer.toString(),
                color,
                record.getMessage()
        );
    }
}
