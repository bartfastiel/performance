package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/birthdays")
class BirthdayController {

    @GetMapping
    String get() {
        return CsvReader.run();
    }
}
