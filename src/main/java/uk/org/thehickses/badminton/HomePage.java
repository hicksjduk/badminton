package uk.org.thehickses.badminton;

import java.util.List;

public class HomePage
{
    private final Session session;
    private final List<String> players;

    public HomePage(Session session, List<String> players)
    {
        this.session = session;
        this.players = players;
    }

    public Session getSession()
    {
        return session;
    }

    public List<String> getPlayers()
    {
        return players;
    }
}
