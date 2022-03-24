package uk.org.thehickses.badminton;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class SessionRoundtripTest
{

    @Test
    void test() throws Exception
    {
        var session = new Session(LocalDate.of(2019, 12, 9),
                players("Jeremy, Pete, Nigel, Katie, Teresa"));
        session.getPairings(4);
        var mapper = new ObjectMapper();
        var json = mapper.writeValueAsString(session);
        var inputSession = mapper.readValue(json, Session.class);
        assertThat(inputSession.getDate()).isEqualTo(session.getDate());
        assertThat(inputSession.getPlayers()).containsExactlyElementsOf(session.getPlayers());
        assertThat(inputSession.getPairings()).containsExactlyElementsOf(session.getPairings());
    }

    private List<Player> players(String names)
    {
        return Stream.of(names.split("\\s*,\\s*"))
                .map(Player::new)
                .toList();
    }

}
