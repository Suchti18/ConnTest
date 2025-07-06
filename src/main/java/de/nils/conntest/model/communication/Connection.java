package de.nils.conntest.model.communication;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Connection
{
    private static final Logger log = LoggerFactory.getLogger(Connection.class);

    private final Socket socket;
    private final BlockingDeque<Message> messages;

    private volatile boolean connected;

    public Connection(Socket socket)
    {
        this.socket = socket;
        messages = new LinkedBlockingDeque<>();

        connected = true;

        new Thread(this::reader).start();
        new Thread(this::writer).start();

        EventQueue.getInstance().addEvent(new Event(EventType.CONNECTION_ESTABLISHED,  System.currentTimeMillis(), Map.of(Const.Event.CONNECTION_KEY, this)));
    }

    public void reader()
    {
        try(BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
                socket)
        {
            final byte[] buf = new byte[1024];

            while(connected)
            {
                int read = in.read(buf);

                if(read == -1)
                {
                    log.warn("Connection closed");
                    stop();
                    break;
                }

                Message message = new Message(MessageType.RECEIVED, new String(buf, 0, read), System.currentTimeMillis(), this, Arrays.copyOfRange(buf, 0, read));

                log.atTrace()
                    .setMessage("received <{}>")
                    .addArgument(() -> message.message())
                    .log();

                EventQueue.getInstance().addEvent(
                        new Event(EventType.CONNECTION_RECEIVED_MESSAGE,
                                System.currentTimeMillis(),
                                Map.of(Const.Event.MESSAGE_KEY, message)));
            }
        }
        catch (IOException e)
        {
            log.error("Error while reading from socket", e);
            
            EventQueue.getInstance().addEvent(new Event(EventType.CONNECTION_LOST,  System.currentTimeMillis(), Map.of(Const.Event.CONNECTION_KEY, this)));
        }
    }

    public void writer()
    {
        try(BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                socket)
        {
            while(connected)
            {
                Message message = messages.takeFirst();

                log.trace("Message <{}> will be sent out", message);

                out.write(message.message());
                out.flush();
                
                EventQueue.getInstance().addEvent(
                        new Event(EventType.CONNECTION_SENT_MESSAGE,
                                System.currentTimeMillis(),
                                Map.of(Const.Event.MESSAGE_KEY, message)));
            }
        }
        catch (InterruptedException | IOException e)
        {
            log.error("Error while writing to socket", e);
        }
    }

    public void stop()
    {
        connected = false;

        try(socket)
        {
        }
        catch (IOException e)
        {
            log.error("Error while stopping socket", e);
        }

        EventQueue.getInstance().addEvent(new Event(EventType.CONNECTION_LOST,  System.currentTimeMillis(), Map.of(Const.Event.CONNECTION_KEY, this)));
    }

    public void sendMessage(Message message)
    {
        messages.add(message);
    }

    public boolean isConnected()
    {
        return connected;
    }
    
    @Override
    public String toString()
    {
    	return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }
}
