package de.nils.conntest.model.services;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.Model;
import de.nils.conntest.model.communication.Connection;
import de.nils.conntest.model.communication.Message;
import de.nils.conntest.model.communication.MessageType;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.event.EventType;

public class ClientService implements EventListener
{
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private Model model;
    private Socket clientSocket;
    private Connection clientConn;

    public ClientService(Model model)
    {
		this.model = model;
	}
    
    public void startClient(String address, int port)
    {
    	if(port <= 0 || port > Short.MAX_VALUE)
        {
        	EventQueue.getInstance().addEvent(
        			new Event(EventType.ERROR,
        					System.currentTimeMillis(),
        					Map.of(Const.Event.ERROR_TEXT, "Port is out of range")));
        	
        	return;
        }
    	
    	if(model.getServerService().isServerRunning())
    	{
    		EventQueue.getInstance().addEvent(
        			new Event(EventType.ERROR,
        					System.currentTimeMillis(),
        					Map.of(Const.Event.ERROR_TEXT, "Cant start client while a server is running")));
    		
    		return;
    	}
    	
    	try
        {
            if(clientSocket == null && clientConn == null)
            {
                clientSocket = new Socket(address, port);
                clientConn = new Connection(clientSocket);
                
                EventQueue.getInstance().addEvent(new Event(EventType.CLIENT_STARTED, System.currentTimeMillis(), Map.of()));
            }
        }
        catch (IOException e)
        {
            log.error("Error while starting client. Dest: <{}:{}>", address, port, e);
            
            if(e instanceof ConnectException)
			{
            	EventQueue.getInstance().addEvent(
            			new Event(EventType.ERROR,
            					System.currentTimeMillis(),
            					Map.of(Const.Event.ERROR_TEXT, "Connection refused: " + address + ":" + port)));
			}
            else if(e instanceof UnknownHostException)
            {
            	EventQueue.getInstance().addEvent(
            			new Event(EventType.ERROR,
            					System.currentTimeMillis(),
            					Map.of(Const.Event.ERROR_TEXT, "Unknown host: " + address)));
            }
        }
    }

    public void stopClient()
    {
    	if(clientSocket != null && clientConn != null)
        {
    		model.getClientMessagesRepo().create(new Message(MessageType.INFORMATION, "Disconnected from: <" + clientConn + ">", System.currentTimeMillis(), null, null));
            
            EventQueue.getInstance().addEvent(new Event(EventType.CLIENT_MESSAGE_RECEIVED, System.currentTimeMillis(), Map.of(Const.Event.ALL_MESSAGES_KEY, model.getClientMessagesRepo().getAll())));
    		
    		clientConn.stop();
        }
    	
    	clientSocket = null;
        clientConn = null;
        EventQueue.getInstance().addEvent(new Event(EventType.CLIENT_STOPPED, System.currentTimeMillis(), Map.of()));
    }

    public boolean isClientRunning()
    {
        return clientSocket != null && clientConn != null;
    }

    @Override
    public void handleEvent(Event event)
    {
        switch(event.eventType())
        {
            case START_CLIENT ->
            {
                event.mustExist(Const.Event.CLIENT_ADDRESS_KEY);
                event.mustExist(Const.Event.CLIENT_PORT_KEY);

                try
                {
                	startClient(event.getData(Const.Event.CLIENT_ADDRESS_KEY),
                			Integer.parseInt(event.getData(Const.Event.CLIENT_PORT_KEY)));
                }
                catch(NumberFormatException e)
                {
                	EventQueue.getInstance().addEvent(
                			new Event(EventType.ERROR,
                					System.currentTimeMillis(),
                					Map.of(Const.Event.ERROR_TEXT, "Port is not parsable")));
                }
            }
            case STOP_CLIENT ->
            {
                stopClient();
            }
            case CLIENT_MESSAGE_SENT ->
            {
            	event.mustExist(Const.Event.MESSAGE_KEY);

                model.getConnectionService().sendClientMessage(new Message(MessageType.SENT, event.getData(Const.Event.MESSAGE_KEY), System.currentTimeMillis(), clientConn, ((String) event.getData(Const.Event.MESSAGE_KEY)).getBytes()));
            }
            default ->
            {
            }
        }
    }
}
