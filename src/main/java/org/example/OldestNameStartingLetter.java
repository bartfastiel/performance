package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

public class OldestNameStartingLetter {

    private static LocalDate TODAY = LocalDate.now();

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("people-2000000.csv"));
        Map<String, List<LocalDate>> birthDays = new HashMap<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] items = line.split(",");
            String name = items[2];
            String birth = items[7];
            birthDays.computeIfAbsent(name, k -> new ArrayList<>())
                    .add(LocalDate.parse(birth));
        }
        Map<String, Integer> agesByName = birthDays.entrySet().stream().map(
                e -> Map.entry(e.getKey(), averageAgeDays(e.getValue()))
        ).collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
        );
        agesByName.entrySet().stream().sorted(
                Map.Entry.comparingByValue()
        ).forEach(System.out::println);
    }

    private static int averageAgeDays(List<LocalDate> birthDays) {
        int sum = 0;
        for (LocalDate birthDay : birthDays) {
            sum += DAYS.between(birthDay, TODAY);
        }
        return sum / birthDays.size();
    }
}
