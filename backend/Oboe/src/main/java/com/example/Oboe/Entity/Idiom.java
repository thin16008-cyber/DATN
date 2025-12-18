package com.example.Oboe.Entity;

import jakarta.persistence.*;
// Đã loại bỏ import Lombok

import java.util.UUID;

@Entity
@Table(name = "idioms") 
public class Idiom { 

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // Đã đổi tên cột Khóa chính từ 'phrase_id' thành 'idiom_id' (đồng bộ với DB)
    @Column(name = "idiom_id", updatable = false, nullable = false) 
    private UUID idiomId; 

    @Column(name = "english_phrase", nullable = false, columnDefinition = "TEXT")
    private String englishPhrase; 

    @Column(name = "vietnamese_meaning", columnDefinition = "TEXT")
    private String vietnameseMeaning; 

    @Column(name = "category") 
    private String category;

    // THÊM MỚI: Audio URL
    @Column(name = "audio_url") 
    private String audioUrl; 

    // THÊM MỚI: Nguồn gốc/Lịch sử
    @Column(name = "origin", columnDefinition = "TEXT")
    private String origin; 

    public Idiom() {}

    // Constructor đầy đủ tham số
    public Idiom(UUID idiomId, String englishPhrase, String vietnameseMeaning, String category, String audioUrl, String origin) {
        this.idiomId = idiomId;
        this.englishPhrase = englishPhrase;
        this.vietnameseMeaning = vietnameseMeaning;
        this.category = category;
        this.audioUrl = audioUrl;
        this.origin = origin;
    }

    // --- 2. Getters và Setters ---

    public UUID getIdiomId() {
        return idiomId;
    }

    public void setIdiomId(UUID idiomId) {
        this.idiomId = idiomId;
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