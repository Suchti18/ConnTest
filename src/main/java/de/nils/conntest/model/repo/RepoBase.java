package de.nils.conntest.model.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class RepoBase<K, V>
{
    private static final Logger log = LoggerFactory.getLogger(RepoBase.class);

    private final String repoName;
    private final Map<K, V> daos;

    public RepoBase(String repoName)
    {
        this.repoName = repoName;
        daos = new HashMap<>();

        log.trace("Repository<{}> created", repoName);
    }

    public void create(V dao)
    {
        if(daos.containsKey(extractKey(dao)))
        {
            throw new IllegalArgumentException(extractKey(dao) + " already exists in repository<" + repoName + ">");
        }

        Objects.requireNonNull(extractKey(dao), "key cannot be null");

        daos.put(extractKey(dao), dao);
        log.trace("Created <{}> with key <{}> in repository <{}>", dao, extractKey(dao), repoName);
    }

    public V get(K key)
    {
        return daos.getOrDefault(key, null);
    }

    public void update(V dao)
    {
        if(!daos.containsKey(extractKey(dao)))
        {
            throw new IllegalArgumentException(dao + " cannot be updated in repository<" + repoName + ">");
        }

        daos.put(extractKey(dao), dao);
    }

    public void delete(V dao)
    {
        if(!daos.containsKey(extractKey(dao)))
        {
            throw new IllegalArgumentException(dao + " cannot be deleted in repository<" + repoName + ">");
        }

        daos.remove(extractKey(dao));
    }

    public List<V> getAll()
    {
        return new ArrayList<>(daos.values());
    }

    public abstract K extractKey(V dao);
}
