package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CsvFileShrinker {

    public static void main(String[] args) throws IOException {
        try (FileWriter fileWriter = new FileWriter("people-2000000-shortened.csv")) {
            Files.readAllLines(Paths.get("people-2000000.csv"))
                    .stream()
                    .map(line -> {
                        String[] split = line.split(",");
                        return split[0] + "," + split[7];
                    })
                    .forEach(line -> {
                        try {
                            fileWriter.write(line + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
