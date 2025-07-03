package de.nils.conntest.model.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class EventQueue
{
    private static final Logger log = LoggerFactory.getLogger(EventQueue.class);
    private static final EventQueue instance = new EventQueue();

    private final Queue<Event> events;
    private final List<EventListener> listeners;

    public static EventQueue getInstance()
    {
        return instance;
    }

    private EventQueue()
    {
        events = new PriorityBlockingQueue<>();
        listeners = new ArrayList<>();
    }

    public void addEvent(Event event)
    {
        log.trace("Adding event <{}> to EventQueue", event);

        synchronized(events)
        {
            events.notify();
            events.add(event);
        }
    }

    public void startEventQueue()
    {
        Event event;

        while(true)
        {
            try
            {
                synchronized(events)
                {
                    if(events.isEmpty())
                    {
                        events.wait();
                    }
                    else
                    {
                        event = events.peek();

                        if(event.time() <= System.currentTimeMillis())
                        {
                            event = events.poll();

                            for(EventListener listener : listeners)
                            {
                                try
                                {
                                    listener.handleEvent(event);
                                }
                                catch(RuntimeException e)
                                {
                                    log.error("Error while running event <{}> to listener <{}>", event, listener, e);
                                }
                            }
                        }
                        else
                        {
                            long delta = event.time() - System.currentTimeMillis();
                            events.wait(delta);
                        }
                    }
                }
            }
            catch(InterruptedException e)
            {
                log.error("Error while running event queue", e);
                Thread.interrupted();
            }
        }
    }

    public void addListener(EventListener eventListener)
    {
        listeners.add(eventListener);
    }
}
