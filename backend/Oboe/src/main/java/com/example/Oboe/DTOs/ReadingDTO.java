package com.example.Oboe.DTOs;

import java.util.UUID;

public class ReadingDTO {
    private UUID readingId; 
    
    private String readingText; // Nội dung cách đọc (VD: /pɪg/)
    
    // 2. Đổi tên: readingType (VD: On'yomi, Kun'yomi) -> variationType (VD: US/UK pronunciation)
    private String variationType; 
    
    private String ownerType; // Loại Entity sở hữu (VD: PHONETIC, VOCABULARY, GRAMMAR)
    private UUID ownerId;

    // Constructors
    public ReadingDTO() {}

    // 3. Cập nhật Constructor
    public ReadingDTO(UUID readingId, String readingText, String variationType, String ownerType, UUID ownerId) {
        this.readingId = readingId;
        this.readingText = readingText;
        this.variationType = variationType;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
    }

    // --- Getters and Setters Đã Chuẩn hóa ---
    
    // Getter/Setter cho readingId
    public UUID getReadingId() {
        return readingId;
    }

    public void setReadingId(UUID readingId) {
        this.readingId = readingId;
    }

    public String getReadingText() {
        return readingText;
    }

    public void setReadingText(String readingText) {
        this.readingText = readingText;
    }

    // Getter/Setter cho variationType
    public String getVariationType() {
        return variationType;
    }

    public void setVariationType(String variationType) {
        this.variationType = variationType;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
}
