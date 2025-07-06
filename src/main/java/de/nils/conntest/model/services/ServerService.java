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
import java.net.BindException;
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
        if(port <= 0 || port > Short.MAX_VALUE)
        {
        	EventQueue.getInstance().addEvent(
        			new Event(EventType.ERROR,
        					System.currentTimeMillis(),
        					Map.of(Const.Event.ERROR_TEXT, "Port is out of range")));
        	
        	return;
        }
    	
    	if((serverThread != null && serverThread.isAlive()) || model.getClientService().isClientRunning())
        {
    		EventQueue.getInstance().addEvent(
        			new Event(EventType.ERROR,
        					System.currentTimeMillis(),
        					Map.of(Const.Event.ERROR_TEXT, "If you have a client or server up and running stop it before starting a new one")));
    		
    		return;
        }

        try
        {
            server = new Server(port);
            serverThread = new Thread(server::start);
            serverThread.start();

            EventQueue.getInstance().addEvent(new Event(EventType.SERVER_STARTED, System.currentTimeMillis(), null));
            
            model.getServerMessagesRepo().create(new Message(MessageType.INFORMATION, "Server started on port <" + port + ">", System.currentTimeMillis(), null, null));
            
            EventQueue.getInstance().addEvent(new Event(EventType.SERVER_MESSAGE_RECEIVED, System.currentTimeMillis(), Map.of(Const.Event.ALL_MESSAGES_KEY, model.getServerMessagesRepo().getAll())));
        }
        catch (IOException e)
        {
            log.error("Error while starting server", e);
            
            if(e instanceof BindException)
			{
            	EventQueue.getInstance().addEvent(
            			new Event(EventType.ERROR,
            					System.currentTimeMillis(),
            					Map.of(Const.Event.ERROR_TEXT, "Port is already in use")));
			}
        }
    }

    public void stopServer()
    {
    	if(server != null && serverThread != null)
    	{
    		server.stop();
            serverThread.interrupt();
            EventQueue.getInstance().addEvent(new Event(EventType.SERVER_STOPPED, System.currentTimeMillis(), null));
            
            model.getServerMessagesRepo().create(new Message(MessageType.INFORMATION, "Server stopped", System.currentTimeMillis(), null, null));
            
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
                
                try
                {
                	startServer(Integer.parseInt(event.getData(Const.Event.SERVER_PORT_KEY)));
                }
                catch(NumberFormatException e)
                {
                	EventQueue.getInstance().addEvent(
                			new Event(EventType.ERROR,
                					System.currentTimeMillis(),
                					Map.of(Const.Event.ERROR_TEXT, "Port is not parsable")));
                }
            }
            case STOP_SERVER ->
            {
                stopServer();
            }
            case SERVER_MESSAGE_SENT ->
            {
                event.mustExist(Const.Event.MESSAGE_KEY);

                model.getConnectionService().sendServerMessage(
                		new Message(MessageType.SENT,
                				event.getData(Const.Event.MESSAGE_KEY),
                				System.currentTimeMillis(), null, null));
            }
            default ->
            {
            }
        }
    }
}
