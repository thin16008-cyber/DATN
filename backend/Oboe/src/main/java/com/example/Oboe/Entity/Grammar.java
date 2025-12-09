package com.example.Oboe.Entity;

import jakarta.persistence.*;
// Đã loại bỏ import Lombok

import java.util.UUID;

@Entity
@Table(name = "grammar") // Đã đổi tên bảng từ 'Gramma' thành 'grammar' trong DB
public class Grammar {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "grammar_id", updatable = false, nullable = false)
    private UUID grammarId; // Khóa chính UUID

    @Column(name = "structure", columnDefinition = "TEXT")
    private String structure; // Cấu trúc ngữ pháp

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation; // Giải thích ngữ pháp

    @Column(name = "grammar_type")
    private String grammarType; // Loại ngữ pháp (e.g., Conditionals, Tenses)
    
    // THÊM MỚI: Nội dung chi tiết/mở rộng
    @Column(name = "detail_content", columnDefinition = "TEXT")
    private String detailContent;

    // THÊM MỚI: Tag chủ đề để dễ phân loại
    @Column(name = "topic_tag")
    private String topicTag;


    // --- 1. Constructors ---
    
    // Constructor không tham số
    public Grammar() {}

    // Constructor đầy đủ tham số
    public Grammar(UUID grammarId, String structure, String explanation, String grammarType, 
                   String detailContent, String topicTag) {
        this.grammarId = grammarId;
        this.structure = structure;
        this.explanation = explanation;
        this.grammarType = grammarType;
        this.detailContent = detailContent;
        this.topicTag = topicTag;
    }


    // --- 2. Getters và Setters ---

    public UUID getGrammarId() {
        return grammarId;
    }

    public void setGrammarId(UUID grammarId) {
        this.grammarId = grammarId;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getGrammarType() {
        return grammarType;
    }

    public void setGrammarType(String grammarType) {
        this.grammarType = grammarType;
    }

    public String getDetailContent() {
        return detailContent;
    }

    public void setDetailContent(String detailContent) {
        this.detailContent = detailContent;
    }

    public String getTopicTag() {
        return topicTag;
    }

    public void setTopicTag(String topicTag) {
        this.topicTag = topicTag;
    }
}