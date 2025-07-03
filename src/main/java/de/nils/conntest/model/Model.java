package de.nils.conntest.model;

import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import de.nils.conntest.model.services.ServerService;

public class Model implements EventListener
{
    private final ServerService serverService;

    public Model()
    {
        serverService = new ServerService();
    }

    public ServerService getServerService()
    {
        return serverService;
    }

    @Override
    public void handleEvent(Event event)
    {
        System.out.println("Model received event");
    }
}
