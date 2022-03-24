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
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Session
{
    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy");

    private LocalDate date;
    private final List<String> players = new ArrayList<>();
    private final List<List<Pair<String, String>>> pairings = new ArrayList<>();

    Session()
    {

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
            IntStream.range(Math.max(1, currentCount), pairings.size())
                    .forEach(i -> Collections.sort(pairings.get(i), posComparator(i)));
        }
        return pairings.get(round);
    }

    private Comparator<Pair<String, String>> posComparator(int roundNo)
    {
        var round = pairings.get(roundNo - 1);
        var positions = IntStream.range(0, round.size())
                .mapToObj(i -> Pair.of(i, round.get(i)))
                .flatMap(this::StringPositions)
                .collect(toMap(Pair::getRight, Pair::getLeft));
        return Comparator.comparingInt(positionGetter(positions))
                .reversed();
    }

    private Stream<Pair<Integer, String>> StringPositions(
            Pair<Integer, Pair<String, String>> pairPositions)
    {
        var pos = pairPositions.getLeft();
        var String1 = pairPositions.getRight()
                .getLeft();
        var String2 = pairPositions.getRight()
                .getRight();
        return Stream.of(Pair.of(pos, String1), Pair.of(pos, String2));
    }

    private ToIntFunction<Pair<String, String>> positionGetter(Map<String, Integer> pos)
    {
        return pair ->
            {
                var pos1 = pos.getOrDefault(pair.getLeft(), 0);
                var pos2 = pos.getOrDefault(pair.getRight(), 0);
                if (pos1 < pos2)
                    return pos2 * 100 + pos1;
                return pos1 * 100 + pos2;
            };
    }

    public LocalDate getDate()
    {
        return date;
    }

    @JsonProperty("date")
    public String getDateString()
    {
        return date.format(dateFormatter);
    }

    @JsonProperty("date")
    public void setDateString(String str)
    {
        date = LocalDate.parse(str, dateFormatter);
    }

    public List<String> getPlayers()
    {
        return players;
    }

    @JsonProperty("players")
    public void setPlayers(List<String> names)
    {
        players.addAll(names.stream()
                .toList());
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
                        .map(p -> List.of(p.getLeft(), p.getRight()))
                        .toList())
                .toList();
    }

    @JsonProperty("pairings")
    public void setPairingsLists(List<List<List<String>>> pLists)
    {
        pairings.addAll(pLists.stream()
                .map(r -> r.stream()
                        .map(ArrayDeque::new)
                        .map(p -> Pair.of(new String(p.pop()), new String(p.pop())))
                        .toList())
                .toList());
    }
}
