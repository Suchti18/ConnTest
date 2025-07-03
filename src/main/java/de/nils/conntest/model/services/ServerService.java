package de.nils.conntest.model.services;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.communication.Server;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ServerService implements EventListener
{
    private static final Logger log = LoggerFactory.getLogger(ServerService.class);

    private Thread serverThread;
    private Server server;

    public void startServer(int port)
    {
        try
        {
            server = new Server(port);
            serverThread = new Thread(server::start);
            serverThread.start();
        }
        catch (IOException e)
        {
            log.error("Error while starting server", e);
        }
    }

    public void stopServer()
    {

    }

    @Override
    public void handleEvent(Event event)
    {
        switch(event.eventType())
        {
            case START_SERVER ->
            {
                event.mustExist(Const.Event.SERVER_PORT_KEY);

                startServer(event.getData(Const.Event.SERVER_PORT_KEY));
            }
            case STOP_SERVER ->
            {
                stopServer();
            }
        }
    }
}
