package de.nils.conntest.model;

import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.services.ClientService;
import de.nils.conntest.model.services.ConnectionService;
import de.nils.conntest.model.services.ServerService;

public class Model
{
    private final ServerService serverService;
    private final ClientService clientService;
    private final ConnectionService connectionService;

    public Model()
    {
        serverService = new ServerService();
        clientService = new ClientService();
        connectionService = new ConnectionService(this);

        EventQueue.getInstance().addListener(serverService);
        EventQueue.getInstance().addListener(clientService);
        EventQueue.getInstance().addListener(connectionService);
    }

    public ServerService getServerService()
    {
        return serverService;
    }

    public ClientService getClientService()
    {
        return clientService;
    }

    public ConnectionService getConnectionService()
    {
        return connectionService;
    }
}
