package com.example.Oboe.DTOs;

import java.util.List;
import java.util.UUID;

public class PhoneticsDTO {
    private UUID phoneticId; 
    
    // 3. Đổi tên thuộc tính: characterName (Ký tự Hán) -> grapheme (Âm vị/Ký tự)
    private String grapheme; 
    
    // 4. Đổi tên thuộc tính: meaning (Nghĩa Hán) -> ipaSymbol (Ký hiệu IPA)
    private String ipaSymbol; 
    
    // 5. Xóa trường strokes (Số nét)
    // private String strokes; 
    
    // 6. Chuẩn hóa tên thuộc tính Java: VietnamesePronunciation -> vietnamesePronunciation
    private String vietnamesePronunciation; 


    // Constructors
    public PhoneticsDTO() {}

    // 7. Cập nhật Constructor
    public PhoneticsDTO(UUID phoneticId, String grapheme, String ipaSymbol, String vietnamesePronunciation) {
        this.phoneticId = phoneticId;
        this.grapheme = grapheme;
        this.ipaSymbol = ipaSymbol;
        this.vietnamesePronunciation = vietnamesePronunciation;
    }

    // Getters and Setters
    
    // 8. Cập nhật Getter/Setter cho Khóa chính
    public UUID getPhoneticId() {
        return phoneticId;
    }

    public void setPhoneticId(UUID phoneticId) {
        this.phoneticId = phoneticId;
    }

    // 9. Cập nhật Getter/Setter cho Grapheme
    public String getGrapheme() {
        return grapheme;
    }

    public void setGrapheme(String grapheme) {
        this.grapheme = grapheme;
    }

    // 10. Cập nhật Getter/Setter cho IPA Symbol
    public String getIpaSymbol() {
        return ipaSymbol;
    }

    public void setIpaSymbol(String ipaSymbol) {
        this.ipaSymbol = ipaSymbol;
    }
    
    // 12. Chuẩn hóa Getter/Setter cho vietnamesePronunciation
    public String getVietnamesePronunciation() {
        return vietnamesePronunciation;
    }

    public void setVietnamesePronunciation(String vietnamesePronunciation) {
        this.vietnamesePronunciation = vietnamesePronunciation;
    }
}
