package de.nils.conntest.model.communication;

import java.net.Socket;

public class Connection
{
    private final Socket socket;

    public Connection(Socket socket)
    {
        this.socket = socket;
    }
}
