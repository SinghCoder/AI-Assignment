package dots;

import dots.engine.*;
import dots.util.*;
import dots.util.logging.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bootstrapper extends Thread
{
    
    private static Bootstrapper instance = new Bootstrapper();

    
    private Logger logger;

    
    private List<DotsNBoxes> games;
    
    
    private ConsoleHandler consoleHandler;

    
    private List<ShutdownHook> hooks;

    
    private Bootstrapper()
    {
        logger = Logger.getLogger(getClass().getPackage().getName());
        prepareLogger();

        games = new LinkedList<DotsNBoxes>();
        hooks = new LinkedList<ShutdownHook>();
    }

    
    public synchronized void startGame(DotsNBoxes game)
    {
        games.add(game);
        game.start();
    }

    
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

    
    public void addShutdownHook(ShutdownHook hook)
    {
        hooks.add(hook);
    }

    
    public void quit()
    {
        for (DotsNBoxes game : games)
        {
            game.interrupt();
        }
    }

    
    private void prepareLogger()
    {
        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new ColorFormatter());

        logger.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);
    }
    
    
    public void setLogLevel(Level level) 
    {
        consoleHandler.setLevel(level);
    }

    
    public static Bootstrapper getInstance()
    {
        return instance;
    }

    
    public static void main(String[] args){
		ArgHandler.handleArgs(args);
		getInstance().start();
    }
}
