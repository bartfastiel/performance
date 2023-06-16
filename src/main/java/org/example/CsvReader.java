package org.example;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.openjdk.jmh.annotations.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public static void run() {
        List<List<String>> records = readCsvData();
        List<String> titles = records.remove(0);
        List<Person> persons = new ArrayList<>();
        Map<Person, Integer> partyFriends = new HashMap<>();
        String biggestParty = getMaximumPartyFriends(records, persons, partyFriends);
        // System.out.println("Biggest party is on " + biggestParty);
    }

    private static String getMaximumPartyFriends(List<List<String>> records, List<Person> persons, Map<Person, Integer> partyFriends) {
        List<Person>[] personsPerBirthDay = new List[12 * 31];
        for (int i = 0; i < personsPerBirthDay.length; i++) {
            personsPerBirthDay[i] = new ArrayList<>();
        }

        int maximumPartyFriends = 0;
        String winner = null;
        for (List<String> record : records) {

            String birthDate = record.get(7);
            int month = Integer.parseInt(birthDate.substring(5, 7));
            int day = Integer.parseInt(birthDate.substring(8));
            int calenderIndex = (month - 1) * 31 + (day - 1);

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

            List<Person> partyPersons = personsPerBirthDay[calenderIndex];
            partyPersons.add(p);

            if (maximumPartyFriends < partyPersons.size()) {
                maximumPartyFriends = partyPersons.size();
                String monthDay = record.get(7).substring(5);
                winner = monthDay;
            }
        }
        return winner;
    }

    private static List<List<String>> readCsvData() {
        String zipFilePath = "people-2000000.zip";
        String textFileName = "people-2000000.csv";

        List<List<String>> records = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            ZipArchiveEntry entry = zipFile.getEntry(textFileName);

            if (entry != null) {
                try (InputStream is = zipFile.getInputStream(entry);
                     InputStreamReader isr = new InputStreamReader(is);
                     BufferedReader reader = new BufferedReader(isr)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] values = line.split(",");
                        records.add(Arrays.asList(values));
                    }
                }
            } else {
                System.out.println("Text file not found in the ZIP file.");
            }
        } catch (IOException e) {
            throw new NoSuchElementException("Cannot read CSV file", e);
        }
        return records;
    }
}
