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

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "audio_url")
    private String audioUrl;

    // Mối quan hệ N-1 (Nhiều câu ví dụ - 1 Từ vựng)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_vocab_id")
    private Vocabulary vocabulary; 

    // Mối quan hệ N-1 (Nhiều câu ví dụ - 1 Thành ngữ)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_idiom_id")
    private Idiom idiom; 

    // Mối quan hệ N-1 (Nhiều câu ví dụ - 1 Ngữ pháp)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_grammar_id")
    private Grammar grammar; 

    // --- 1. Constructors ---
    
    // Constructor không tham số
    public SampleSentence() {}

    // Constructor đầy đủ tham số (Không bao gồm ID và các Entity)
    public SampleSentence(String englishSentence, String vietnameseTranslation, 
                          String difficulty, String audioUrl) {
        this.englishSentence = englishSentence;
        this.vietnameseTranslation = vietnameseTranslation;
        this.difficulty = difficulty;
        this.audioUrl = audioUrl;
    }

    // Constructor đầy đủ tham số (Bao gồm tất cả các trường)
    public SampleSentence(UUID sentenceId, String englishSentence, String vietnameseTranslation, 
                          String difficulty, String audioUrl, Vocabulary vocabulary, 
                          Idiom idiom, Grammar grammar) {
        this.sentenceId = sentenceId;
        this.englishSentence = englishSentence;
        this.vietnameseTranslation = vietnameseTranslation;
        this.difficulty = difficulty;
        this.audioUrl = audioUrl;
        this.vocabulary = vocabulary;
        this.idiom = idiom;
        this.grammar = grammar;
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

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    public Idiom getIdiom() {
        return idiom;
    }

    public void setIdiom(Idiom idiom) {
        this.idiom = idiom;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
    }
}