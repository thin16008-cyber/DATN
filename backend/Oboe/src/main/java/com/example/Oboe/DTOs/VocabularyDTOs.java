package com.example.Oboe.DTOs;

import java.util.UUID;
// Đã loại bỏ import Lombok

public class VocabularyDTOs {

    private UUID vocabularyId; 
    
    private String word;
    
    // 1. Chuẩn hóa tên thuộc tính
    private String vietnameseMeaning; 
    
    private String wordType;
    
    // --- Các thuộc tính mới cho hệ thống Tiếng Anh ---
    
    // 2. THÊM MỚI: Ký hiệu phiên âm quốc tế
    private String phoneticIpa; 
    
    // 3. THÊM MỚI: Đường dẫn file phát âm
    private String audioUrl;
    
    // 4. THÊM MỚI: Cấp độ (A1, B2,...)
    private String level;
    
    // 5. THÊM MỚI: Từ đồng nghĩa
    private String synonyms; 
    
    // 6. THÊM MỚI: Từ trái nghĩa
    private String antonyms; 


    // --- 1. Constructors ---
    
    // Constructor không tham số (thay thế @NoArgsConstructor)
    public VocabularyDTOs() {}

    // Constructor đầy đủ tham số (thay thế @AllArgsConstructor)
    public VocabularyDTOs(UUID vocabularyId, String word, String vietnameseMeaning, 
                          String wordType, String phoneticIpa, String audioUrl, 
                          String level, String synonyms, String antonyms) {
        this.vocabularyId = vocabularyId;
        this.word = word;
        this.vietnameseMeaning = vietnameseMeaning;
        this.wordType = wordType;
        this.phoneticIpa = phoneticIpa;
        this.audioUrl = audioUrl;
        this.level = level;
        this.synonyms = synonyms;
        this.antonyms = antonyms;
    }


    // --- 2. Getters và Setters ---

    public UUID getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(UUID vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getVietnameseMeaning() {
        return vietnameseMeaning;
    }

    public void setVietnameseMeaning(String vietnameseMeaning) {
        this.vietnameseMeaning = vietnameseMeaning;
    }

    public String getWordType() {
        return wordType;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

    public String getPhoneticIpa() {
        return phoneticIpa;
    }

    public void setPhoneticIpa(String phoneticIpa) {
        this.phoneticIpa = phoneticIpa;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    public String getAntonyms() {
        return antonyms;
    }

    public void setAntonyms(String antonyms) {
        this.antonyms = antonyms;
    }
}