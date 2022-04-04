package uk.org.thehickses.badminton;

import java.time.LocalDate;
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
        var session = datastore.getSession(date);
        if (session == null)
            datastore.upsert(session = new Session(date));
        return templater.applyTemplate("home.ftlh", session);
    }

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public String homePost(HttpServletRequest req) throws Exception
    {
        var date = Session.parseDate(req.getParameter("date"));
        var session = datastore.getSession(date);
        if (session == null)
            session = new Session(date);
        var players = Stream.of(req.getParameter("players")
                .split("(?m)\\s+"))
                .filter(StringUtils::isNotBlank)
                .distinct()
                .toList();
        session.setPlayers(players);
        if (req.getParameter("action")
                .equals("Next"))
            session.setRound(session.getRound() + 1);
        datastore.upsert(session);
        var answer = templater.applyTemplate("home.ftlh", session);
        return answer;
    }

}