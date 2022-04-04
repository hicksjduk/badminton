package uk.org.thehickses.badminton;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Session
{
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy");

    private LocalDate date;
    private final List<String> players = new ArrayList<>();
    private final List<List<Pair<String, String>>> pairings = new ArrayList<>();
    private int round = -1;

    public static String formatDate(LocalDate d)
    {
        return d.format(dateFormatter);
    }

    public static LocalDate parseDate(String str)
    {
        return LocalDate.parse(str, dateFormatter);
    }

    Session()
    {
    }

    public Session(LocalDate date)
    {
        this(date, List.of());
    }

    public Session(LocalDate date, List<String> Strings)
    {
        this.date = date;
        this.players.addAll(Strings);
    }

    public List<Pair<String, String>> getPairings(int round)
    {
        var currentCount = pairings.size();
        var roundsNeeded = round - currentCount + 1;
        if (roundsNeeded > 0)
        {
            var pairs = Combiner.combine(players.stream())
                    .skip(currentCount)
                    .limit(roundsNeeded)
                    .map(r -> r.collect(toList()))
                    .peek(Collections::shuffle)
                    .toList();
            pairings.addAll(pairs);
            IntStream.range(Math.max(0, currentCount), pairings.size())
                    .forEach(i -> Collections.sort(pairings.get(i), posComparator(i)));
        }
        return pairings.get(round);
    }

    private Comparator<Pair<String, String>> posComparator(int roundNo)
    {
        Comparator<Pair<String, String>> singletonLast = Comparator.comparing(Pair::getRight,
                Comparator.comparing(Objects::isNull));
        if (roundNo == 0)
            return singletonLast;
        var round = pairings.get(roundNo - 1);
        var positions = IntStream.range(0, round.size())
                .mapToObj(i -> Pair.of(i, round.get(i)))
                .flatMap(this::StringPositions)
                .collect(toMap(Pair::getRight, Pair::getLeft));
        return singletonLast.thenComparing(Comparator.comparingInt(positionGetter(positions))
                .reversed());
    }

    private Stream<Pair<Integer, String>> StringPositions(
            Pair<Integer, Pair<String, String>> pairPositions)
    {
        var pos = pairPositions.getLeft();
        var s1 = pairPositions.getRight()
                .getLeft();
        var s2 = pairPositions.getRight()
                .getRight();
        return Stream.of(Pair.of(pos, s1), Pair.of(pos, s2));
    }

    private ToIntFunction<Pair<String, String>> positionGetter(Map<String, Integer> pos)
    {
        return pair ->
            {
                var pos1 = pos.getOrDefault(pair.getLeft(), 99);
                var pos2 = pos.getOrDefault(pair.getRight(), 99);
                if (pos1 < pos2)
                    return pos2 * 1000 + pos1;
                return pos1 * 1000 + pos2;
            };
    }

    public LocalDate getDate()
    {
        return date;
    }

    @JsonProperty("date")
    public String getDateString()
    {
        return formatDate(date);
    }

    @JsonProperty("date")
    public void setDateString(String str)
    {
        date = parseDate(str);
    }

    public List<String> getPlayers()
    {
        return players;
    }

    @JsonProperty("players")
    public void setPlayers(List<String> names)
    {
        players.clear();
        players.addAll(names);
    }

    public List<List<Pair<String, String>>> getPairings()
    {
        return pairings;
    }

    @JsonProperty("pairings")
    public List<List<List<String>>> getPairingsLists()
    {
        return pairings.stream()
                .map(r -> r.stream()
                        .map(p -> Stream.of(p.getLeft(), p.getRight())
                                .filter(Objects::nonNull)
                                .toList())
                        .toList())
                .toList();
    }

    @JsonProperty("pairings")
    public void setPairingsLists(List<List<List<String>>> pLists)
    {
        pairings.addAll(pLists.stream()
                .map(r -> r.stream()
                        .map(ArrayDeque::new)
                        .map(p -> Pair.of(p.pop(), p.poll()))
                        .toList())
                .toList());
    }
    
    public void setPairingStrings(List<String> ps) {}

    @JsonIgnore
    public List<String> getPairingStrings()
    {
        var answer = new ArrayList<String>();
        if (round < 0)
            return answer;
        var players = getPairings(round).stream()
                .flatMap(p -> Stream.of(p.getLeft(), p.getRight()))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayDeque::new));
        while (players.size() >= 4)
            answer.add("%s & %s  v  %s & %s".formatted(Stream.generate(players::pop)
                    .limit(4)
                    .toArray()));
        if (!players.isEmpty())
            answer.add(players.stream()
                    .collect(Collectors.joining(", ")));
        return answer;
    }

    public int getRound()
    {
        return round;
    }

    public void setRound(int round)
    {
        this.round = round;
    }
}
