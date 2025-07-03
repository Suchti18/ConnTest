package de.nils.conntest;

import de.nils.conntest.exception.UncaughtHandler;
import de.nils.conntest.gui.UIStart;
import de.nils.conntest.model.Model;
import de.nils.conntest.model.communication.Server;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventQueue;

import java.io.IOException;
import java.util.Map;

public class ConnTest
{
    public static void main(String[] args)
    {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtHandler());

        EventQueue.getInstance().addListener(new Model());
        EventQueue.getInstance().addEvent(new Event("Test", System.currentTimeMillis(), Map.of()));

        new UIStart();
        try
        {
            new Server(8080).start();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }


        EventQueue.getInstance().startEventQueue();
    }
}