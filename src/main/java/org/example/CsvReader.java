package org.example;

import org.openjdk.jmh.annotations.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class CsvReader {

    public static void main(String[] args) throws IOException {
        org.openjdk.jmh.Main.main(args);
    }

    @Benchmark
    public static void run() {
        List<List<String>> records = readCsvData();
        List<String> titles = records.remove(0);
        List<Person> persons = new ArrayList<>();
        Map<Person, Integer> partyFriends = new HashMap<>();
        MonthDay biggestParty = getMaximumPartyFriends(records, persons, partyFriends);
        // System.out.println("Biggest party is on " + biggestParty);
    }

    private static MonthDay getMaximumPartyFriends(List<List<String>> records, List<Person> persons, Map<Person, Integer> partyFriends) {
        Map<MonthDay, List<Person>> personsPerBirthDay = new HashMap<>();
        for (List<String> record : records) {
            LocalDate birthDate = LocalDate.parse(record.get(7));
            Person p = new Person(
                    record.get(0),
                    record.get(1),
                    record.get(2),
                    record.get(3),
                    record.get(4),
                    record.get(5),
                    record.get(6),
                    birthDate,
                    record.get(8)
            );
            persons.add(p);
            MonthDay monthDay = MonthDay.from(birthDate);

            personsPerBirthDay
                    .computeIfAbsent(monthDay, k -> new ArrayList<>())
                    .add(p);
        }

        int maximumPartyFriends = 0;
        Map.Entry<MonthDay, List<Person>> winner = null;
        for (Map.Entry<MonthDay, List<Person>> entry : personsPerBirthDay.entrySet()) {
            int partyPersons = entry.getValue().size();
            if (maximumPartyFriends < partyPersons) {
                maximumPartyFriends = partyPersons;
                winner = entry;
            }
        }

        return winner.getKey();
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
