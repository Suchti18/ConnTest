package de.nils.conntest.model.event;

import java.util.Map;

public record Event(String eventType, long time, Map<String, ?> data) implements Comparable<Event>
{
    @SuppressWarnings("unchecked")
    public <T> T getData(String key)
    {
        return (T) data.get(key);
    }

    @Override
    public int compareTo(Event o)
    {
        return Long.compare(time, o.time());
    }
}
