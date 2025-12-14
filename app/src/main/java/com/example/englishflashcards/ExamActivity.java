package com.example.englishflashcards;



import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import com.example.englishflashcards.api.ApiClient;
import com.example.englishflashcards.api.VocabModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;

public class ExamActivity extends AppCompatActivity {

    private TextView questionWord, feedbackText ,timerText;
    private TextView option1, option2, option3, option4, nextButton,btn_tab1;
    // Thêm biến lưu chủ đề hiện tại
    private String currentTopicName = "";
    private int currentTopicId;

    private List<Flashcard> flashcardList;
    private int currentCardIndex = 0;
    private Handler handler = new Handler();
    private List<String> resultList = new ArrayList<>();
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private int timeRemaining = 15;


    private String correctAnswer;  // đáp án đúng


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam);
        // --- Ánh xạ ---
        questionWord = findViewById(R.id.question_word);
        feedbackText = findViewById(R.id.feedback_text);
        timerText = findViewById(R.id.timer_text);
        btn_tab1= findViewById(R.id.btn_tab1);
        option1 = findViewById(R.id.option1_button);
        option2 = findViewById(R.id.option2_button);
        option3 = findViewById(R.id.option3_button);
        option4 = findViewById(R.id.option4_button);
        nextButton = findViewById(R.id.next_button);
        // lay chu de
        currentTopicId = getIntent().getIntExtra("TOPIC_ID", -1);
        String topicName = getIntent().getStringExtra("TOPIC_NAME");
        setTitle("Từ vựng: " + topicName);

        loadFlashcardsFromApi();
        updateCard();
        btn_tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // --- Xử lý chọn đáp án ---
        View.OnClickListener answerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView clickedBox = (TextView) v;
                String selectedAnswer = clickedBox.getText().toString();
                checkAnswer(clickedBox, selectedAnswer);
            }
        };

        option1.setOnClickListener(answerListener);
        option2.setOnClickListener(answerListener);
        option3.setOnClickListener(answerListener);
        option4.setOnClickListener(answerListener);

        // --- Nút Next ---
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextCard();
            }
        });
    }

    // Dữ liệu flashcard
    private void loadFlashcardsFromApi() {
        flashcardList = new ArrayList<>();

        ApiClient.getService().getVocabByTopic(currentTopicId).enqueue(new Callback<List<VocabModel>>() {
            @Override
            public void onResponse(Call<List<VocabModel>> call, Response<List<VocabModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Chuyển đổi dữ liệu từ API (VocabModel) sang dữ liệu App (Flashcard)
                    for (VocabModel model : response.body()) {
                        flashcardList.add(new Flashcard(
                                model.english_word,
                                model.correct_meaning,
                                model.wrong_option1,
                                model.wrong_option2,
                                model.wrong_option3
                        ));
                    }

                    if (!flashcardList.isEmpty()) {
                        updateCard();
                    } else {
                        Toast.makeText(ExamActivity.this, "Chủ đề này chưa có từ vựng!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<VocabModel>> call, Throwable t) {
                Toast.makeText(ExamActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật câu hỏi và 4 lựa chọn
    private void updateCard() {
        if (flashcardList != null && !flashcardList.isEmpty()) {
            Flashcard currentCard = flashcardList.get(currentCardIndex);
            questionWord.setText(currentCard.getEnglishWord());
            // lưu đáp đúng
            correctAnswer = currentCard.getCorrectMeaning();

            List<String> options = new ArrayList<>();
            options.add(currentCard.getCorrectMeaning());
            options.add(currentCard.getWrongOption1());
            options.add(currentCard.getWrongOption2());
            options.add(currentCard.getWrongOption3());
            Collections.shuffle(options);

            option1.setText(options.get(0));
            option2.setText(options.get(1));
            option3.setText(options.get(2));
            option4.setText(options.get(3));

            feedbackText.setText("");
            resetBoxColors();
            enableBoxes(true);
            nextButton.setEnabled(true);
            // tự chuyênr sau 15s
            startTimer();

        }
    }

    // Kiểm tra đáp án
    private void checkAnswer(TextView clickedBox, String selectedAnswer) {
        enableBoxes(false);
        timerHandler.removeCallbacksAndMessages(null); // dừng mọi tác vụ liên quan nhu đếm ngược chuyển câu


        if (selectedAnswer.equals(correctAnswer)) {
            clickedBox.setBackgroundResource(R.drawable.card_background_back);
            feedbackText.setText("✅ Đúng rồi!");
            feedbackText.setTextColor(Color.GREEN);
            resultList.add("Câu " + (currentCardIndex + 1) + ": ✅ " + selectedAnswer);
        } else {
            clickedBox.setBackgroundResource(R.drawable.option_wrong);
            feedbackText.setText("❌ Sai rồi! Đáp án đúng là: " + correctAnswer);
            feedbackText.setTextColor(Color.RED);
            resultList.add("Câu " + (currentCardIndex + 1) + ": ❌ " + selectedAnswer + " → Đúng: " + correctAnswer);

            // Tô xanh ô đúng
            if (option1.getText().toString().equals(correctAnswer)) option1.setBackgroundResource(R.drawable.card_background_back);
            if (option2.getText().toString().equals(correctAnswer)) option2.setBackgroundResource(R.drawable.card_background_back);
            if (option3.getText().toString().equals(correctAnswer)) option3.setBackgroundResource(R.drawable.card_background_back);
            if (option4.getText().toString().equals(correctAnswer)) option4.setBackgroundResource(R.drawable.card_background_back);
        }
    }

    // Chuyển sang câu kế tiếp
    private void showNextCard() {
        if (currentCardIndex < flashcardList.size() - 1) {
            currentCardIndex++;
            updateCard();
        } else {
            showResultList();

        }
    }
    private void enableBoxes(boolean isEnabled) {
        option1.setEnabled(isEnabled);
        option2.setEnabled(isEnabled);
        option3.setEnabled(isEnabled);
        option4.setEnabled(isEnabled);
    }


    private void resetBoxColors() {
        option1.setBackgroundResource(R.drawable.bg_option_item);
        option2.setBackgroundResource(R.drawable.bg_option_item);
        option3.setBackgroundResource(R.drawable.bg_option_item);
        option4.setBackgroundResource(R.drawable.bg_option_item);
    }
    private void showResultList() {
        StringBuilder sb = new StringBuilder();
        int correctCount = 0;
        for (String s : resultList) {
            sb.append(s).append("\n");
            if (s.contains("✅")) correctCount++;
        }

        String summary = "\nĐiểm số: " + correctCount + "/" + resultList.size();

        // --- Gắn layout tuỳ chỉnh cho dialog ---
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_result, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        android.widget.Button btnRetry = dialogView.findViewById(R.id.btnRetry);
        android.widget.Button btnClose = dialogView.findViewById(R.id.btnClose);

        tvMessage.setText(sb.toString() + summary);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnRetry.setOnClickListener(v -> {
            currentCardIndex = 0;
            resultList.clear();
            updateCard();
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void startTimer() {
        timeRemaining = 15;
        timerText.setText(String.valueOf(timeRemaining));

        timerHandler.removeCallbacksAndMessages(null); // xoá timer cũ nếu có
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeRemaining--;
                timerText.setText(String.valueOf(timeRemaining));

                if (timeRemaining > 0) {
                    timerHandler.postDelayed(this, 1000);
                } else {
                    // hết thời gian tự chuyển câu
                    feedbackText.setText("⏰ Hết thời gian!");
                    feedbackText.setTextColor(Color.RED);
                    enableBoxes(false);
                    showNextCard();
                }
            }
        }, 1000);
    }



}
