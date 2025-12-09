package com.example.Oboe.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "favorites") // Chuẩn hóa tên bảng thành chữ thường
public class Favorites {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    // Chuẩn hóa tên cột Khóa chính: favorites_id
    @Column(name = "favorites_id", updatable = false, nullable = false) 
    private UUID favoritesId; // Chuẩn hóa tên thuộc tính Java (camelCase)

    private String title;
    private String content;

    // Chuẩn hóa tên cột DB: favorited_at
    @Column(name = "favorited_at") 
    private LocalDate favoritedAt = LocalDate.now(); 

    // --- Mối quan hệ Khóa ngoại ---

    // 1. User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 2. Grammar
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="grammar_id") 
    private Grammar grammar;

    // 3. Vocabulary
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="vocabulary_id") 
    private Vocabulary vocabulary;
    
    // 4. Idiom
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idiom_id") 
    private Idiom idiom; 
    
    // 5. FlashCards
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="card_id") 
    private FlashCards flashCards;


    // --- 1. Constructors ---
    
    // Constructor không tham số (thay thế @NoArgsConstructor)
    public Favorites() {}

    // Constructor đầy đủ tham số (thay thế @AllArgsConstructor)
    public Favorites(UUID favoritesId, String title, String content, LocalDate favoritedAt, 
                     User user, Grammar grammar, Vocabulary vocabulary, Idiom idiom, FlashCards flashCards) {
        this.favoritesId = favoritesId;
        this.title = title;
        this.content = content;
        this.favoritedAt = favoritedAt;
        this.user = user;
        this.grammar = grammar;
        this.vocabulary = vocabulary;
        this.idiom = idiom;
        this.flashCards = flashCards;
    }


    // --- 2. Getters và Setters ---

    public UUID getFavoritesId() {
        return favoritesId;
    }

    public void setFavoritesId(UUID favoritesId) {
        this.favoritesId = favoritesId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getFavoritedAt() {
        return favoritedAt;
    }

    public void setFavoritedAt(LocalDate favoritedAt) {
        this.favoritedAt = favoritedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
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

    public FlashCards getFlashCards() {
        return flashCards;
    }

    public void setFlashCards(FlashCards flashCards) {
        this.flashCards = flashCards;
    }
}