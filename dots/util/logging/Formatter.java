package dots.util.logging;

import java.util.logging.LogRecord;


public class Formatter extends java.util.logging.Formatter
{
    @Override
    public String format(LogRecord record)
    {
        Object[] params = record.getParameters();
        StringBuffer buffer = new StringBuffer();

        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; ++i) {
                if (i != 0)
                    buffer.append(", ");
                buffer.append(params[i]);
            }
        }

        return String.format("%s::%s(%s): %s\n",
                record.getSourceClassName(),
                record.getSourceMethodName(),
                buffer.toString(),
                record.getMessage()
        );
    }
}
