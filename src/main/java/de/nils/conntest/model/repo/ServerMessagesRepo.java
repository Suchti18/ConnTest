package de.nils.conntest.model.repo;

import de.nils.conntest.model.communication.Message;

public class ServerMessagesRepo extends RepoBase<Long, Message>
{
    public ServerMessagesRepo()
    {
        super("ServerMessages");
    }

    @Override
    public Long extractKey(Message dao)
    {
        return dao.time();
    }
}
