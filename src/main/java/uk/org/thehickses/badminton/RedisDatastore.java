package uk.org.thehickses.badminton;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.JedisPool;

public class RedisDatastore
{
    private final JedisPool pool;

    private static JedisPool pool(String uri)
    {
        var u = URI.create(uri);
        return new JedisPool(u.getHost(), u.getPort());
    }

    public RedisDatastore(String uri)
    {
        this(pool(uri));
    }

    public RedisDatastore(JedisPool pool)
    {
        this.pool = pool;
    }

    public void add(String... players)
    {
        try (var t = pool.getResource()
                .multi())
        {
            Stream.of(players)
                    .forEach(n -> t.rpush("players", n));
            t.exec();
        }
    }

    public Stream<String> getPlayers()
    {
        List<String> resp;
        try (var j = pool.getResource())
        {
            resp = j.lrange("players", 0, -1);
        }
        return resp.stream();
    }

    public void clearPlayers()
    {
        try (var j = pool.getResource())
        {
            j.lpop("players", Integer.MAX_VALUE);
        }
    }

    public void upsert(Session s)
    {
        try (var j = pool.getResource())
        {
            j.hset("sessions", s.getDateString(), new ObjectMapper().writeValueAsString(s));
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Session getSession(LocalDate d)
    {
        String json;
        try (var j = pool.getResource())
        {
            json = j.hget("sessions", Session.dateString(d));
        }
        if (json == null)
            return null;
        try
        {
            return new ObjectMapper().readValue(json, Session.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void clearSessions()
    {
        try (var j = pool.getResource())
        {
            j.del("sessions");
        }
    }
}
