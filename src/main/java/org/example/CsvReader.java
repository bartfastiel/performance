package org.example;

import org.openjdk.jmh.annotations.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 50, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CsvReader {

    public static final int CHARACTERS_IN_TITLE = 76;
    public static final int FIRST_CHARS_THAT_CONTAIN_ONE_COMMA_FOR_CERTAIN = 15;

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(new String[]{"org.example.CsvReader"});
        // System.out.println(run());
    }

    @Benchmark
    public static String run() {
        int[] personsPerBirthDay = new int[12 * 31];
        try (var r = new FileInputStream("people-2000000.csv")) {
            byte[] data = r.readAllBytes();
            int commaCount = 0;
            for (int i = CHARACTERS_IN_TITLE + FIRST_CHARS_THAT_CONTAIN_ONE_COMMA_FOR_CERTAIN; i < data.length; i++) {
                if (data[i] == '\n') {
                    commaCount = 0;
                    i += FIRST_CHARS_THAT_CONTAIN_ONE_COMMA_FOR_CERTAIN;
                } else if (data[i] == ',') {
                    if (commaCount++ == 5) {
                        int month = (data[i + 6] - '0') * 10 + (data[i + 7] - '0');
                        int day = (data[i + 9] - '0') * 10 + (data[i + 10] - '0');
                        int calenderIndex = (month - 1) * 31 + (day - 1);
                        personsPerBirthDay[calenderIndex]++;
                        i += 13;
                    }
                }
            }
        } catch (IOException e) {
            throw new NoSuchElementException("Cannot read CSV file", e);
        }
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
