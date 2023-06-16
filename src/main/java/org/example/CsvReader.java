package org.example;

import org.openjdk.jmh.annotations.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 50, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CsvReader {

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(new String[]{"org.example.CsvReader"});
        // run();
    }

    @Benchmark
    public static String run() {
        int[] personsPerBirthDay = new int[12 * 31];

        int maximumPartyFriends = 0;
        String winner = null;

        try (BufferedReader br = new BufferedReader(new FileReader("people-2000000.csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String birthDate = values[7];

                int month = Integer.parseInt(birthDate.substring(5, 7));
                int day = Integer.parseInt(birthDate.substring(8));
                int calenderIndex = (month - 1) * 31 + (day - 1);

                personsPerBirthDay[calenderIndex]++;

                if (maximumPartyFriends < personsPerBirthDay[calenderIndex]) {
                    maximumPartyFriends = personsPerBirthDay[calenderIndex];
                    winner = birthDate;
                }
            }
        } catch (IOException e) {
            throw new NoSuchElementException("Cannot read CSV file", e);
        }
        return winner.substring(5);
    }
}
