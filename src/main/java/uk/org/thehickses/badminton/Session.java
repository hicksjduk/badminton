package uk.org.thehickses.badminton;

import static java.util.stream.Collectors.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

public class Session
{
    private final LocalDate date;
    private final List<Player> players;
    private final List<List<Pair<Player, Player>>> pairings = new ArrayList<>();

    public Session(LocalDate date, List<Player> players)
    {
        this.date = date;
        this.players = new ArrayList<>(players);
    }

    public List<Pair<Player, Player>> getPairings(int round)
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
                    .collect(toList());
            pairings.addAll(pairs);
            IntStream.range(Math.max(1, currentCount), pairings.size())
                    .forEach(i -> Collections.sort(pairings.get(i), posComparator(i)));
        }
        return pairings.get(round);
    }

    private Comparator<Pair<Player, Player>> posComparator(int roundNo)
    {
        var round = pairings.get(roundNo - 1);
        var positions = IntStream.range(0, round.size())
                .mapToObj(i -> Pair.of(i, round.get(i)))
                .flatMap(this::playerPositions)
                .collect(toMap(Pair::getRight, Pair::getLeft));
        return Comparator.comparingInt(positionGetter(positions))
                .reversed();
    }

    private Stream<Pair<Integer, Player>> playerPositions(
            Pair<Integer, Pair<Player, Player>> pairPositions)
    {
        var pos = pairPositions.getLeft();
        var player1 = pairPositions.getRight()
                .getLeft();
        var player2 = pairPositions.getRight()
                .getRight();
        return Stream.of(Pair.of(pos, player1), Pair.of(pos, player2));
    }

    private ToIntFunction<Pair<Player, Player>> positionGetter(Map<Player, Integer> pos)
    {
        return pair ->
            {
                var pos1 = pos.get(pair.getLeft());
                var pos2 = pos.get(pair.getRight());
                if (pos1 < pos2)
                    return pos2 * 100 + pos1;
                return pos1 * 100 + pos2;
            };
    }

    public LocalDate getDate()
    {
        return date;
    }

    public List<Player> getPlayers()
    {
        return players;
    }

    public List<List<Pair<Player, Player>>> getPairings()
    {
        return pairings;
    }
}
