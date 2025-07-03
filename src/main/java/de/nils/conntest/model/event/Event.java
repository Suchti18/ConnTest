package de.nils.conntest.model.event;

import java.util.Map;

public record Event(EventType eventType, long time, Map<String, ?> data) implements Comparable<Event>
{
    @SuppressWarnings("unchecked")
    public <T> T getData(String key)
    {
        return (T) data.get(key);
    }

    public void mustExist(String key)
    {
        if(!data.containsKey(key))
        {
            throw new IllegalArgumentException("Key <" + key + "> does not exist in event: " + this);
        }
    }

    @Override
    public int compareTo(Event o)
    {
        return Long.compare(time, o.time());
    }
}
