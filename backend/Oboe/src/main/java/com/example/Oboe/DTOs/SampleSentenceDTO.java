package com.example.Oboe.DTOs;

import java.util.UUID;

public class SampleSentenceDTO {
    
    private UUID sentenceId;
    
    // Đã đổi tên: japaneseText -> englishSentence
    private String englishSentence;
    
    private String vietnameseTranslation; // Đã đổi tên thuộc tính trong DTO (từ vietnameseMeaning)

    // Các trường mới
    private String difficulty;
    private String audioUrl;
    
    // Các ID liên kết
    private UUID relatedVocabId;
    private UUID relatedIdiomId;
    private UUID relatedGrammarId;

    // --- Constructors ---
    
    // Constructor không tham số
    public SampleSentenceDTO() {}

    // Constructor đầy đủ tham số
    public SampleSentenceDTO(UUID sentenceId, String englishSentence, String vietnameseTranslation, 
                             String difficulty, String audioUrl, UUID relatedVocabId, 
                             UUID relatedIdiomId, UUID relatedGrammarId) {
        this.sentenceId = sentenceId;
        this.englishSentence = englishSentence;
        this.vietnameseTranslation = vietnameseTranslation;
        this.difficulty = difficulty;
        this.audioUrl = audioUrl;
        this.relatedVocabId = relatedVocabId;
        this.relatedIdiomId = relatedIdiomId;
        this.relatedGrammarId = relatedGrammarId;
    }

    // --- Getters & Setters ---
    
    public UUID getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(UUID sentenceId) {
        this.sentenceId = sentenceId;
    }

    public String getEnglishSentence() {
        return englishSentence;
    }

    public void setEnglishSentence(String englishSentence) {
        this.englishSentence = englishSentence;
    }

    public String getVietnameseTranslation() {
        return vietnameseTranslation;
    }

    public void setVietnameseTranslation(String vietnameseTranslation) {
        this.vietnameseTranslation = vietnameseTranslation;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public UUID getRelatedVocabId() {
        return relatedVocabId;
    }

    public void setRelatedVocabId(UUID relatedVocabId) {
        this.relatedVocabId = relatedVocabId;
    }

    public UUID getRelatedIdiomId() {
        return relatedIdiomId;
    }

    public void setRelatedIdiomId(UUID relatedIdiomId) {
        this.relatedIdiomId = relatedIdiomId;
    }

    public UUID getRelatedGrammarId() {
        return relatedGrammarId;
    }

    public void setRelatedGrammarId(UUID relatedGrammarId) {
        this.relatedGrammarId = relatedGrammarId;
    }
}