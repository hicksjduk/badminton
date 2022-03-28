package uk.org.thehickses.badminton;

import static java.util.stream.Collectors.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

/**
 * A combiner that uses the polygon method to pair up members of a stream.
 * 
 * The result is an infinite stream of streams, where each contained stream
 * contains as many pairs of values, taken from the input, as it is possible
 * to make. This implies that if the number of values in the output is even,
 * the number of pairs will be half that number, and if it is odd, it will be
 * half that number rounded down. 
 * 
 * Each value in the input appears at most once in the pairs generated for each
 * set, and is paired exactly once with each other value in the first (n * (n-1))
 * pairs, where n is the number of values. 
 * 
 * @author Jeremy Hicks
 *
 */
public class Combiner
{
    public static <T> Stream<Stream<Pair<T, T>>> combine(Stream<T> objs)
    {
        var polygon = objs.collect(toCollection(ArrayDeque::new));
        var last = polygon.size() % 2 == 0 ? polygon.removeLast() : null;
        return Stream.generate(() -> combine(polygon, last));
    }

    private static <T> Stream<Pair<T, T>> combine(Deque<T> polygon, T extra)
    {
        var work = new ArrayDeque<>(polygon);
        var builder = Stream.<Pair<T, T>> builder();
        var lastPair = Pair.of(work.pop(), extra);
        while (work.size() > 1)
            builder.add(Pair.of(work.removeFirst(), work.removeLast()));
        builder.add(lastPair);
        polygon.addFirst(polygon.removeLast());
        return builder.build();
    }
}
