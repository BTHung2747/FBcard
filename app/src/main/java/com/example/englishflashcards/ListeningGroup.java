package com.example.englishflashcards;

import java.util.List;

public class ListeningGroup {
    // SỬA: Đổi từ int sang String để chứa đường link URL
    private String audioUrl;
    private List<ListeningCard> cards;

    public ListeningGroup(String audioUrl, List<ListeningCard> cards) {
        this.audioUrl = audioUrl;
        this.cards = cards;
    }

    // SỬA: Getter cũng trả về String
    public String getAudioUrl() {
        return audioUrl;
    }

    public List<ListeningCard> getCards() {
        return cards;
    }
}