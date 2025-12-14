package com.example.englishflashcards.api;

import com.example.englishflashcards.Topic;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    // 1. Lấy danh sách chủ đề
    @GET("api/topics")
    Call<List<Topic>> getTopics();

    // 2. Lấy từ vựng theo ID chủ đề
    @GET("api/vocab/{id}")
    Call<List<VocabModel>> getVocabByTopic(@Path("id") int topicId);

    // 3. Lấy bài nghe theo ID chủ đề
    @GET("api/listening/{id}")
    Call<List<ListeningModel>> getListeningByTopic(@Path("id") int topicId);
}