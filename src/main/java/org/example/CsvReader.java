package org.example;

import org.openjdk.jmh.annotations.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
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
        int maximumPartyFriends = 0;
        maximumPartyFriends = getMaximumPartyFriends(records, persons, partyFriends, maximumPartyFriends);
        System.out.println("Max PartyFriends: "+maximumPartyFriends);
        for (Map.Entry<Person, Integer> entry : partyFriends.entrySet()) {
            if (entry.getValue() == maximumPartyFriends) {
                System.out.println(entry.getKey());
            }
        }
    }

    private static int getMaximumPartyFriends(List<List<String>> records, List<Person> persons, Map<Person, Integer> partyFriends, int maximumPartyFriends) {
        for (List<String> record : records) {
            Person p = new Person(
                    record.get(0),
                    record.get(1),
                    record.get(2),
                    record.get(3),
                    record.get(4),
                    record.get(5),
                    record.get(6),
                    LocalDate.parse(record.get(7)),
                    record.get(8)
            );
            persons.add(p);
            for (List<String> record2 : records) {
                int numberOfPersonsWithSameBirthDate = 0;
                if(
                        p.dateOfBirth().getDayOfMonth() == LocalDate.parse(record2.get(7)).getDayOfMonth()
                        &&
                        p.dateOfBirth().getMonthValue() == LocalDate.parse(record2.get(7)).getMonthValue()
                )
                {
                    numberOfPersonsWithSameBirthDate++;
                };
                partyFriends.put(p, numberOfPersonsWithSameBirthDate);

                if (numberOfPersonsWithSameBirthDate > maximumPartyFriends) {
                    maximumPartyFriends = numberOfPersonsWithSameBirthDate;
                }
            }
        }
        return maximumPartyFriends;
    }

    private static List<List<String>> readCsvData() {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("people-1000.csv"))) {
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
