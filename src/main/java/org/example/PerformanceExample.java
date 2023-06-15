package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(time = 1, timeUnit = TimeUnit.SECONDS, iterations = 2)
@Measurement(time = 1, timeUnit = TimeUnit.SECONDS, iterations = 10)
@Fork(1)
public class PerformanceExample {

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public static void run() {
        int amountOfNumbers = 20_000_000;

        List<Integer> numbers = generateNumbers(amountOfNumbers);
        List<Integer> filteredNumbers = filterNumbers(numbers);
        int sum = calculateSum(filteredNumbers);
    }

    private static List<Integer> generateNumbers(int amountOfNumbers) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < amountOfNumbers; i++) {
            numbers.add(i);
        }
        return numbers;
    }

    private static List<Integer> filterNumbers(List<Integer> numbers) {
        List<Integer> filteredNumbers = new ArrayList<>();
        for (Integer number : numbers) {
            if (number % 2 == 0) {
                filteredNumbers.add(number);
            }
        }
        return filteredNumbers;
    }

    private static int calculateSum(List<Integer> numbers) {
        int sum = 0;
        for (Integer number : numbers) {
            sum += number;
        }
        return sum;
    }
}
