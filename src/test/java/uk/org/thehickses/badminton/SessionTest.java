package uk.org.thehickses.badminton;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SessionTest
{
    private static final Logger LOG = LoggerFactory.getLogger(SessionTest.class);

    @Test
    void testGetRounds()
    {
        var players = names(
                "Jeremy, Nigel, Teresa, Hannah, Helen, Katie, Kimberley, Denise, Mike, Sophie")
                        .toList();
        var session = new Session(LocalDate.of(2022, 3, 16), players);
        checkPairings(session, 0,
                "Nigel, Mike, Teresa, Denise, Hannah, Kimberley, Helen, Katie, Jeremy, Sophie");
        checkPairings(session, 1,
                "Jeremy, Denise, Nigel, Kimberley, Teresa, Katie, Hannah, Helen, Mike, Sophie");
        checkPairings(session, 2,
                "Mike, Kimberley, Jeremy, Katie, Nigel, Helen, Teresa, Hannah, Denise, Sophie");
    }

    void checkPairings(Session session, int round, String expectedPairs)
    {
        var expected = names(expectedPairs).collect(toCollection(LinkedList::new));
        var pairings = session.getPairings(round);
        pairings.stream()
                .peek(p -> LOG.debug("{}", p))
                .sorted(Comparator.comparingInt(p -> expected.indexOf(p.getLeft())))
                .flatMap(p -> Stream.of(p.getLeft(), p.getRight()))
                .forEach(n -> assertThat(n).isEqualTo(expected.pop()));
        LOG.debug("");
    }

    static Stream<String> names(String names)
    {
        return Stream.of(names.split("[,\\s]+"));
    }
}
