package uk.org.thehickses.badminton;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

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

    public void add(Player... players)
    {
        try (var t = pool.getResource()
                .multi())
        {
            Stream.of(players)
                    .map(Player::getName)
                    .forEach(n -> t.rpush("players", n));
            t.exec();
        }
    }

    public Stream<Player> getPlayers()
    {
        List<String> resp;
        try (var j = pool.getResource())
        {
            resp = j.lrange("players", 0, -1);
        }
        return resp.stream()
                .map(Player::new);
    }

    public void clearPlayers()
    {
        try (var j = pool.getResource())
        {
            j.lpop("players", Integer.MAX_VALUE);
        }
    }
}
