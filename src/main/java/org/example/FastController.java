package org.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fast")
class FastController {

    @GetMapping
    int fast() {
        int[] numbers = new int[10_000_000];
        for (int i = 0; i < 10_000_000; i++) {
            numbers[i] = i;
            System.out.println("bla");
        }
        return numbers.length;
    }

}
