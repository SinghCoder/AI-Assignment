/*-****************************************************************************
 * Formatter.java
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

import java.util.logging.LogRecord;

/**
 * A basic formatter with no color support.
 */
public class Formatter extends java.util.logging.Formatter
{
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
