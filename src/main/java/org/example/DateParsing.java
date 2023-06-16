package org.example;

import java.time.LocalDate;

public class DateParsing {
    public static void main(String[] args) {
        String s = "1984-04-01";
        System.out.println(s);

        LocalDate d = LocalDate.parse(s);
        System.out.println(d);
    }
}
