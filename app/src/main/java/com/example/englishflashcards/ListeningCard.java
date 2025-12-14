package com.example.englishflashcards;

public class ListeningCard {
    // nội dung hiện có
    private String correctWord;
    private String wrongOption1;
    private String wrongOption2;
    private String wrongOption3;


    private String questionText;

    public ListeningCard(String correctWord, String wrongOption1, String wrongOption2, String wrongOption3) {
        this.correctWord = correctWord;
        this.wrongOption1 = wrongOption1;
        this.wrongOption2 = wrongOption2;
        this.wrongOption3 = wrongOption3;
        this.questionText = null; //
    }

    public ListeningCard(String questionText, String correctWord, String wrongOption1, String wrongOption2, String wrongOption3) {
        this.correctWord = correctWord;
        this.wrongOption1 = wrongOption1;
        this.wrongOption2 = wrongOption2;
        this.wrongOption3 = wrongOption3;
        this.questionText = questionText;
    }

    public String getCorrectWord() {
        return correctWord;
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

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}