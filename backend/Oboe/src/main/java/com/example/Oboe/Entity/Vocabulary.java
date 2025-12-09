package com.example.Oboe.Entity;

import jakarta.persistence.*;
// Đã loại bỏ import Lombok
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "vocabulary") 
public class Vocabulary {

    // Khóa chính: Giữ nguyên UUID
    @Id
    @UuidGenerator 
    @Column(name = "vocalb_id", updatable = false, nullable = false) 
    private UUID vocabularyId;

    @Column(name = "words", nullable = false)
    private String words; // Từ vựng Tiếng Anh

    @Column(name = "vietnamese_meaning", columnDefinition = "TEXT") 
    private String vietnameseMeaning; // Nghĩa Tiếng Việt

    @Column(name = "word_type")
    private String wordType; // Loại từ: noun, verb, adj...
    
    // Phiên âm IPA được lưu trực tiếp
    @Column(name = "phonetic_ipa")
    private String phoneticIpa; 

    // Audio URL được lưu trực tiếp (Cho phép NULL)
    @Column(name = "audio_url") 
    private String audioUrl; 

    // Mức độ (Level)
    @Column(name = "level")
    private String level; 

    // Đồng nghĩa 
    @Column(name = "synonyms", columnDefinition = "TEXT") 
    private String synonyms; 

    // Trái nghĩa 
    @Column(name = "antonyms", columnDefinition = "TEXT") 
    private String antonyms;


    // --- 1. Constructors ---
    
    // Constructor không tham số
    public Vocabulary() {}

    // Constructor đầy đủ tham số
    public Vocabulary(UUID vocabularyId, String words, String vietnameseMeaning, String wordType, 
                      String phoneticIpa, String audioUrl, String level, String synonyms, String antonyms) {
        this.vocabularyId = vocabularyId;
        this.words = words;
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

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
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