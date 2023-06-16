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
        char[] winner = null;

        try (BufferedReader br = new BufferedReader(new FileReader("people-2000000.csv"), 1024 * 1024 * 300)) {
            br.readLine();

            int commaCount = 0;
            char[] dateCharsBuffer = new char[5];

            int charRead;
            while ((charRead = br.read()) != -1) {
                if (charRead == '\n') {
                    commaCount = 0;
                } else if (charRead == ',') {
                    if (++commaCount == 7) {
                        br.skip(5);
                        br.read(dateCharsBuffer, 0, 5);
                        int month = (dateCharsBuffer[0] - '0') * 10 + (dateCharsBuffer[1] - '0');
                        int day = (dateCharsBuffer[3] - '0') * 10 + (dateCharsBuffer[4] - '0');
                        int calenderIndex = (month - 1) * 31 + (day - 1);
                        personsPerBirthDay[calenderIndex]++;
                        if (maximumPartyFriends < personsPerBirthDay[calenderIndex]) {
                            maximumPartyFriends = personsPerBirthDay[calenderIndex];
                            winner = dateCharsBuffer;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new NoSuchElementException("Cannot read CSV file", e);
        }
        return new String(winner);
    }
}
