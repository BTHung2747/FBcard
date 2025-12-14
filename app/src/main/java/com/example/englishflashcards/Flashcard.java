package com.example.englishflashcards;
public class Flashcard {
    private String englishWord;
    private String correctMeaning;
    private String wrongOption1;
    private String wrongOption2;
    private String wrongOption3;

    public Flashcard(String englishWord, String correctMeaning, String wrongOption1, String wrongOption2, String wrongOption3) {
        this.englishWord = englishWord;
        this.correctMeaning = correctMeaning;
        this.wrongOption1 = wrongOption1;
        this.wrongOption2 = wrongOption2;
        this.wrongOption3 = wrongOption3;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public String getCorrectMeaning() {
        return correctMeaning;
    }

    public String getWrongOption1() {
        return wrongOption1;
    }

    public String getWrongOption2() {
        return wrongOption2;
    }

    public String getWrongOption3() {
        return wrongOption3;
    }
}