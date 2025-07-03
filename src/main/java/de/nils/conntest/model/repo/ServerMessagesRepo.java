package de.nils.conntest.model.repo;

import de.nils.conntest.model.communication.Message;

public class ServerMessagesRepo extends RepoBase<Integer, Message>
{
    public ServerMessagesRepo()
    {
        super("ServerMessages");
    }

    @Override
    public Integer extractKey(Message dao)
    {
        return dao.id();
    }
}
