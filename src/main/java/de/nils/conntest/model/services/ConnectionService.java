package de.nils.conntest.model.services;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.Model;
import de.nils.conntest.model.communication.Message;
import de.nils.conntest.model.communication.MessageType;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.event.EventType;

import java.util.Map;

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
        switch(event.eventType())
        {
            case CONNECTION_ESTABLISHED ->
            {

            }
            case CONNECTION_LOST ->
            {

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

                }
            }
            case CONNECTION_SENT_MESSAGE ->
            {

            }
        }
    }
}
