package de.nils.conntest.model.services;

import java.io.IOException;
import java.net.Socket;
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
        try
        {
            if(clientSocket == null && clientConn == null && !model.getServerService().isServerRunning())
            {
                clientSocket = new Socket(address, port);
                clientConn = new Connection(clientSocket);
                
                EventQueue.getInstance().addEvent(new Event(EventType.CLIENT_STARTED, System.currentTimeMillis(), Map.of()));
            }
        }
        catch (IOException e)
        {
            log.error("Error while starting client. Dest: <{}:{}>", address, port, e);
        }
    }

    public void stopClient()
    {
    	if(clientSocket != null && clientConn != null)
        {
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

                startClient(event.getData(Const.Event.CLIENT_ADDRESS_KEY),
                        event.getData(Const.Event.CLIENT_PORT_KEY));
            }
            case STOP_CLIENT ->
            {
                stopClient();
            }
            case CLIENT_MESSAGE_SENT ->
            {
            	event.mustExist(Const.Event.MESSAGE_KEY);

                model.getConnectionService().sendClientMessage(new Message(MessageType.SENT, event.getData(Const.Event.MESSAGE_KEY), System.currentTimeMillis()));
            }
            default ->
            {
            }
        }
    }
}
