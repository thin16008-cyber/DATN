package com.example.Oboe.DTOs;

import java.util.UUID;


public class IdiomDTO { 
    
    private UUID id; 
    
    private String englishPhrase; 
    private String vietnameseMeaning;
    
    // Chuẩn hóa tên thuộc tính: phraseCategory -> category
    private String category;
    
    // THÊM MỚI: Audio URL
    private String audioUrl;
    
    // THÊM MỚI: Nguồn gốc/Lịch sử
    private String origin;

    // --- 1. Constructors ---
    
    // Constructor không tham số (thay thế @NoArgsConstructor)
    public IdiomDTO() {}
    
    // Constructor đầy đủ tham số (thay thế @AllArgsConstructor)
    public IdiomDTO(UUID id, String englishPhrase, String vietnameseMeaning, 
                    String category, String audioUrl, String origin) {
        this.id = id;
        this.englishPhrase = englishPhrase;
        this.vietnameseMeaning = vietnameseMeaning;
        this.category = category;
        this.audioUrl = audioUrl;
        this.origin = origin;
    }
    

    // --- 2. Getters và Setters ---
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEnglishPhrase() {
        return englishPhrase;
    }

    public void setEnglishPhrase(String englishPhrase) {
        this.englishPhrase = englishPhrase;
    }

    public String getVietnameseMeaning() {
        return vietnameseMeaning;
    }

    public void setVietnameseMeaning(String vietnameseMeaning) {
        this.vietnameseMeaning = vietnameseMeaning;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}