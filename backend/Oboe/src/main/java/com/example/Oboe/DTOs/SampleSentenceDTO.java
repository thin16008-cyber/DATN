package com.example.Oboe.DTOs;

import java.util.UUID;

public class SampleSentenceDTO {
    
    private UUID sentenceId;
    
    // Đã đổi tên: japaneseText -> englishSentence
    private String englishSentence;
    
    private String vietnameseTranslation; // Đã đổi tên thuộc tính trong DTO (từ vietnameseMeaning)

    // Các trường mới
    private String usageFrequency;
    

    // --- Constructors ---
    
    // Constructor không tham số
    public SampleSentenceDTO() {}

    // Constructor đầy đủ tham số
    public SampleSentenceDTO(UUID sentenceId, String englishSentence, String vietnameseTranslation, 
                             String usageFrequency) {
        this.sentenceId = sentenceId;
        this.englishSentence = englishSentence;
        this.vietnameseTranslation = vietnameseTranslation;
        this.usageFrequency = usageFrequency;
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

    public String getUsageFrequency() {
        return usageFrequency;
    }

    public void setUsageFrequency(String usageFrequency) {
        this.usageFrequency = usageFrequency;
    }

}