package uk.org.thehickses.badminton;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller
{
    @Autowired
    Templater templater;

    @Autowired
    RedisDatastore datastore;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String homeGet(HttpServletRequest req) throws Exception
    {
        var date = LocalDate.now();
        return process(date, req, Action.INIT);
    }

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public String homePost(HttpServletRequest req) throws Exception
    {
        LocalDate date;
        try
        {
            date = LocalDate.parse(req.getParameter("date"));
        }
        catch (Exception ex)
        {
            date = LocalDate.now();
        }
        var action = Action.valueOf(req.getParameter("action")
                .toUpperCase());
        return process(date, req, action);
    }

    private String process(LocalDate date, HttpServletRequest req, Action action) throws Exception
    {
        var session = Optional.ofNullable(datastore.getSession(date))
                .orElse(new Session(date));
        action.process(req, session);
        datastore.upsert(session);
        var players = datastore.getPlayers()
                .sorted()
                .toList();
        return templater.applyTemplate("home.ftlh", new HomePage(session, players));
    }

    private static void playersProcessor(HttpServletRequest req, Session session)
    {
        session.setPlayers(Stream.of(req.getParameter("players")
                .split("(?m)\\s+"))
                .filter(StringUtils::isNotBlank)
                .distinct()
                .toList());
        session.getPairings(session.getRound());
    }

    private static void nextProcessor(HttpServletRequest req, Session session)
    {
        changeRound(session, 1);
    }

    private static void prevProcessor(HttpServletRequest req, Session session)
    {
        changeRound(session, -1);
    }

    private static void changeRound(Session session, int increment)
    {
        var round = session.getRound() + increment;
        if (round < 0)
            return;
        session.setRound(round);
        session.getPairings(round);
    }

    private static enum Action
    {
        INIT(),
        DATE(),
        SAVE(Controller::playersProcessor),
        NEXT(Controller::playersProcessor, Controller::nextProcessor),
        PREV(Controller::playersProcessor, Controller::prevProcessor);

        BiConsumer<HttpServletRequest, Session>[] processors;

        @SafeVarargs
        private Action(BiConsumer<HttpServletRequest, Session>... processors)
        {
            this.processors = processors;
        }

        public void process(HttpServletRequest req, Session session)
        {
            Stream.of(processors)
                    .forEach(p -> p.accept(req, session));
        }
    }

    @RequestMapping(path = "/players", method = RequestMethod.GET)
    public String getPlayerList(HttpServletRequest req, HttpServletResponse res) throws Exception
    {
        return datastore.getPlayers()
                .collect(Collectors.joining(", "));
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public String updatePlayerList(HttpServletRequest req, HttpServletResponse res) throws Exception
    {
        if (!Objects.equals(System.getenv("ADMIN_SECRET"), req.getHeader("as")))
        {
            res.setStatus(HttpStatus.BAD_REQUEST.value());
            return "";
        }
        var players = Stream.of(req.getReader()
                .lines()
                .collect(Collectors.joining())
                .split(","))
                .filter(StringUtils::isNotEmpty)
                .map(String::trim)
                .map(s -> s.replaceAll("\\s\\s+", " "))
                .distinct()
                .toArray(String[]::new);
        datastore.replacePlayers(players);
        return "Player list replaced with %d player(s)".formatted(players.length);
    }
}