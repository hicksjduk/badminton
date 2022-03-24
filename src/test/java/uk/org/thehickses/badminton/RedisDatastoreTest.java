package uk.org.thehickses.badminton;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RedisDatastoreTest
{
    private static RedisDatastore datastore;

    @BeforeAll
    static void init()
    {
        datastore = new RedisDatastore("redis://localhost:6378");
        datastore.clearPlayers();
    }

    @Test
    void testPlayersAdd()
    {
        datastore.add(players("Jeremy, Pete, Nigel").toArray(String[]::new));
        assertThat(datastore.getPlayers()).containsExactly("Jeremy", "Pete", "Nigel");
        datastore.add(players("Mike, Denise").toArray(String[]::new));
        assertThat(datastore.getPlayers()).containsExactly("Jeremy", "Pete", "Nigel", "Mike", "Denise");
    }

    private Stream<String> players(String names)
    {
        return Stream.of(names.split("\\s*,\\s*"));
    }

}
