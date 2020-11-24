/*-****************************************************************************
 * ColorFormatter.java
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

package dots.util.logging;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.LogRecord;
import java.util.logging.Level;

/**
 * A basic formatter for the logging framework. This formatter uses ANSI escape
 * codes to format the log messages.
 */
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
