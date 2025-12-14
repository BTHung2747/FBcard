package com.example.englishflashcards;
import com.example.englishflashcards.api.ApiClient;
import com.example.englishflashcards.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class TopicsActivity extends AppCompatActivity {

    RecyclerView rvTopics;
    TopicAdapter adapter;
    List<Topic> mListTopics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        rvTopics = findViewById(R.id.rvTopics);
        mListTopics = new ArrayList<>();

        rvTopics.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new TopicAdapter(this, mListTopics, topic -> showOptionDialog(topic));
        rvTopics.setAdapter(adapter);

        // --- GỌI API LẤY DATA ---
        fetchTopicsFromApi();
    }

    private void fetchTopicsFromApi() {
        ApiClient.getService().getTopics().enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mListTopics.clear();
                    mListTopics.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Cập nhật giao diện
                }
            }

            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                Toast.makeText(TopicsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOptionDialog(Topic topic) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chủ đề: " + topic.getName());
        builder.setMessage("Bạn muốn học kỹ năng nào?");

        builder.setPositiveButton("Từ vựng", (dialog, which) -> {
            Intent intent = new Intent(TopicsActivity.this, ExamActivity.class);
            intent.putExtra("TOPIC_ID", topic.getId());     // Truyền ID
            intent.putExtra("TOPIC_NAME", topic.getName()); // Truyền Tên
            startActivity(intent);
        });

        builder.setNegativeButton("Luyện nghe", (dialog, which) -> {
            Intent intent = new Intent(TopicsActivity.this, ListeningActivity.class);
            intent.putExtra("TOPIC_ID", topic.getId());
            intent.putExtra("TOPIC_NAME", topic.getName());
            startActivity(intent);
        });
        builder.show();
    }
}