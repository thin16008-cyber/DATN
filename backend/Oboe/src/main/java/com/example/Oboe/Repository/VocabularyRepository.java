package com.example.Oboe.Repository;

import com.example.Oboe.Entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VocabularyRepository extends JpaRepository<Vocabulary, UUID> {
    

    @Query("SELECT v FROM Vocabulary v WHERE LOWER(v.word) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.vietnameseMeaning) LIKE LOWER(CONCAT('%', :keyword, '%')) " + 
            "OR LOWER(v.phoneticIpa) LIKE LOWER(CONCAT('%', :keyword, '%')) " + 
            "OR LOWER(v.wordType) LIKE LOWER(CONCAT('%', :keyword, '%')) " + 
            "OR LOWER(v.level) LIKE LOWER(CONCAT('%', :keyword, '%'))") 
    List<Vocabulary> searchVocabulary(@Param("keyword") String keyword);

    @Query("""
        SELECT v FROM Vocabulary v
        WHERE v.audioUrl IS NULL OR v.audioUrl = ''
    """)
    List<Vocabulary> findVocabularyWithoutAudio();

}