package com.example.Oboe.Entity;

import java.util.UUID;

import jakarta.persistence.*;
// Đã loại bỏ import Lombok

@Entity
@Table(name = "sample_sentences")
public class SampleSentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sentence_id")
    private UUID sentenceId;

    @Column(name = "english_sentence", columnDefinition = "TEXT", nullable = false)
    private String englishSentence;

    @Column(name = "vietnamese_translation", columnDefinition = "TEXT", nullable = false)
    private String vietnameseTranslation;

    @Column(name = "topic_Tag")
    private String topicTag;

    @Column(name = "usage_frequency")
    private String usageFrequency;




   

    // --- 1. Constructors ---
    
    // Constructor không tham số
    public SampleSentence() {}

    // Constructor đầy đủ tham số (Không bao gồm ID và các Entity)
    public SampleSentence(String englishSentence, String vietnameseTranslation, String topicTag,
                          String usageFrequency) {
        this.englishSentence = englishSentence;
        this.vietnameseTranslation = vietnameseTranslation;
        this.topicTag = topicTag;
        this.usageFrequency = usageFrequency;
    }

    // Constructor đầy đủ tham số (Bao gồm tất cả các trường)
    public SampleSentence(UUID sentenceId, String englishSentence, String vietnameseTranslation, String topicTag,
                          String usageFrequency) {
        this.sentenceId = sentenceId;
        this.englishSentence = englishSentence;
        this.vietnameseTranslation = vietnameseTranslation;
        this.topicTag = topicTag;
        this.usageFrequency = usageFrequency;
    }


    // --- 2. Getters và Setters ---

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
    
    public String getTopicTag() {
        return topicTag;
    }
    public void setTopicTag(String topicTag) {
        this.topicTag = topicTag;
    }

    public String getUsageFrequency() {
        return usageFrequency;
    }

    public void setUsageFrequency(String usageFrequency) {
        this.usageFrequency = usageFrequency;
    }

    
}