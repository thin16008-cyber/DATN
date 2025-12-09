package com.example.Oboe.DTOs;

import java.util.UUID;
// KHÔNG cần import Lombok

public class GrammarDTO {
    
    private UUID grammarId; 
    
    // Giữ nguyên tên 'structure' để đồng bộ với Service đang gọi setStructure()
    private String structure; 
    
    private String explanation;
    private String example;
    
    // Trường mới thay thế grammarType
    private String tenseAspect; 
    
    private String detailContent;
    private String topicTag;


    // --- 1. Constructors ---
    
    // Constructor không tham số (NoArgsConstructor)
    public GrammarDTO() {}
    
    // Constructor đầy đủ tham số (AllArgsConstructor)
    public GrammarDTO(UUID grammarId, String structure, String explanation, String example, String tenseAspect) {
        this.grammarId = grammarId;
        this.structure = structure;
        this.explanation = explanation;
        this.example = example;
        this.tenseAspect = tenseAspect;
    }


    // --- 2. Getters và Setters ---
    
    // Getter/Setter cho grammarId
    public UUID getGrammarId() {
        return grammarId;
    }

    public void setGrammarId(UUID grammarId) {
        this.grammarId = grammarId;
    }

    // Getter/Setter cho structure
    // Đảm bảo method này tồn tại để khắc phục lỗi "cannot find symbol: method getStructure()"
    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    // Getter/Setter cho explanation
    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    // Getter/Setter cho example
    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
    
    // Getter/Setter cho tenseAspect
    public String getTenseAspect() {
        return tenseAspect;
    }

    public void setTenseAspect(String tenseAspect) {
        this.tenseAspect = tenseAspect;
    }
    // Getter
    public String getDetailContent() {
        return detailContent;
    }

    // Setter
    public void setDetailContent(String detailContent) {
        this.detailContent = detailContent;
    }
    // Getter
    public String getTopicTag() {
        return topicTag;
    }

    // Setter
    public void setTopicTag(String detailContent) {
        this.topicTag = topicTag;
    }
}