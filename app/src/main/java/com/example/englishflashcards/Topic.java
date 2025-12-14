package com.example.englishflashcards;

public class Topic {
    private int id;      // Thêm ID từ database
    private String name;

    // Constructor & Getter
    public Topic(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}