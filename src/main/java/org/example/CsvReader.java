package org.example;

import org.openjdk.jmh.annotations.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 50, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CsvReader {

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(new String[]{"org.example.CsvReader"});
        //run();
    }

    @Benchmark
    public static String run() {
        List<List<String>> records = readCsvData();
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
        return readCsvDataBufferedOld();
    }

    /**
     * Benchmark      Mode  Cnt  Score   Error  Units
     * CsvReader.run  avgt   50  1,198 ± 0,018   s/op
     */
    private static List<List<String>> readCsvDataNioAllLines() {
        try {
            return Files.readAllLines(Path.of("people-2000000.csv"))
                    .stream()
                    .skip(1)
                    .map(line -> Arrays.asList(line.split(",")))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Cannot read CSV", e);
        }
    }


    /**
     * Benchmark      Mode  Cnt  Score   Error  Units
     * CsvReader.run  avgt   50  1,215 ± 0,019   s/op
     */
    private static List<List<String>> readCsvDataBigBuffer() {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("people-2000000.csv"), 1024 * 1024 * 300)) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            throw new NoSuchElementException("Cannot read CSV file", e);
        }
        return records;
    }

    /**
     * Benchmark      Mode  Cnt  Score   Error  Units
     * CsvReader.run  avgt   50  1,201 ± 0,021   s/op
     */
    private static List<List<String>> readCsvDataBufferedOld() {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("people-2000000.csv"))) {
            String line;
            br.readLine();
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
