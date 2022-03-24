package uk.org.thehickses.badminton;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.stream.IntStream;
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
        datastore.clearSessions();
    }

    @Test
    void testPlayersAdd()
    {
        datastore.add(players("Jeremy, Pete, Nigel").toArray(String[]::new));
        assertThat(datastore.getPlayers()).containsExactly("Jeremy", "Pete", "Nigel");
        datastore.add(players("Mike, Denise").toArray(String[]::new));
        assertThat(datastore.getPlayers()).containsExactly("Jeremy", "Pete", "Nigel", "Mike",
                "Denise");
    }

    private Stream<String> players(String names)
    {
        return Stream.of(names.split("\\s*,\\s*"));
    }

    @Test
    void testSessionUpsertAndRead()
    {
        LocalDate date = LocalDate.of(2019, 12, 9);
        assertThat(datastore.getSession(date)).isNull();
        String names = "Nigel, Pete, Jeremy, Kimberley, Mike, Denise, Mark, Dan";
        Session session = new Session(date, players(names).toList());
        IntStream.of(3, 8)
                .forEach(i ->
                    {
                        session.getPairings(i);
                        datastore.upsert(session);
                        var retrieved = datastore.getSession(date);
                        assertThat(session.getDate()).isEqualTo(retrieved.getDate());
                        assertThat(session.getPlayers())
                                .containsExactlyElementsOf(retrieved.getPlayers());
                        assertThat(session.getPairings())
                                .containsExactlyElementsOf(retrieved.getPairings());
                    });
    }
}
