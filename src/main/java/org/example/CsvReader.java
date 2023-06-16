package org.example;

import org.openjdk.jmh.annotations.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CsvReader {

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(new String[]{"org.example.CsvReader"});
        //run();
    }

    @Benchmark
    public static String run() {
        List<List<String>> records = readCsvData();
        List<String> titles = records.remove(0);
        String biggestParty = getMaximumPartyFriends(records);
        return biggestParty;
        // System.out.println("Biggest party is on " + biggestParty);
    }

    private static String getMaximumPartyFriends(List<List<String>> records) {
        int[] personsPerBirthDay = new int[12 * 31];

        int maximumPartyFriends = 0;
        String winner = null;
        for (List<String> record : records) {

            String birthDate = record.get(7);
            int month = Integer.parseInt(birthDate.substring(5, 7));
            int day = Integer.parseInt(birthDate.substring(8));
            int calenderIndex = (month - 1) * 31 + (day - 1);

            personsPerBirthDay[calenderIndex]++;

            if (maximumPartyFriends < personsPerBirthDay[calenderIndex]) {
                maximumPartyFriends = personsPerBirthDay[calenderIndex];
                winner = record.get(7).substring(5);
            }
        }
        return winner;
    }

    private static List<List<String>> readCsvData() {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("people-2000000.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new NoSuchElementException("Cannot read CSV file", e);
        }
        return records;
    }
}
