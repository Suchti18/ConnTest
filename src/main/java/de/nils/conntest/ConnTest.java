package de.nils.conntest;

import de.nils.conntest.exception.UncaughtHandler;
import de.nils.conntest.gui.UIStart;
import de.nils.conntest.model.Model;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventQueue;

import java.util.Map;

public class ConnTest
{
    public static void main(String[] args)
    {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtHandler());

        EventQueue.getInstance().addListener(new Model());
        EventQueue.getInstance().addEvent(new Event("Test", System.currentTimeMillis(), Map.of()));

        new UIStart();

        EventQueue.getInstance().startEventQueue();
    }
}