package uk.org.thehickses.badminton;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.Collection;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CombinerTest
{
    @ParameterizedTest
    @MethodSource
    void testCombine(int count)
    {
        var result = Combiner.combine(IntStream.range(0, count)
                .boxed())
                .limit(count % 2 == 0 ? count - 1 : count);
        check(result, count / 2, count - 1);
    }

    static Stream<Arguments> testCombine()
    {
        return Stream.of(arguments(6), arguments(9));
    }

    <T> void check(Stream<Stream<Pair<T, T>>> result, int expectedCombsPerSet,
            int expectedPartnerCount)
    {
        var array = result.map(s -> s.toArray(Pair[]::new))
                .toArray(Pair[][]::new);
        var uniqueValueCountsPerSet = Stream.of(array)
                .peek(a -> assertThat(a.length).isEqualTo(expectedCombsPerSet))
                .map(Stream::of)
                .map(s -> s.flatMap(p -> Stream.of(p.getLeft(), p.getRight()))
                        .collect(toSet()))
                .map(Collection::size);
        uniqueValueCountsPerSet.forEach(c -> assertThat(c).isEqualTo(expectedCombsPerSet * 2));
        var partnersByValue = Stream.of(array)
                .flatMap(Stream::of)
                .flatMap(p -> Stream.of(p, Pair.of(p.getRight(), p.getLeft())))
                .collect(groupingBy(Pair::getLeft, mapping(Pair::getRight, toSet())));
        partnersByValue.values()
                .stream()
                .map(Collection::size)
                .forEach(v -> assertThat(v).isEqualTo(expectedPartnerCount));
    }
}
