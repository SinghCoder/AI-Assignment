/*-****************************************************************************
 * Bootstrapper.java
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

package dots;

import dots.engine.*;
import dots.util.*;
import dots.util.logging.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class acts as a boot strapper for the game engine. The main method of
 * this class calls Arghandler for parsing the command line. The Bootstrapper
 * class is then used to start the execution of games and also to cleanup when
 * games are completed. 
 */
public class Bootstrapper extends Thread
{
    /**
     * Holds the single instance of Bootstrapper.
     */
    private static Bootstrapper instance = new Bootstrapper();

    /**
     * The logger to use for logging.
     */
    private Logger logger;

    /**
     * The set of currently running games.
     */
    private List<DotsNBoxes> games;
    
    /**
     * The consolehandler which handles the console
     */
    private ConsoleHandler consoleHandler;

    /**
     * A list of object to shutdown prior to exit.
     */
    private List<ShutdownHook> hooks;

    /**
     * Creates a new Bootstrapper instance.
     */
    private Bootstrapper()
    {
        logger = Logger.getLogger(getClass().getPackage().getName());
        prepareLogger();

        games = new LinkedList<DotsNBoxes>();
        hooks = new LinkedList<ShutdownHook>();
    }

    /**
     * Start a new Game.
     *
     * @param game The game to start.
     */
    public synchronized void startGame(DotsNBoxes game)
    {
        logger.finest("Creating game simulator.");

        games.add(game);
        game.start();
    }

    /**
     * This is the cleanup-thread, responsible for shutting down all started
     * games.
     */
    @Override
    public void run()
    {
        while (games.size() > 0)
        {
            DotsNBoxes game = games.get(0);

            if (!game.isInterrupted())
            {
                try
                {
                    game.join();
                }
                catch (InterruptedException e)
                {
                    logger.finest("Caught InterruptedException from thread.");
                }
            }

            game.shutdown();

            games.remove(0);
        }

        for (ShutdownHook hook : hooks) {
            hook.shutdown();
        }

        System.exit(0);
    }

    /**
     * Add an object to the list of object to shutdown prior to exiting.
     *
     * @param hook The object to add to the list.
     */
    public void addShutdownHook(ShutdownHook hook)
    {
        hooks.add(hook);
    }

    /**
     * Causes all games to be shutdown.
     */
    public void quit()
    {
        for (DotsNBoxes game : games)
        {
            game.interrupt();
        }
    }

    /**
     * Prepares the application for logging messages.
     */
    private void prepareLogger()
    {
        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new ColorFormatter());

        logger.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);
    }
    
    /**
     * Change the log level of the consolehandler
     * 
     * @param level The new loglevel the handler will be set to
     */
    public void setLogLevel(Level level) 
    {
        consoleHandler.setLevel(level);
    }

    /**
     * @return the singleton instance of Bootstrapper.
     */
    public static Bootstrapper getInstance()
    {
        return instance;
    }

    /**
     * @param args Not used
     */
    public static void main(String[] args){
		ArgHandler.handleArgs(args);
		getInstance().start();
    }
}
