package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VocabularyRepository extends JpaRepository<Vocabulary, UUID> {
    
    /**
     * Tìm kiếm từ vựng theo từ tiếng Anh, nghĩa tiếng Việt, hoặc phiên âm IPA.
     * Đã loại bỏ vietnamesePronunciation.
     */
    @Query("SELECT v FROM Vocabulary v WHERE LOWER(v.word) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.vietnameseMeaning) LIKE LOWER(CONCAT('%', :keyword, '%')) " + 
            "OR LOWER(v.phoneticIpa) LIKE LOWER(CONCAT('%', :keyword, '%')) " + // Đã thay thế bằng phoneticIpa
            "OR LOWER(v.wordType) LIKE LOWER(CONCAT('%', :keyword, '%')) " + 
            "OR LOWER(v.level) LIKE LOWER(CONCAT('%', :keyword, '%'))") // Thêm tìm kiếm theo level
    List<Vocabulary> searchVocabulary(@Param("keyword") String keyword);

    @Query("""
        SELECT v FROM Vocabulary v
        WHERE v.audioUrl IS NULL OR v.audioUrl = ''
    """)
    List<Vocabulary> findVocabularyWithoutAudio();

}