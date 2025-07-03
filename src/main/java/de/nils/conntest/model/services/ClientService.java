package de.nils.conntest.model.services;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;

public class ClientService implements EventListener
{
    public void startClient(String address, int port)
    {

    }

    public void stopClient()
    {

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
        }
    }
}
