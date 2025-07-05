package de.nils.conntest.model.services;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.Model;
import de.nils.conntest.model.communication.Message;
import de.nils.conntest.model.communication.MessageType;
import de.nils.conntest.model.communication.Server;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class ServerService implements EventListener
{
    private static final Logger log = LoggerFactory.getLogger(ServerService.class);

    private Model model;
    private Thread serverThread;
    private Server server;

    public ServerService(Model model)
    {
        this.model = model;
    }

    public void startServer(int port)
    {
        if(serverThread != null && serverThread.isAlive() && !model.getClientService().isClientRunning())
        {
            return;
        }

        try
        {
            server = new Server(port);
            serverThread = new Thread(server::start);
            serverThread.start();

            EventQueue.getInstance().addEvent(new Event(EventType.SERVER_STARTED, System.currentTimeMillis(), null));
            
            model.getServerMessagesRepo().create(new Message(MessageType.INFORMATION, "Server started on port <" + port + ">", System.currentTimeMillis()));
            
            EventQueue.getInstance().addEvent(new Event(EventType.SERVER_MESSAGE_RECEIVED, System.currentTimeMillis(), Map.of(Const.Event.ALL_MESSAGES_KEY, model.getServerMessagesRepo().getAll())));
        }
        catch (IOException e)
        {
            log.error("Error while starting server", e);
        }
    }

    public void stopServer()
    {
    	if(server != null && serverThread != null)
    	{
    		server.stop();
            serverThread.interrupt();
            EventQueue.getInstance().addEvent(new Event(EventType.SERVER_STOPPED, System.currentTimeMillis(), null));
            
            model.getServerMessagesRepo().create(new Message(MessageType.INFORMATION, "Server stopped", System.currentTimeMillis()));
            
            EventQueue.getInstance().addEvent(new Event(EventType.SERVER_MESSAGE_RECEIVED, System.currentTimeMillis(), Map.of(Const.Event.ALL_MESSAGES_KEY, model.getServerMessagesRepo().getAll())));
    	}
        
        server = null;
        serverThread = null;
    }

    public boolean isServerRunning()
    {
        return serverThread != null && serverThread.isAlive();
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
            case SERVER_MESSAGE_SENT ->
            {
                event.mustExist(Const.Event.MESSAGE_KEY);

                model.getConnectionService().sendServerMessage(new Message(MessageType.SENT, event.getData(Const.Event.MESSAGE_KEY), System.currentTimeMillis()));
            }
            default ->
            {
            }
        }
    }
}
