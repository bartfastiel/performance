package org.example;

import org.openjdk.jmh.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 50, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CsvReader {

    public static final int CHARACTERS_IN_TITLE = 76;
    public static final int FIRST_CHARS_THAT_CONTAIN_ONE_COMMA_FOR_CERTAIN = 15;
    public static final int COMMAS_BEFORE_BIRTH_DATE = 5;
    public static final int FIRST_CHAR_OF_MONTH_IN_DATE = 6;
    public static final int SECOND_CHAR_OF_MONTH_IN_DATE = 7;
    public static final int FIRST_CHAR_OF_DAY_IN_DATE = 9;
    public static final int SECOND_CHAR_OF_DAY_IN_DATE = 10;
    public static final int NUMBER_OF_CHARS_OF_DATE_AND_BEHIND = 13;
    public static final int NUMBER_OF_PARALLEL_THREADS = 32;

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(new String[]{"org.example.CsvReader"});
        //  System.out.println(run());
    }

    @Benchmark
    public static String run() {
        byte[] data = readFile();

        int approximateChunkSize = data.length / NUMBER_OF_PARALLEL_THREADS;
        int[] personsPerBirthDay = IntStream.range(0, NUMBER_OF_PARALLEL_THREADS)
                .parallel()
                .map(chunk -> {
                    int chunkStart = chunk * approximateChunkSize;
                    while (chunkStart < data.length && data[chunkStart++] != '\n') ;
                    return chunkStart;
                })
                .mapToObj(chunkStart -> createHistogram(data, chunkStart, approximateChunkSize))
                .reduce((arr1, arr2) ->
                        IntStream.range(0, Math.min(arr1.length, arr2.length))
                                .map(i -> arr1[i] + arr2[i])
                                .toArray()
                )
                .orElseThrow();

        return findBiggestParty(personsPerBirthDay);
    }

    private static byte[] readFile() {
        try (var r = new FileInputStream("people-2000000.csv")) {
            return r.readAllBytes();
        } catch (IOException e) {
            throw new NoSuchElementException("Cannot read CSV file", e);
        }
    }

    private static int[] createHistogram(byte[] data, int chunkStart, int approximateChunkSize) {
        int[] personsPerBirthDay = new int[12 * 31];
        int commaCount = 0;
        for (int i = chunkStart + CHARACTERS_IN_TITLE + FIRST_CHARS_THAT_CONTAIN_ONE_COMMA_FOR_CERTAIN; i < data.length; i++) {
            if (data[i] == '\n') {
                if (i > chunkStart + approximateChunkSize) {
                    break;
                }
                commaCount = 0;
                i += FIRST_CHARS_THAT_CONTAIN_ONE_COMMA_FOR_CERTAIN;
            } else if (data[i] == ',' && commaCount++ == COMMAS_BEFORE_BIRTH_DATE) {
                int month = (data[i + FIRST_CHAR_OF_MONTH_IN_DATE] - '0') * 10 +
                            (data[i + SECOND_CHAR_OF_MONTH_IN_DATE] - '0');
                int day = (data[i + FIRST_CHAR_OF_DAY_IN_DATE] - '0') * 10 +
                          (data[i + SECOND_CHAR_OF_DAY_IN_DATE] - '0');
                int calenderIndex = (month - 1) * 31 + (day - 1);
                personsPerBirthDay[calenderIndex]++;
                i += NUMBER_OF_CHARS_OF_DATE_AND_BEHIND;
            }
        }
        return personsPerBirthDay;
    }

    private static String findBiggestParty(int[] personsPerBirthDay) {
        int max = 0;
        int maxIndex = 0;
        for (int i = 0; i < personsPerBirthDay.length; i++) {
            if (personsPerBirthDay[i] > max) {
                max = personsPerBirthDay[i];
                maxIndex = i;
            }
        }
        return "Most popular birthday is " + (maxIndex / 31 + 1) + "/" + (maxIndex % 31 + 1) + " with " + max + " people";
    }
}
