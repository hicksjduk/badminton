package uk.org.thehickses.badminton;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
        return process(date, req, Action.Init);
    }

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public String homePost(HttpServletRequest req) throws Exception
    {
        var date = Session.parseDate(req.getParameter("date"));
        var action = Action.valueOf(req.getParameter("action"));
        return process(date, req, action);
    }

    private String process(LocalDate date, HttpServletRequest req, Action action) throws Exception
    {
        var session = Optional.ofNullable(datastore.getSession(date))
                .orElse(new Session(date));
        action.process(req, session);
        datastore.upsert(session);
        return templater.applyTemplate("home.ftlh", session);
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
        var round = session.getRound() + 1;
        session.setRound(round);
        session.getPairings(round);
    }

    private static enum Action
    {
        Init(),
        Select(),
        Save(Controller::playersProcessor),
        Next(Controller::playersProcessor, Controller::nextProcessor);

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
}