package com.example.Oboe.DTOs;

import java.util.UUID;
import java.time.LocalDate;
// Đã loại bỏ import Lombok

public class FavoritesDTO {
    
    // Chuẩn hóa tên thuộc tính: FavoritesID -> favoritesId
    private UUID favoritesId; 
    private String title;
    private String content;
    // Chuẩn hóa tên thuộc tính: favoritesAt -> favoritedAt
    private LocalDate favoritedAt; 
    private UUID userId;
    
    // Chuẩn hóa tên ID: grammaId -> grammarId
    private UUID grammarId; 
    
    // Đổi tên ID: phraseId -> idiomId (Thay thế PhoneticID đã xóa)
    private UUID idiomId; 
    
    // Chuẩn hóa tên thuộc tính: vocabularyId
    private UUID vocabularyId; 
    
    // Trường để xác định loại nội dung (grammar, vocabulary, idiom)
    private String type;
    

    // --- 1. Constructors ---
    
    // Constructor không tham số (thay thế @NoArgsConstructor)
    public FavoritesDTO() {}
    
    // Constructor đầy đủ tham số (thay thế @AllArgsConstructor)
    // Lưu ý: Thêm 'type' vào constructor nếu bạn muốn khởi tạo nó ngay lập tức
    // Nếu 'type' chỉ được set sau đó, nó có thể bị loại khỏi constructor này
    public FavoritesDTO(UUID favoritesId, String title, String content, LocalDate favoritedAt, UUID userId, 
                        UUID grammarId, UUID idiomId, UUID vocabularyId, String type) {
        this.favoritesId = favoritesId;
        this.title = title;
        this.content = content;
        this.favoritedAt = favoritedAt;
        this.userId = userId;
        this.grammarId = grammarId;
        this.idiomId = idiomId;
        this.vocabularyId = vocabularyId;
        this.type = type;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getGrammarId() {
        return grammarId;
    }

    public void setGrammarId(UUID grammarId) {
        this.grammarId = grammarId;
    }

    public UUID getIdiomId() {
        return idiomId;
    }

    public void setIdiomId(UUID idiomId) {
        this.idiomId = idiomId;
    }

    public UUID getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(UUID vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}