package de.nils.conntest.model;

import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;

public class Model implements EventListener
{
    @Override
    public void handleEvent(Event event)
    {
        System.out.println("Model received event");
    }
}
