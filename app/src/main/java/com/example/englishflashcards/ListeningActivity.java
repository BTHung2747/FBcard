package com.example.englishflashcards;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar;
import java.util.ArrayList;
import com.example.englishflashcards.api.ApiClient;
import com.example.englishflashcards.api.ListeningModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import android.os.Handler;
public class ListeningActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private TextView listenQuestion;

    private int cauindex = 1;
    private LinearLayout playAudioButton;
    private TextView listenOption1, listenOption2, listenOption3, listenOption4;
    private TextView listenNextButton, tv13, listenPrevButton, tvCurrentTime, tvTotalTime;
    private List<ListeningGroup> listeningGroups;
    private int currentGroupIndex = 0, currentCardIndexInGroup = 0;
    private String currentTopicName = "";
    private int currentTopicId;
    private String correctListeningAnswer;

    private boolean isAnswerChecked = false; // Flag để ngăn nhấn nhiều lần
    private TextView correctOptionTextView = null; // Biến để lưu TextView nào là đúng
    // nút tua
    SeekBar audioSeekBar;
    Handler seekHandler = new Handler();
    boolean isUserSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);


        playAudioButton = findViewById(R.id.btnplay_audio);
        listenOption1 = findViewById(R.id.listen_option1);
        listenOption2 = findViewById(R.id.listen_option2);
        listenOption3 = findViewById(R.id.listen_option3);
        listenOption4 = findViewById(R.id.listen_option4);
        listenNextButton = findViewById(R.id.btnnext);
        listenPrevButton = findViewById(R.id.btnprev);
        audioSeekBar = findViewById(R.id.skr_audio);
        listenQuestion = findViewById(R.id.listen_question);
        tv13=findViewById(R.id.tv13);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        //1. NHẬN CHỦ ĐỀ
        currentTopicName = getIntent().getStringExtra("TOPIC_NAME");
        currentTopicId = getIntent().getIntExtra("TOPIC_ID", -1);
        if (currentTopicName == null) currentTopicName = "Shopping";

        setTitle("Luyện nghe: " + currentTopicName);
        loadListeningFromApi();
        updateListeningCard();

        playAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listeningGroups.isEmpty()) return;
                String url = listeningGroups.get(currentGroupIndex).getAudioUrl();

                String fullUrl = "http://10.0.2.2:3000/uploads/" + url;

                android.util.Log.d("CHECK_AUDIO", "Link đầy đủ là: " + fullUrl);
                if (mediaPlayer == null) {
                    playAudio(fullUrl);
                    return;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();// tạm dung
                    seekHandler.removeCallbacks(updateSeekBarRunnable);
                    return;
                }
                mediaPlayer.start();
                seekHandler.postDelayed(updateSeekBarRunnable, 1000);
            }
        });
        // Khi người dùng tua bằng tay
        audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);// vị trí thơi gian
                    tvCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
            }
        });
        // view cho bốn đáp án
        View.OnClickListener answerListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAnswerChecked) return;
                isAnswerChecked = true; // Đánh dấu là đã check

                TextView clickedTextView = (TextView) v;
                String selectedAnswer = clickedTextView.getText().toString();
                checkListeningAnswer(clickedTextView, selectedAnswer);
            }
        };

        listenOption1.setOnClickListener(answerListener);
        listenOption2.setOnClickListener(answerListener);
        listenOption3.setOnClickListener(answerListener);
        listenOption4.setOnClickListener(answerListener);

        listenNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextListeningCard();
            }
        });
        listenPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousListeningCard();
            }
        });
    }

    private void loadListeningFromApi() {
        listeningGroups = new ArrayList<>();

        ApiClient.getService().getListeningByTopic(currentTopicId).enqueue(new Callback<List<ListeningModel>>() {
            @Override
            public void onResponse(Call<List<ListeningModel>> call, Response<List<ListeningModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processApiResponse(response.body());
                    updateListeningCard();
                }
            }
            @Override
            public void onFailure(Call<List<ListeningModel>> call, Throwable t) { }
        });
    }

    // Logic gom nhóm: Các câu hỏi có cùng audio_url sẽ vào 1 nhóm
    private void processApiResponse(List<ListeningModel> rawList) {
        Map<String, List<ListeningCard>> map = new HashMap<>();

        for (ListeningModel item : rawList) {
            if (!map.containsKey(item.audio_url)) {
                map.put(item.audio_url, new ArrayList<>());
            }
            // Tạo card câu hỏi
            ListeningCard card = new ListeningCard(
                    item.question_text,
                    item.correct_word,
                    item.wrong_option1,
                    item.wrong_option2,
                    item.wrong_option3
            );
            map.get(item.audio_url).add(card);
        }

        // Chuyển Map thành List<ListeningGroup>
        for (Map.Entry<String, List<ListeningCard>> entry : map.entrySet()) {
            listeningGroups.add(new ListeningGroup(entry.getKey(), entry.getValue()));
        }
    }
    private void playAudio(String fullUrl) {
        android.util.Log.d("CHECK_AUDIO", "Đang thử phát link: " +fullUrl );
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fullUrl); // Load từ mạng
            mediaPlayer.prepareAsync(); // Chuẩn bị bất đồng bộ để không đơ máy

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                audioSeekBar.setMax(mp.getDuration());
                tvTotalTime.setText(formatTime(mp.getDuration()));
                seekHandler.postDelayed(updateSeekBarRunnable, 1000);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể phát audio", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateListeningCard() {
        if (listeningGroups.isEmpty()) return;

        ListeningGroup group = listeningGroups.get(currentGroupIndex);
        ListeningCard card = group.getCards().get(currentCardIndexInGroup);
        // tên câu hỏi
        if (card.getQuestionText() != null) {
            listenQuestion.setText(card.getQuestionText());
        } else {
            listenQuestion.setText("");
        }
        // đáp án đúng
        correctListeningAnswer = card.getCorrectWord();

        List<String> options = new ArrayList<>();
        options.add(card.getCorrectWord());
        options.add(card.getWrongOption1());
        options.add(card.getWrongOption2());
        options.add(card.getWrongOption3());
        // xáo l
        Collections.shuffle(options);

        listenOption1.setText(options.get(0));
        listenOption2.setText(options.get(1));
        listenOption3.setText(options.get(2));
        listenOption4.setText(options.get(3));
        // Tìm xem TextView nào đang giữ đáp án đúng
        correctOptionTextView = findCorrectTextView(options);
        // Reset lại giao diện
        resetOptionBackgrounds();
        isAnswerChecked = false;
    }

    // tìm ô đáp án đúng
    private TextView findCorrectTextView(List<String> options) {
        if (options.get(0).equals(correctListeningAnswer)) return listenOption1;
        if (options.get(1).equals(correctListeningAnswer)) return listenOption2;
        if (options.get(2).equals(correctListeningAnswer)) return listenOption3;
        if (options.get(3).equals(correctListeningAnswer)) return listenOption4;
        return null;
    }


    private void resetOptionBackgrounds() {
        listenOption1.setBackgroundResource(R.drawable.option_border);
        listenOption2.setBackgroundResource(R.drawable.option_border);
        listenOption3.setBackgroundResource(R.drawable.option_border);
        listenOption4.setBackgroundResource(R.drawable.option_border);
    }


    private void checkListeningAnswer(TextView clickedTextView, String selectedAnswer) {
        if (selectedAnswer.equals(correctListeningAnswer)) {
            clickedTextView.setBackgroundResource(R.drawable.card_background_back);
        } else {
            clickedTextView.setBackgroundResource(R.drawable.option_wrong);
            if (correctOptionTextView != null) {
                correctOptionTextView.setBackgroundResource(R.drawable.card_background_back);
            }
        }
    }
    private void showNextListeningCard() {
        ListeningGroup currentGroup = listeningGroups.get(currentGroupIndex);
        if (currentCardIndexInGroup < currentGroup.getCards().size() - 1) {
            // sang câu hỏi tiếp theo trong cùng audio
            currentCardIndexInGroup++;
            updateListeningCard();
            cauindex++;
            tv13.setText("Câu " +cauindex +" / 3");



        } else {
            // hết tất cả
            Toast.makeText(this, "Bạn đã hoàn thành tất cả bài nghe!", Toast.LENGTH_LONG).show();

            listenNextButton.setOnClickListener(null);

        }
    }
    // quay lại
    private void showPreviousListeningCard() {
        ListeningGroup currentGroup = listeningGroups.get(currentGroupIndex);
        if (currentCardIndexInGroup > 0) {
            currentCardIndexInGroup--;
            updateListeningCard();
            cauindex--;
            tv13.setText("Câu "+ cauindex + " / 3");
        }
        else {
                Toast.makeText(this, "Đang ở câu đầu tiên rồi!", Toast.LENGTH_SHORT).show();
            }

    }
    //Cập nhật liên tục thời gian và thanh SeekBar theo tiến độ audio đang phát
    private final Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying() && !isUserSeeking) {
                int currentPosition = mediaPlayer.getCurrentPosition();// vị trí tg
                audioSeekBar.setProgress(currentPosition);
                tvCurrentTime.setText(formatTime(currentPosition));
            }
            seekHandler.postDelayed(this, 1000);
        }
    };
    private String formatTime(int milliseconds) {
        int minutes = (milliseconds / 1000) / 60;
        int seconds = (milliseconds / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}