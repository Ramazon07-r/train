package com.example.mathquiz;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button startGameButton;
    private MediaPlayer buttonSound;
    private TextView exampleText, timerText, levelText, scoreText;
    private Button option1Button, option2Button, option3Button, easyButton, mediumButton, hardButton;
    private int level = 1;
    private int errors = 0;
    private int currentScore = 0;
    private int highScore = 0;
    private CountDownTimer timer;
    private Random random = new Random();
    private int correctAnswer;
    private int difficulty = 1; // 1 - Легкий, 2 - Средний, 3 - Сложный
    private SharedPreferences preferences;
    private boolean isAnswered = false; // Флаг для предотвращения повторного выбора

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("MathQuizPrefs", MODE_PRIVATE);
        highScore = preferences.getInt("highScore", 0);
        buttonSound = MediaPlayer.create(this, R.raw.click_sound); // Загружаем звук кнопок

        startGameButton = findViewById(R.id.start_game_button);
        easyButton = findViewById(R.id.easy_button);
        mediumButton = findViewById(R.id.medium_button);
        hardButton = findViewById(R.id.hard_button);
        exampleText = findViewById(R.id.example_text);
        timerText = findViewById(R.id.timer_text);
        levelText = findViewById(R.id.level_text);
        scoreText = findViewById(R.id.score_text);
        option1Button = findViewById(R.id.option1_button);
        option2Button = findViewById(R.id.option2_button);
        option3Button = findViewById(R.id.option3_button);

        startGameButton.setOnClickListener(v -> {
            playButtonSound();
            startGameButton.setVisibility(View.GONE);
            easyButton.setVisibility(View.VISIBLE);
            mediumButton.setVisibility(View.VISIBLE);
            hardButton.setVisibility(View.VISIBLE);
        });

        easyButton.setOnClickListener(v -> {
            playButtonSound();
            difficulty = 1;
            startGame();
        });

        mediumButton.setOnClickListener(v -> {
            playButtonSound();
            difficulty = 2;
            startGame();
        });

        hardButton.setOnClickListener(v -> {
            playButtonSound();
            difficulty = 3;
            startGame();
        });

        option1Button.setOnClickListener(v -> handleAnswer(option1Button));
        option2Button.setOnClickListener(v -> handleAnswer(option2Button));
        option3Button.setOnClickListener(v -> handleAnswer(option3Button));

        setupGameStart();
    }

    private void playButtonSound() {
        if (buttonSound != null) {
            buttonSound.start(); // Воспроизвести звук
        }
    }

    private void setupGameStart() {
        startGameButton.setVisibility(View.VISIBLE);
        easyButton.setVisibility(View.GONE);
        mediumButton.setVisibility(View.GONE);
        hardButton.setVisibility(View.GONE);
        exampleText.setVisibility(View.GONE);
        timerText.setVisibility(View.GONE);
        levelText.setVisibility(View.GONE);
        scoreText.setVisibility(View.GONE);
        option1Button.setVisibility(View.GONE);
        option2Button.setVisibility(View.GONE);
        option3Button.setVisibility(View.GONE);
    }

    private void startGame() {
        easyButton.setVisibility(View.GONE);
        mediumButton.setVisibility(View.GONE);
        hardButton.setVisibility(View.GONE);
        exampleText.setVisibility(View.VISIBLE);
        timerText.setVisibility(View.VISIBLE);
        levelText.setVisibility(View.VISIBLE);
        scoreText.setVisibility(View.VISIBLE);
        option1Button.setVisibility(View.VISIBLE);
        option2Button.setVisibility(View.VISIBLE);
        option3Button.setVisibility(View.VISIBLE);
        currentScore = 0;
        errors = 0;
        level = 1;
        isAnswered = false;
        updateScoreDisplay();
        generateNewExample();
    }

    private void generateNewExample() {
        if (timer != null) timer.cancel();
        isAnswered = false;

        int num1 = random.nextInt(10 * difficulty) + 1;
        int num2 = random.nextInt(10 * difficulty) + 1;
        String operation = random.nextBoolean() ? "+" : "-";
        if (difficulty > 1 && random.nextBoolean()) {
            operation = random.nextBoolean() ? "*" : "/";
        }
        if (operation.equals("/") && num2 == 0) {
            num2 = 1;
        }

        correctAnswer = operation.equals("+") ? num1 + num2 : operation.equals("-") ? num1 - num2 :
                operation.equals("*") ? num1 * num2 : num1 / num2;

        String question = num1 + " " + operation + " " + num2 + " = ?";
        exampleText.setText(question);
        levelText.setText("Уровень: " + level);

        setAnswerOptions(correctAnswer);
        startTimer();
    }

    private void setAnswerOptions(int correctAnswer) {
        int wrongAnswer1 = correctAnswer + random.nextInt(5 * difficulty) + 1;
        int wrongAnswer2 = correctAnswer - random.nextInt(5 * difficulty) - 1;

        int correctPosition = random.nextInt(3);
        if (correctPosition == 0) {
            option1Button.setText(String.valueOf(correctAnswer));
            option2Button.setText(String.valueOf(wrongAnswer1));
            option3Button.setText(String.valueOf(wrongAnswer2));
        } else if (correctPosition == 1) {
            option1Button.setText(String.valueOf(wrongAnswer1));
            option2Button.setText(String.valueOf(correctAnswer));
            option3Button.setText(String.valueOf(wrongAnswer2));
        } else {
            option1Button.setText(String.valueOf(wrongAnswer2));
            option2Button.setText(String.valueOf(wrongAnswer1));
            option3Button.setText(String.valueOf(correctAnswer));
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Осталось времени: " + millisUntilFinished / 1000 + " сек");
            }

            @Override
            public void onFinish() {
                if (!isAnswered) {
                    Toast.makeText(MainActivity.this, "Время вышло!", Toast.LENGTH_SHORT).show();
                    handleWrongAnswer();
                }
            }
        }.start();
    }

    private void handleAnswer(Button selectedButton) {
        if (isAnswered) return;
        isAnswered = true;
        playButtonSound();

        int selectedAnswer = Integer.parseInt(selectedButton.getText().toString());
        if (selectedAnswer == correctAnswer) {
            Toast.makeText(this, "Правильно!", Toast.LENGTH_SHORT).show();
            currentScore++;
            updateScoreDisplay();
            if (currentScore > highScore) {
                highScore = currentScore;
                saveHighScore();
            }
            level++;
            generateNewExample();
        } else {
            handleWrongAnswer();
        }
    }

    private void handleWrongAnswer() {
        errors++;
        if (errors >= 3) {
            showGameOverDialog();
        } else {
            Toast.makeText(this, "Неправильно! Правильный ответ: " + correctAnswer, Toast.LENGTH_SHORT).show();
            generateNewExample();
        }
    }

    private void showGameOverDialog() {
        if (timer != null) timer.cancel();
        new android.app.AlertDialog.Builder(this)
                .setTitle("Игра окончена")
                .setMessage("Ваш уровень: " + level + "\nРекорд: " + highScore)
                .setPositiveButton("Снова играть", (dialog, which) -> restartGame())
                .setNegativeButton("Выход", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void restartGame() {
        level = 1;
        errors = 0;
        currentScore = 0;
        setupGameStart();
    }

    private void updateScoreDisplay() {
        scoreText.setText("Ваш счёт: " + currentScore);
    }

    private void saveHighScore() {
        preferences.edit().putInt("highScore", highScore).apply();
    }
}
