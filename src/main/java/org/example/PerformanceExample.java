package org.example;

import java.util.ArrayList;
import java.util.List;

public class PerformanceExample {
    public static void main(String[] args) {
        int amountOfNumbers = 100_000_000;

        List<Integer> numbers = generateNumbers(amountOfNumbers);
        List<Integer> filteredNumbers = filterNumbers(numbers);
        int sum = calculateSum(filteredNumbers);
        System.out.println("Sum: " + sum);
    }

    private static List<Integer> generateNumbers(int amountOfNumbers) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < amountOfNumbers; i++) {
            numbers.add(i);
        }
        return numbers;
    }

    private static List<Integer> filterNumbers(List<Integer> numbers) {
        List<Integer> filteredNumbers = new ArrayList<>();
        for (Integer number : numbers) {
            if (number % 2 == 0) {
                filteredNumbers.add(number);
            }
        }
        return filteredNumbers;
    }

    private static int calculateSum(List<Integer> numbers) {
        int sum = 0;
        for (Integer number : numbers) {
            sum += number;
        }
        return sum;
    }
}
