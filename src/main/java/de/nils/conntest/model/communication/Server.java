package de.nils.conntest.model.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final ServerSocket serverSocket;

    public Server(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
    }

    public void start()
    {
        try
        {
            log.info("Server started on port {}", serverSocket.getLocalPort());

            while(!serverSocket.isClosed())
            {
                Socket newSocket = serverSocket.accept();

                log.info("New connection from {}", newSocket.getRemoteSocketAddress());

                new Connection(newSocket);
            }
        }
        catch (IOException e)
        {
            log.error("Error accepting connection", e);
        }
    }

    public void stop()
    {
        try(serverSocket)
        {
        }
        catch (IOException e)
        {
            log.error("Error while stopping server", e);
        }
    }
}
