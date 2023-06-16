package org.example;

import java.time.LocalDate;

public record Person(
        String index,
        String userId,
        String firstName,
        String lastName,
        String sex,
        String email,
        String phone,
        String birthday,
        String jobTitle
) {
}
