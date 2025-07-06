package de.nils.conntest.model.communication;

public record Message(MessageType messageType, String message, long time, Connection source, byte[] rawData) implements Comparable<Message>
{
    @Override
    public int compareTo(Message o)
    {
        return Long.compare(time, o.time());
    }
}
