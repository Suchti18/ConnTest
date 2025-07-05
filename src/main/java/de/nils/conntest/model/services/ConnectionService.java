package de.nils.conntest.model.services;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.Model;
import de.nils.conntest.model.communication.Connection;
import de.nils.conntest.model.communication.Message;
import de.nils.conntest.model.communication.MessageType;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionService implements EventListener
{
    private static final Logger log = LoggerFactory.getLogger(ConnectionService.class);

    private final Model model;
    private final List<Connection> connectionList;

    public ConnectionService(Model model)
    {
        this.model = model;
        connectionList = new CopyOnWriteArrayList<>();
    }

    public void sendServerMessage(Message message)
    {
        for(Connection connection : connectionList)
        {
            log.trace("Send message <{}>", message);

            connection.sendMessage(message);
        }
    }
    
    public void sendClientMessage(Message message)
    {
    	connectionList.getFirst().sendMessage(message);
    }

    @Override
    public void handleEvent(Event event)
    {
        switch(event.eventType())
        {
            case CONNECTION_ESTABLISHED ->
            {
                event.mustExist(Const.Event.CONNECTION_KEY);

                connectionList.add(event.getData(Const.Event.CONNECTION_KEY));
            }
            case CONNECTION_LOST ->
            {
                event.mustExist(Const.Event.CONNECTION_KEY);

                connectionList.remove(event.getData(Const.Event.CONNECTION_KEY));
                
                if(model.getClientService().isClientRunning())
                {
                	model.getClientService().stopClient();
                }
            }
            case CONNECTION_RECEIVED_MESSAGE ->
            {
                event.mustExist(Const.Event.MESSAGE_KEY);

                if(model.getServerService().isServerRunning())
                {
                    model.getServerMessagesRepo().create(
                            new Message(MessageType.RECEIVED,
                                    event.getData(Const.Event.MESSAGE_KEY),
                                    System.currentTimeMillis()));

                    EventQueue.getInstance().addEvent(
                            new Event(EventType.SERVER_MESSAGE_RECEIVED,
                                    System.currentTimeMillis(),
                                    Map.of(Const.Event.ALL_MESSAGES_KEY, model.getServerMessagesRepo().getAll())));
                }
                else
                {
                	model.getClientMessagesRepo().create(
                            new Message(MessageType.RECEIVED,
                                    event.getData(Const.Event.MESSAGE_KEY),
                                    System.currentTimeMillis()));

                    EventQueue.getInstance().addEvent(
                            new Event(EventType.CLIENT_MESSAGE_RECEIVED,
                                    System.currentTimeMillis(),
                                    Map.of(Const.Event.ALL_MESSAGES_KEY, model.getServerMessagesRepo().getAll())));
                }
            }
            case CONNECTION_SENT_MESSAGE ->
            {
            	event.mustExist(Const.Event.MESSAGE_KEY);
            	
            	if(model.getServerService().isServerRunning())
            	{
            		model.getServerMessagesRepo().create(event.getData(Const.Event.MESSAGE_KEY));

                    EventQueue.getInstance().addEvent(
                            new Event(EventType.SERVER_MESSAGE_RECEIVED,
                                    System.currentTimeMillis(),
                                    Map.of(Const.Event.ALL_MESSAGES_KEY, model.getServerMessagesRepo().getAll())));
            	}
            	else
            	{
            		model.getClientMessagesRepo().create(event.getData(Const.Event.MESSAGE_KEY));

                    EventQueue.getInstance().addEvent(
                            new Event(EventType.CLIENT_MESSAGE_RECEIVED,
                                    System.currentTimeMillis(),
                                    Map.of(Const.Event.ALL_MESSAGES_KEY, model.getClientMessagesRepo().getAll())));
            	}
            }
            case SERVER_STOPPED ->
            {
            	for(Connection conn : connectionList)
            	{
            		conn.stop();
            	}
            	
            	connectionList.clear();
            }
            case CLIENT_STOPPED ->
            {
            	for(Connection conn : connectionList)
            	{
            		conn.stop();
            	}
            	
            	connectionList.clear();
            }
            default ->
            {
            }
        }
    }
}
