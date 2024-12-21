package com.example.trainscore;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvTask, tvScore, tvErrors, tvFeedback;
    private Button btnOption1, btnOption2, btnOption3;

    private TaskGenerator taskGenerator;

    private int score = 0;
    private int errors = 0;
    private int level = 1; // уровень сложности
    private final int maxErrors = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTask = findViewById(R.id.tvTask);
        tvScore = findViewById(R.id.tvScore);
        tvErrors = findViewById(R.id.tvErrors);
        tvFeedback = findViewById(R.id.tvFeedback);

        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);

        taskGenerator = new TaskGenerator();
        generateNewTask();

        btnOption1.setOnClickListener(view -> checkUserAnswer(btnOption1.getText().toString()));
        btnOption2.setOnClickListener(view -> checkUserAnswer(btnOption2.getText().toString()));
        btnOption3.setOnClickListener(view -> checkUserAnswer(btnOption3.getText().toString()));
    }

    private void generateNewTask() {
        taskGenerator.generateTask(level);
        tvTask.setText("Решите задачу: " + taskGenerator.getTaskString());

        // Генерируем варианты ответа
        List<String> options = taskGenerator.generateOptions();
        btnOption1.setText(options.get(0));
        btnOption2.setText(options.get(1));
        btnOption3.setText(options.get(2));

        tvFeedback.setText("");
    }

    private void checkUserAnswer(String userAnswer) {
        boolean correct = taskGenerator.checkAnswer(userAnswer);

        if (correct) {
            score += 10;
            tvFeedback.setText("Верно!");
            tvScore.setText("Очки: " + score);

            if (score % 50 == 0) {
                level++;
            }
        } else {
            errors++;
            tvFeedback.setText("Неверно!");
            tvErrors.setText("Ошибки: " + errors + " из " + maxErrors);

            if (errors >= maxErrors) {
                tvFeedback.setText("Игра окончена! Ваш счёт: " + score);
                disableButtons();
                return;
            }
        }
        generateNewTask();
    }

    private void disableButtons() {
        btnOption1.setEnabled(false);
        btnOption2.setEnabled(false);
        btnOption3.setEnabled(false);
    }
}
