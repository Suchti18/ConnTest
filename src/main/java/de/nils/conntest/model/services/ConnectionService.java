package de.nils.conntest.model.services;

import de.nils.conntest.model.Model;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;

public class ConnectionService implements EventListener
{
    private final Model model;

    public ConnectionService(Model model)
    {
        this.model = model;
    }

    @Override
    public void handleEvent(Event event)
    {

    }
}
