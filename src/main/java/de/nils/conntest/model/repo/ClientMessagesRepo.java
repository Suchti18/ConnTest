package de.nils.conntest.model.repo;

import de.nils.conntest.model.communication.Message;

public class ClientMessagesRepo extends RepoBase<Long, Message>
{
    public ClientMessagesRepo()
    {
        super("ClientMessages");
    }

    @Override
    public Long extractKey(Message dao)
    {
        return dao.time();
    }
}
