package com.example.trainscore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TaskGenerator {
    private Random rand = new Random();

    private int num1;         // Первое число
    private int num2;         // Второе число
    private int correctAnswer; // Правильный ответ
    private String operation; // Операция

    // Генерация задачи
    public void generateTask(int level) {
        int max = 10 + level * 10; // Максимальное значение чисел увеличивается с уровнем
        num1 = rand.nextInt(max) + 1;
        num2 = rand.nextInt(max) + 1;

        // Случайный выбор операции
        String[] operations = {"+", "-"};
        operation = operations[rand.nextInt(operations.length)];

        // Вычисление правильного ответа
        if (operation.equals("+")) {
            correctAnswer = num1 + num2;
        } else if (operation.equals("-")) {
            if (num1 < num2) { // Обеспечиваем положительный результат
                int temp = num1;
                num1 = num2;
                num2 = temp;
            }
            correctAnswer = num1 - num2;
        }
    }

    // Возвращает текст задачи
    public String getTaskString() {
        return num1 + " " + operation + " " + num2;
    }

    // Проверка ответа
    public boolean checkAnswer(String userAnswer) {
        try {
            int answer = Integer.parseInt(userAnswer);
            return answer == correctAnswer;
        } catch (NumberFormatException e) {
            return false; // Если пользователь ввёл некорректный ответ
        }
    }

    // Генерация трёх вариантов ответа (один правильный и два случайных)
    public List<String> generateOptions() {
        List<String> options = new ArrayList<>();
        options.add(String.valueOf(correctAnswer)); // Добавляем правильный ответ
        options.add(String.valueOf(correctAnswer + rand.nextInt(5) + 1)); // Неверный ответ 1
        options.add(String.valueOf(correctAnswer - rand.nextInt(5) - 1)); // Неверный ответ 2

        Collections.shuffle(options); // Перемешиваем варианты
        return options;
    }
}
